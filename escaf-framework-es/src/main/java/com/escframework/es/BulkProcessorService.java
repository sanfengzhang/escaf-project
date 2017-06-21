package com.escframework.es;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.routing.IndexRoutingTable;
import org.elasticsearch.cluster.routing.IndexShardRoutingTable;
import org.elasticsearch.cluster.routing.Murmur3HashFunction;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.shard.ShardId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

public class BulkProcessorService implements InitializingBean, DisposableBean
{

	@Value("${es.index.servers}")
	private String esIndexServers;

	@Value("${spring.es.concurrentRequests}")
	private int springConcurrentRequests = 1;

	@Value("${spring.es.bulkActions}")
	private int springBulkActions = 1000;

	/** 批量提交按MB为单位的 */
	@Value("${spring.es.bulkSize}")
	private int springBulkSize = 5;

	@Value("${spring.es.flushInterval}")
	private int springFlushInterval;

	private List<Client> clients = new ArrayList<Client>();

	// 可以把这个几个集群参数另外放在缓存中去，能够动态变化控制。能够及时感知ES中索引配置的变化、
	private Map<String, BulkProcessor> nodeIdToBulkProcessorMap = new ConcurrentHashMap<>();

	private Map<String, String> indexstrShardToNodeIdMap = new ConcurrentHashMap<>();

	private Map<String, Integer> indexToNumShards = new ConcurrentHashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(BulkProcessorService.class);

	public BulkProcessorService()
	{

	}

	public void afterPropertiesSet() throws Exception
	{

		initBulkProcessors();
		initEsIndexShardsCache();
	}

	private void initBulkProcessors() throws Exception
	{
		if (!StringUtils.isEmpty(esIndexServers))
		{
			String indexServers[] = StringUtils.split(esIndexServers, ",");

			for (String indexServer : indexServers)
			{
				TransportClientFactoryBean transportClientFactoryBean = new TransportClientFactoryBean();
				transportClientFactoryBean.setClusterNodes(indexServer);
				transportClientFactoryBean.afterPropertiesSet();
				Client client = transportClientFactoryBean.getObject();

				BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessorListener())
						.setBulkActions(springBulkActions).setConcurrentRequests(springConcurrentRequests)
						.setBulkSize(new ByteSizeValue(springBulkSize, ByteSizeUnit.MB))
						.setFlushInterval(
								springFlushInterval == 0 ? null : new TimeValue(springFlushInterval, TimeUnit.SECONDS))
						.build();
				clients.add(client);
				nodeIdToBulkProcessorMap.put(indexServer, bulkProcessor);
			}
		}

	}

	private void initEsIndexShardsCache()
	{

		if (clients.size() > 0)
		{
			Client client = clients.get(0);
			ClusterStateResponse clusterSearchShardsResponse = client.admin().cluster().prepareState().get();
			ImmutableOpenMap<String, IndexRoutingTable> indexRoutingTables = clusterSearchShardsResponse.getState()
					.getRoutingTable().getIndicesRouting();

			Iterator<String> keysIt = indexRoutingTables.keysIt();

			while (keysIt.hasNext())
			{
				String key = keysIt.next();
				IndexRoutingTable indexRoutingTable = indexRoutingTables.get(key);

				indexToNumShards.put(key, indexRoutingTable.getShards().size());
				indexRoutingTable.shards().forEach(e ->
				{

					IndexShardRoutingTable indexShardRoutingTable = e.value;
					ShardRouting shardRouting = indexShardRoutingTable.primaryShard();

					String currentNodeId = shardRouting.currentNodeId();

					ShardId shardId = shardRouting.shardId();
					String index = shardId.getIndexName();
					int id = shardId.getId();
					String keyMap = index + "$$" + id;
					indexstrShardToNodeIdMap.put(keyMap, currentNodeId);

				});

			}

		}

	}

	public void addIndexRequest(IndexRequest request)
	{

		String index = request.index();
		String id = request.id();	
		int hash = Murmur3HashFunction.hash(id);
		int numShards = indexToNumShards.get(index);

		// 这里和ES的源代码里面的分片分拣数据的hash算法有区别，这里只是初步的分拣。希望能最大限度的减少在ES中通过远程传输分片数据的次数、
		// Math.floorMod(hash, indexMetaData.getRoutingNumShards())/
		// indexMetaData.getRoutingFactor()

		int chooseShardId = Math.floorMod(hash, numShards);

		// 根据索引名称和分配的shardId组合的字符串进行选择具体的BulkProcessor进行提交数据。
		BulkProcessor bulkProcessor = this.nodeIdToBulkProcessorMap.get(index + "$$" + chooseShardId);
		if (null == bulkProcessor)
		{
			logger.error("the bulkProcessor null", new NullPointerException("the bulkProcessor null"));
		}

		bulkProcessor.add(request);

	}

	public void destroy() throws Exception
	{
		if (!CollectionUtils.isEmpty(nodeIdToBulkProcessorMap))
		{
			nodeIdToBulkProcessorMap.entrySet().forEach(e ->
			{
				BulkProcessor bulkProcessor = e.getValue();
				if (null != bulkProcessor)
				{
					bulkProcessor.close();

				}
			});
		}

		if (!CollectionUtils.isEmpty(clients))
		{
			clients.forEach(e -> e.close());
		}

	}

	class BulkProcessorListener implements BulkProcessor.Listener
	{

		public void beforeBulk(long executionId, BulkRequest request)
		{
			logger.info("ready to submit data to ES executionId={},numberOfActions={},requetsServerIp={}", executionId,
					request.numberOfActions(), request.remoteAddress());

		}

		public void afterBulk(long executionId, BulkRequest request, BulkResponse response)
		{
			logger.info("complete submit data to es  executionId={}", executionId);
		}

		public void afterBulk(long executionId, BulkRequest request, Throwable failure)
		{

			logger.warn("failed to index elastic executionId={},requetsServerIp={},failureMsg={}", executionId,
					request.remoteAddress(), failure);
		}
	}

}
