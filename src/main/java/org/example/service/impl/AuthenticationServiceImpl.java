package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	public User authenticate(String username, String password)
	{
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		return userRepository.findByUsername(username).orElseThrow();
	}
}
