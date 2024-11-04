package org.example.utils.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServiceException extends RuntimeException
{

	private ServiceExceptionType serviceExceptionTypeEnum;

	private String message;

	public ServiceException(ServiceExceptionType serviceExceptionTypeEnum, String message)
	{
		setServiceExceptionTypeEnum(serviceExceptionTypeEnum);
		setMessage(message);
	}

	public ServiceException(String message)
	{
		super(message);
	}
}
