//$Id$
package com.srs.core;

import java.security.SecureRandom;
import java.util.Set;

import com.srs.exception.ComponentException;
import com.srs.exception.ErrorCode.BookingErrorCode;
import com.srs.exception.ErrorCode.ComponentErrorCode;
import com.srs.exception.TicketBookingException;
import com.srs.model.IPaymentService;
import com.srs.model.ITicketBookingService;
import com.srs.model.PaymentDetail;
import com.srs.query.MySQLQueryUtil;
import com.srs.query.RedisQueryUtil;

public class TicketBookingService implements ITicketBookingService {

	@Override
	public void markSeat(long userId, long showId, Set<Long> seatIds)
			throws TicketBookingException, ComponentException {
		if (seatIds.isEmpty()) {
			throw new TicketBookingException(BookingErrorCode.INVALID_INPUT);
		}
		int alreadyBookedSeatCount = MySQLQueryUtil.getAlreadyBookedSeatCount(userId);
		if (alreadyBookedSeatCount + seatIds.size() > 6) {
			throw new TicketBookingException(BookingErrorCode.SEAT_LIMIT_EXCEEDED);
		}
		if (MySQLQueryUtil.isAnyOneSeatAlreadybooked(showId, seatIds)) {
			throw new TicketBookingException(BookingErrorCode.ONE_OR_MORE_SEATS_IS_ALREADY_BOOKED);
		}
		RedisQueryUtil.markSeatsForBooking(userId, showId, seatIds);
	}

	@Override
	public void bookSeat(long userId, long showId, PaymentDetail paymentDetail, Set<Long> seatIds)
			throws TicketBookingException, ComponentException {
		if (seatIds.isEmpty()) {
			throw new TicketBookingException(BookingErrorCode.INVALID_INPUT);
		}
		if (MySQLQueryUtil.isAnyOneSeatAlreadybooked(showId, seatIds)) {
			throw new TicketBookingException(BookingErrorCode.ONE_OR_MORE_SEATS_IS_ALREADY_BOOKED);
		}
		Set<Long> reservedSeats = RedisQueryUtil.getReservedSeats(userId, showId);
		if (reservedSeats.size() != seatIds.size()) {
			throw new TicketBookingException(BookingErrorCode.MISMATCH_OR_EXPIRED);
		}
		for (long reservedSeat : reservedSeats) {
			if (!seatIds.contains(reservedSeat)) {
				throw new TicketBookingException(BookingErrorCode.MISMATCH_OR_EXPIRED);
			}
		}
		if (RedisQueryUtil.isAnyOneSeatBookingIsInProgress(seatIds)) {
			throw new TicketBookingException(BookingErrorCode.ONE_OR_MORE_SEATS_IS_ALREADY_BOOKED);
		}
		if (RedisQueryUtil.isEligibleForBooking(seatIds)) {
			RedisQueryUtil.lockForConflictResolution(seatIds);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new ComponentException(ComponentErrorCode.INTERRUPTED);
			}
			if (RedisQueryUtil.isEligibleForBooking(seatIds)) {
				try {
					RedisQueryUtil.markBookingInProgress(seatIds, userId);
					IPaymentService paymentService = new PaymentService();
					if (!paymentService.processPayment(paymentDetail)) {
						throw new TicketBookingException(BookingErrorCode.PAYMENT_FAILURE);
					} else {
						// payment success
						MySQLQueryUtil.insertBookedSeats(userId, showId, seatIds, new SecureRandom().nextLong());
					}
					RedisQueryUtil.unMarkBookingInProgress(seatIds);
				} catch (Exception e) {
					RedisQueryUtil.unMarkBookingInProgress(seatIds);
					throw e;
				}
			} else {
				throw new TicketBookingException(BookingErrorCode.BOOKING_FAILED_BY_PRIORITY);
			}
		} else {
			throw new TicketBookingException(BookingErrorCode.BOOKING_FAILED_BY_PRIORITY);
		}
	}
}
