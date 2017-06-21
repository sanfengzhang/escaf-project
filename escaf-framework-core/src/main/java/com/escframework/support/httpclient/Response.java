package com.escframework.support.httpclient;

public class Response
{

	private int responseCode;

	private String errMsg;

	private String responseMsg;

	public int getResponseCode()
	{
		return responseCode;
	}

	public void setResponseCode(int responseCode)
	{
		this.responseCode = responseCode;
	}

	public String getErrMsg()
	{
		return errMsg;
	}

	public void setErrMsg(String errMsg)
	{
		this.errMsg = errMsg;
	}

	public String getResponseMsg()
	{
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg)
	{
		this.responseMsg = responseMsg;
	}

}
