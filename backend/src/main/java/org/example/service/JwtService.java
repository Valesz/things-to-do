package org.example.service;

import io.jsonwebtoken.Claims;
import java.security.Key;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService
{
	String extractUsername(String token);

	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);

	String generateToken(UserDetails userDetails);

	String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isTokenExpired(String token);

	Claims extractAllClaims(String token);

	Key getSignInKey();
}
