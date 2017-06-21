package com.escframework.esauth;

import org.elasticsearch.common.logging.LoggerMessageFormat;

public class AuthException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public AuthException(String msg)
	{
		super(msg);
	}

	public AuthException(String msg, Object... args)
	{
		super(LoggerMessageFormat.format(msg, args));
	}

	public AuthException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	public AuthException(String msg, Throwable cause, Object... args)
	{
		super(LoggerMessageFormat.format(msg, args), cause);
	}

}
