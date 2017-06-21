package com.escaf.esservice.search;

import java.util.Map;

public class EscafElasticException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	private Map<String, String> failedDocuments;

	public EscafElasticException(String message)
	{
		super(message);
	}

	public EscafElasticException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public EscafElasticException(String message, Throwable cause, Map<String, String> failedDocuments)
	{
		super(message, cause);
		this.failedDocuments = failedDocuments;
	}

	public EscafElasticException(String message, Map<String, String> failedDocuments)
	{
		super(message);
		this.failedDocuments = failedDocuments;
	}

	public Map<String, String> getFailedDocuments()
	{
		return failedDocuments;
	}

}
