package com.escframework.es;

import static org.apache.commons.lang.StringUtils.split;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;

import java.net.InetAddress;
import java.util.Properties;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

public class TransportClientFactoryBean implements FactoryBean<TransportClient>, InitializingBean, DisposableBean
{

	private static final Logger logger = LoggerFactory.getLogger(TransportClientFactoryBean.class);

	@Value("${es.clusterNodes}")
	private String clusterNodes = "127.0.0.1:9300";

	@Value("${es.clusterName}")
	private String clusterName = "es-cluster";

	private Boolean clientTransportSniff = true;

	private Boolean clientIgnoreClusterName = Boolean.FALSE;

	@Value("${client.transport.ping_timeout}")
	private String clientPingTimeout = "5s";

	@Value("${client.transport.nodes_sampler_interval}")
	private String clientNodesSamplerInterval = "5s";

	private TransportClient client;

	private Properties properties;

	static final String COLON = ":";

	static final String COMMA = ",";

	public void destroy() throws Exception
	{
		try
		{
			logger.info("Closing elasticSearch  client");
			if (client != null)
			{
				client.close();
			}
		} catch (final Exception e)
		{
			logger.error("Error closing ElasticSearch client: ", e);
		}

	}

	public void afterPropertiesSet() throws Exception
	{
		buildClient();
	}

	protected void buildClient() throws Exception
	{
		client =  new PreBuiltTransportClient(settings());
		Assert.hasText(clusterNodes, "[Assertion failed] clusterNodes settings missing.");
		for (String clusterNode : split(clusterNodes, COMMA))
		{
			String hostName = substringBeforeLast(clusterNode, COLON);
			String port = substringAfterLast(clusterNode, COLON);
			Assert.hasText(hostName, "[Assertion failed] missing host name in 'clusterNodes'");
			Assert.hasText(port, "[Assertion failed] missing port in 'clusterNodes'");
			logger.info("adding transport node : " + clusterNode);
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostName), Integer.valueOf(port)));
		}
		client.connectedNodes();
	}

	private Settings settings()
	{
		if (properties != null)
		{
			return Settings.builder().put(properties).build();
		}
		return Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", clientTransportSniff)
				.put("client.transport.ignore_cluster_name", clientIgnoreClusterName).put("client.transport.ping_timeout", clientPingTimeout)
				.put("client.transport.nodes_sampler_interval", clientNodesSamplerInterval).build();
	}

	public TransportClient getObject() throws Exception
	{

		return client;
	}

	public Class<TransportClient> getObjectType()
	{
		return TransportClient.class;
	}

	public boolean isSingleton()
	{

		return false;
	}

	public void setClusterNodes(String clusterNodes)
	{
		this.clusterNodes = clusterNodes;
	}

	public void setClusterName(String clusterName)
	{
		this.clusterName = clusterName;
	}

	public void setClientTransportSniff(Boolean clientTransportSniff)
	{
		this.clientTransportSniff = clientTransportSniff;
	}

	public String getClientNodesSamplerInterval()
	{
		return clientNodesSamplerInterval;
	}

	public void setClientNodesSamplerInterval(String clientNodesSamplerInterval)
	{
		this.clientNodesSamplerInterval = clientNodesSamplerInterval;
	}

	public String getClientPingTimeout()
	{
		return clientPingTimeout;
	}

	public void setClientPingTimeout(String clientPingTimeout)
	{
		this.clientPingTimeout = clientPingTimeout;
	}

	public Boolean getClientIgnoreClusterName()
	{
		return clientIgnoreClusterName;
	}

	public void setClientIgnoreClusterName(Boolean clientIgnoreClusterName)
	{
		this.clientIgnoreClusterName = clientIgnoreClusterName;
	}

	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}
}
