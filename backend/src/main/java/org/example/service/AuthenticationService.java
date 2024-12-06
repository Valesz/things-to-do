package org.example.service;

import org.example.model.User;

public interface AuthenticationService
{
	User authenticate(String username, String password);
}
