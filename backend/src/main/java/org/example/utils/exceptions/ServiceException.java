package org.example.utils.exceptions;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException
{

	private final ServiceExceptionType serviceExceptionTypeEnum;

	private final String message;

	public ServiceException(ServiceExceptionType serviceExceptionTypeEnum, String message)
	{
		super(message);
		this.serviceExceptionTypeEnum = serviceExceptionTypeEnum;
		this.message = message;
	}
}
