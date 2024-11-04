package org.example.configs;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.example.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
	@Autowired
	private HandlerExceptionResolver handlerExceptionResolver;

	@Autowired
	private JwtServiceImpl jwtService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
	{
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer "))
		{
			filterChain.doFilter(request, response);
			return;
		}

		try
		{
			final String jwt = authHeader.substring(7);
			final String username = jwtService.extractUsername(jwt);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (username == null || authentication != null)
			{
				filterChain.doFilter(request, response);
				return;
			}

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (!jwtService.isTokenValid(jwt, userDetails))
			{
				filterChain.doFilter(request, response);
				return;
			}

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
			);

			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);

			filterChain.doFilter(request, response);
		}
		catch (Exception e)
		{
			handlerExceptionResolver.resolveException(request, response, null, e);
		}
	}
}
