package com.escframework.esauth.tokenauth;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

import com.escframework.esauth.AuthService;
import com.escframework.esauth.utils.DateUtils;
import com.escframework.esauth.utils.PropsConfigUtils;

/**
 * 做简单点，查询认证逻辑可以放在其他诸如：统一认证中心去做认证，并生成token。
 * 
 * @author HanLin
 *
 */
public class AuthTokenService implements AuthService
{

	private static final String RECEIVE_TOKEN_REQUEST_URL = "/_esc/estoken";

	private static final  String TOKEN = "x-esauth-token";

	private static Map<String, Date> tokenMap = new ConcurrentHashMap<String, Date>();

	private final ScheduledExecutorService scheduler;

	public AuthTokenService()
	{
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new CheckTokenExpiresTask(), 1, 10, TimeUnit.MINUTES);

	}

	public BytesRestResponse authorized(RestRequest request, RestChannel channel, NodeClient client)
	{

		BytesRestResponse checkBytesRestResponse = checkReceiveToken(request, channel, client);
		if (null != checkBytesRestResponse)
		{
			return checkBytesRestResponse;
		}
		String authToken = request.header(TOKEN);
		if (null == authToken || "".equals(authToken))
		{
			authToken = request.param(TOKEN);
		}

		if (null == authToken || "".equals(authToken))
		{

			BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.FOUND, "Request Authorized");
			bytesRestResponse.addHeader("Location", PropsConfigUtils.getInstance().getValue("AUTH_TOKEN_URL"));			
			return bytesRestResponse;
		} else
		{
			// 在认证交给第三方认证系统的时候，返回x-es@auth-token给客户端，客户端拿着该token再次请求。但是需要注意：在server端也可能需要验证token是否存在
			// 需要第三方认证时候将生成的token只会传到给server端，server端将该token存储。在此做校验
			Date date = tokenMap.get(authToken);
			if (null == date)
			{
				return new BytesRestResponse(RestStatus.UNAUTHORIZED, "Authorized Failed!");

			}
		}

		return null;
	}

	private BytesRestResponse checkReceiveToken(RestRequest request, RestChannel channel, NodeClient client)
	{
		// 这个请求URL只在内部应用系统间暴露，不对外网公开。
		if (request.rawPath().equals(RECEIVE_TOKEN_REQUEST_URL))
		{
			String token = request.param(TOKEN);
			if (null != token && !"".equals(token))
			{
				tokenMap.put(token, new Date());

				// -------------------这个表示内部认证系统通过认证并将token返回给ES集群的auth，此时需要将用户请求的路径进行处理--------------
				return new BytesRestResponse(RestStatus.OK, "Received Token Success!");

			} else
			{
				return new BytesRestResponse(RestStatus.UNAUTHORIZED, "Authorized Failed!");
			}
		}

		return null;

	}

	class CheckTokenExpiresTask implements Runnable
	{

		public void run()
		{
			Set<Entry<String, Date>> set = tokenMap.entrySet();
			Iterator<Entry<String, Date>> it = set.iterator();
			List<String> removeKeys = new LinkedList<String>();
			while (it.hasNext())
			{
				Entry<String, Date> en = it.next();
				Date value = en.getValue();
				int expiresMin = Integer.valueOf(PropsConfigUtils.getInstance().getValue("expiresTokenMin"));
				Date expiresDate = DateUtils.addMinutes(value, expiresMin);
				if (DateUtils.compareDate(new Date(), expiresDate))
				{
					removeKeys.add(en.getKey());
				}

			}

			// --------------------------------------

			for (String removeToken : removeKeys)
			{
				tokenMap.remove(removeToken);
			}

		}
	}

}
