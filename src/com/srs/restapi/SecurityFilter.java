package com.srs.restapi;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.srs.query.MySQLQueryUtil;
import com.srs.util.AuthorizationUtil;

@Provider
public class SecurityFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		if (context.getHeaders().containsKey(AuthorizationUtil.AUTH_HEADER)) {
			String authHeader = context.getHeaders().getFirst(AuthorizationUtil.AUTH_HEADER);
			String[] authParams = AuthorizationUtil.extractEmailIdAndPassword(authHeader);
			try {
				if (authParams.length == 2 && MySQLQueryUtil.isValidUser(authParams[0], authParams[1])) {
					return;
				}
			} catch (Exception e) {
				Response authFailedResponse = Response.status(500).entity("Internal Server error").build();
				context.abortWith(authFailedResponse);
				return;
			}
		}
		Response authFailedResponse = Response.status(401).entity("Please do basic authentication").build();
		context.abortWith(authFailedResponse);
	}

}
