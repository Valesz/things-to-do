package org.example.utils.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import java.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleSecurityException(Exception ex) {


		if (ex instanceof BadCredentialsException) {
			return new ResponseEntity<>("The username or password is incorrect", HttpStatus.UNAUTHORIZED);
		}

		if (ex instanceof AccountStatusException) {
			return new ResponseEntity<>("The account is locked", HttpStatus.UNAUTHORIZED);
		}

		if (ex instanceof AccessDeniedException) {
			return new ResponseEntity<>("You are not authorized to access this", HttpStatus.FORBIDDEN);
		}

		if (ex instanceof SignatureException) {
			return new ResponseEntity<>("The signature is invalid", HttpStatus.UNAUTHORIZED);
		}

		if (ex instanceof ExpiredJwtException) {
			return new ResponseEntity<>("The JWT token has expired", HttpStatus.UNAUTHORIZED);
		}

		ex.printStackTrace();
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
