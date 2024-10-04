package org.example.utils;

import lombok.Data;

@Data
public class HttpErrorResponseForTests
{

	private int status;

	private String error;

	private String message;
}
