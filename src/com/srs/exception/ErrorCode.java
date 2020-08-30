package com.srs.exception;

public interface ErrorCode {
	
	public static enum BookingErrorCode implements ErrorCode {
        SEAT_LIMIT_EXCEEDED,
        INVALID_INPUT,
        MISMATCH_OR_EXPIRED,
        ONE_OR_MORE_SEATS_IS_ALREADY_BOOKED,
        PAYMENT_FAILURE,
        BOOKING_FAILED_BY_PRIORITY,
        ;
	}
	
	public static enum ComponentErrorCode implements ErrorCode {
		ERROR_LOADING_CONFIGURATION_FILE,
		CONFIGURATION_KEY_NOT_LOADED,
		MYSQL_CONNECTOR_NOT_LOADED,
		ERROR_INITIALIZING_MYSQL_CONNECTION,
		ERROR_IN_SQL_QUERY,
		INTERRUPTED
	}

}
