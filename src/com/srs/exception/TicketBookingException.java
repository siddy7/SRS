//$Id$
package com.srs.exception;

import com.srs.exception.ErrorCode.BookingErrorCode;

public class TicketBookingException extends Exception {

	private static final long serialVersionUID = 1L;

	BookingErrorCode errorCode;

	public TicketBookingException(BookingErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public TicketBookingException(BookingErrorCode errorCode, Throwable t) {
		super(t);
		this.errorCode = errorCode;
	}

	public BookingErrorCode getErrorCode() {
		return errorCode;
	}

}
