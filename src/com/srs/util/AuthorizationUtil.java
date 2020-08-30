package com.srs.util;

import java.util.Base64;

public class AuthorizationUtil {

	public static final String AUTH_HEADER = "Authorization";
	public static final String BASIC_PREFIX = "Basic ";

	private AuthorizationUtil() {

	}

	public static String[] extractEmailIdAndPassword(String authorizationHeader) {
		authorizationHeader = authorizationHeader.replace(BASIC_PREFIX, "");
		String decodedAuthValue = new String(Base64.getDecoder().decode(authorizationHeader.getBytes()));
		return decodedAuthValue.split(":");
	}

}
