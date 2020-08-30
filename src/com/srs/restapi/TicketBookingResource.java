package com.srs.restapi;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.srs.core.TicketBookingService;
import com.srs.exception.ComponentException;
import com.srs.exception.TicketBookingException;
import com.srs.model.ITicketBookingService;
import com.srs.query.MySQLQueryUtil;
import com.srs.util.AuthorizationUtil;

@Path("ticket")
public class TicketBookingResource {

	@GET
	@Path("/markSeats")
	public Response markSeats(@Context HttpHeaders httpheaders, @QueryParam("seatIds") String seatIds,
			@QueryParam("showId") long showId) {
		try {
			Logger.getGlobal().info(System.getProperty("catalina.base"));
			if (seatIds == null) {
				return Response.status(400).entity("invalid input").build();
			}
			String[] splits = seatIds.split(",");
			Set<Long> seatIdSet = new HashSet<Long>();
			for (String seatId : splits) {
				seatIdSet.add(Long.parseLong(seatId));
			}
			ITicketBookingService ticketBookingService = new TicketBookingService();
			String authHeader = httpheaders.getHeaderString(AuthorizationUtil.AUTH_HEADER);
			String[] extractedData = AuthorizationUtil.extractEmailIdAndPassword(authHeader);
			long userId = MySQLQueryUtil.getUserId(extractedData[0]);
			ticketBookingService.markSeat(userId, showId, seatIdSet);
			return Response.status(200).entity("seats marked for booking successfully").build();
		} catch (TicketBookingException t) {
			return Response.status(400).entity(t.getErrorCode().name().toLowerCase()).build();
		} catch (ComponentException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getErrorCode().name(), e);
			return Response.status(400).entity("internal error").build();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uncaught exception", e);
			return Response.status(400).entity("internal error").build();
		}
	}

	@GET
	@Path("/bookSeats")
	public Response bookSeats(@Context HttpHeaders httpheaders, @QueryParam("seatIds") String seatIds,
			@QueryParam("showId") long showId) {
		try {
			if (seatIds == null) {
				return Response.status(400).entity("invalid input").build();
			}
			String[] splits = seatIds.split(",");
			Set<Long> seatIdSet = new HashSet<Long>();
			for (String seatId : splits) {
				seatIdSet.add(Long.parseLong(seatId));
			}
			ITicketBookingService ticketBookingService = new TicketBookingService();
			String authHeader = httpheaders.getHeaderString(AuthorizationUtil.AUTH_HEADER);
			String[] extractedData = AuthorizationUtil.extractEmailIdAndPassword(authHeader);
			long userId = MySQLQueryUtil.getUserId(extractedData[0]);
			ticketBookingService.bookSeat(userId, showId, null, seatIdSet);
			return Response.status(200).entity("Seats booked successfully").build();
		} catch (TicketBookingException t) {
			return Response.status(400).entity(t.getErrorCode().name().toLowerCase()).build();
		} catch (ComponentException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getErrorCode().name(), e);
			return Response.status(400).entity("internal error").build();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uncaught exception", e);
			return Response.status(400).entity("internal error").build();
		}
	}

}
