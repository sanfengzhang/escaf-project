package com.esc.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class RestRequestTest
{

	@SuppressWarnings("deprecation")
	@Test
	public void setup() throws IOException, URISyntaxException
	{

		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();

		String params = URLDecoder.decode(
				"{%22query%22:{%22match%22:{%22title%22:{%22query%22:%22quick%20brown%20%20fox%20%22,%22type%22:%22phrase%22,%22slop%22:3}}}}");
		System.out.println(params);
		ClientHttpRequest clientHttpRequest = simpleClientHttpRequestFactory
				.createRequest(new URI("http://127.0.0.1:9200/my_index/_search?"), HttpMethod.GET);

		ClientHttpResponse clientHttpResponse = clientHttpRequest.execute();
		System.out.println(IOUtils.toString(clientHttpResponse.getBody()));
	}

	@Test
	public void readTest() throws FileNotFoundException, IOException
	{
		File file = new File(
				"F:\\GitHubRepository\\esc-samples\\target");
		System.out.println(file.exists());
		System.out.println(IOUtils.toString(new FileInputStream(file)).length());
		;
	}

	@Test
	public void testString()
	{
		String strs[] = StringUtils.split("sadasdfgfdf/sddddddd", "/s");
		System.out.println(strs[0]);
		System.out.println(strs[1]);

	}

}
