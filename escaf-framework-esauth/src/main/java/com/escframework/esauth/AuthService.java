package com.escframework.esauth;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;

/**
 * 
 * @author HanLin
 *
 */
public interface AuthService
{

	public BytesRestResponse authorized(RestRequest request, RestChannel channel,NodeClient client);

}
