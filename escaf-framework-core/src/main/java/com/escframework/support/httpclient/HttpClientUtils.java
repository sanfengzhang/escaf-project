package com.escframework.support.httpclient;

/**
 * 
 * 支持httpclient请求
 * @author hal
 * 
 *
 */
public interface HttpClientUtils
{

	public Response get(String requestURI);

	public Response post(String requestURI, String requestParams);

	public Response download(String requestURI);
}
