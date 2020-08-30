//$Id$
package com.srs.model;

import java.util.Set;

import com.srs.exception.ComponentException;
import com.srs.exception.TicketBookingException;

public interface ITicketBookingService {

    void markSeat(long userId, long showId, Set<Long> seatId) throws TicketBookingException, ComponentException;

    void bookSeat(long userId, long showId, PaymentDetail paymentDetail, Set<Long> seatId) throws TicketBookingException, ComponentException;


}
