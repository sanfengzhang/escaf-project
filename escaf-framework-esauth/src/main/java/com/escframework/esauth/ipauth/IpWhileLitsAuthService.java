package com.escframework.esauth.ipauth;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

import com.escframework.esauth.AuthException;
import com.escframework.esauth.AuthService;
import com.escframework.esauth.utils.PropsConfigUtils;
import com.escframework.esauth.utils.StringUtils;

public class IpWhileLitsAuthService implements AuthService
{

	private Set<String> ipWhileList = null;	

    private final   String msg = "No permission to access the URI";
	
	public IpWhileLitsAuthService()
	{
		ipWhileList = new ConcurrentSkipListSet<String>();
		ipWhileList.add("127.0.0.1");
		this.initIpWhileList();
	}

	public BytesRestResponse authorized(RestRequest request, RestChannel channel, NodeClient client)
	{

		String ip = null;
		try
		{
			SocketAddress socketAddress = request.getRemoteAddress();
			if (null != socketAddress && socketAddress instanceof InetSocketAddress)
			{
				InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
				ip = inetSocketAddress.getAddress().getHostAddress();				
				if (!ipWhileList.contains(ip))
				{
					ESLoggerFactory.getLogger(IpWhileLitsAuthService.class).info("IP={} not allowed to access", ip);
					return new BytesRestResponse(RestStatus.UNAUTHORIZED, msg);
				}

			}
		} catch (Exception e)
		{
			throw new AuthException("auth request failed host ip= " + ip, e);

		}

		return null;
	}

	protected void initIpWhileList()
	{

		PropsConfigUtils propsConfigUtils = PropsConfigUtils.getInstance();
		String ips = propsConfigUtils.getValue("ips");
		String[] ipArr = StringUtils.split(ips, ",");
		for (String ip : ipArr)
		{
			ipWhileList.add(ip);

		}

	}

	@Override
	public String toString()
	{
		return "IpWhileLitsAuthService [ipWhileList=" + ipWhileList + "]";
	}

}
