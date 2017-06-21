package com.esctest.sso;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SsoTokenController
{
	


	@RequestMapping(value = "ssoToken.action", method = RequestMethod.POST)
	@ResponseBody
	public String requestToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException, URISyntaxException
	{
		// ---------先生成token将token发送给需要认证的APP-----

		String token = "test-token-1234567";

		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		ClientHttpRequest clientHttpRequest = simpleClientHttpRequestFactory
				.createRequest(new URI("http://192.168.1.76:9200/_esc/estoken?x-esauth-token=" + token), HttpMethod.PUT);
		clientHttpRequest.execute();
		
		Cookie cookie = new Cookie("x-esauth-token", token);
		cookie.setMaxAge(300);
		response.addCookie(cookie);
		
		

		return "success";

	}

}
