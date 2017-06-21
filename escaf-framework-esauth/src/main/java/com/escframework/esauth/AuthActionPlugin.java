package com.escframework.esauth;

import java.util.function.UnaryOperator;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

public class AuthActionPlugin extends Plugin implements ActionPlugin
{
	private AuthService authService = null;

	public AuthActionPlugin()
	{
		super();
		authService = AuthServiceFactory.getAuthService();

	}

	public UnaryOperator<RestHandler> getRestHandlerWrapper(ThreadContext threadContext)
	{

		return new UnaryOperator<RestHandler>()
		{

			public RestHandler apply(RestHandler restHandler)
			{

				return new AuthRestHandler(restHandler);
			}
		};

	}

	class AuthRestHandler implements RestHandler
	{
		private RestHandler restHandler;

		public AuthRestHandler(RestHandler restHandler)
		{
			this.restHandler = restHandler;

		}

		public void handleRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception
		{
			try
			{
				BytesRestResponse bytesRestResponse = authService.authorized(request, channel, client);
				if (null != bytesRestResponse)
				{
					// final String msg = "No permission to access the uri [" + request.uri() + "]";
					channel.sendResponse(bytesRestResponse);
					return;
				}
			} catch (Exception e)
			{

				channel.sendResponse(new BytesRestResponse(RestStatus.UNAUTHORIZED, e.getMessage()));
				return;
			}

			restHandler.handleRequest(request, channel, client);

		}
	}

}
