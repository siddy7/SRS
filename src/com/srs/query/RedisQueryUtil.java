//$Id$
package com.srs.query;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.srs.connection.RedisConnectionHandler;
import com.srs.constants.ConfigurationHandler;
import com.srs.constants.ConfigurationHandler.ConfigKey;
import com.srs.exception.ComponentException;
import com.srs.constants.RedisConstants;

public class RedisQueryUtil {

	private RedisQueryUtil() {

	}

	private static String constructSeatBookingKey(long userId, long showId) {
		return RedisConstants.SEAT_LOCK_PREFIX + RedisConstants.REDIS_KEY_DELIMITTER + userId
				+ RedisConstants.REDIS_KEY_DELIMITTER + showId;
	}

	private static String constructInBookingProgressKey(long seatId) {
		return RedisConstants.SEAT_IN_BOOKING_PROGRESS_PREFIX + RedisConstants.REDIS_KEY_DELIMITTER + seatId;
	}

	private static String constructConflictResolutionKey(long seatId) {
		return RedisConstants.CONFLICT_RESOLUTION_PREFIX + RedisConstants.REDIS_KEY_DELIMITTER + seatId;
	}

	public static void markSeatsForBooking(long userId, long showId, Set<Long> seatIds) throws ComponentException {
		String key = constructSeatBookingKey(userId, showId);
		for (long seatId : seatIds) {
			RedisConnectionHandler.getInstance().getJedisConnection().rpush(key, seatId + "");
		}
		RedisConnectionHandler.getInstance().getJedisConnection().expire(key,
				ConfigurationHandler.getInstance().getIntValue(ConfigKey.SEAT_PAYMENT_THRESHOLD_TIME_SEC));
	}

	public static Set<Long> getReservedSeats(long userId, long showId) throws ComponentException {
		String key = constructSeatBookingKey(userId, showId);
		List<String> storedList = RedisConnectionHandler.getInstance().getJedisConnection().lrange(key, 0, -1);
		return storedList.stream().map(id -> Long.parseLong(id)).collect(Collectors.toSet());
	}

	public static boolean isAnyOneSeatBookingIsInProgress(Set<Long> seatIds) throws ComponentException {
		for (long seatId : seatIds) {
			String inProgressKey = constructInBookingProgressKey(seatId);
			if (RedisConnectionHandler.getInstance().getJedisConnection().get(inProgressKey) != null) {
				return true;
			}
		}
		return false;
	}

	public static void markBookingInProgress(Set<Long> seatIds, long userId) throws ComponentException {
		for (long seatId : seatIds) {
			String inProgressKey = constructInBookingProgressKey(seatId);
			RedisConnectionHandler.getInstance().getJedisConnection().set(inProgressKey, userId + "");
		}
	}

	public static void unMarkBookingInProgress(Set<Long> seatIds) throws ComponentException {
		String[] keys = seatIds.stream().map(seat -> constructInBookingProgressKey(seat)).collect(Collectors.toList())
				.toArray(new String[0]);
		RedisConnectionHandler.getInstance().getJedisConnection().del(keys);
	}

	public static boolean isEligibleForBooking(Set<Long> seatIds) throws ComponentException {
		int seatCount = 0;
		for (long seatId : seatIds) {
			String conflictResolutionKey = constructConflictResolutionKey(seatId);
			String value = RedisConnectionHandler.getInstance().getJedisConnection().get(conflictResolutionKey);
			if (value != null) {
				int count = Integer.parseInt(value);
				if (count > seatCount) {
					seatCount = count;
				}
			}
		}
		if (seatCount == seatIds.size()) {
			return new SecureRandom().nextBoolean();
		}
		return seatCount < seatIds.size();
	}

	public static void lockForConflictResolution(Set<Long> seatIds) throws ComponentException {
		for (long seatId : seatIds) {
			String conflictResolutionKey = constructConflictResolutionKey(seatId);
			RedisConnectionHandler.getInstance().getJedisConnection().set(conflictResolutionKey, seatIds.size() + "");
			RedisConnectionHandler.getInstance().getJedisConnection().expire(conflictResolutionKey, 1);
		}
	}
}
