package com.escaf.esservice.entity;

import com.escaf.esservice.core.indexstrategy.IndexNameStrategyDay;
import com.escframework.es.anotation.Document;
import com.escframework.es.anotation.Field;
import com.escframework.es.anotation.FieldIndex;
import com.escframework.es.anotation.FieldType;

@Document(indexName = "user_info_index", type = "user_type", replicas = 0, shards = 5, refreshInterval = "30s", indexStrategyClass = IndexNameStrategyDay.class)
public class UserIndexEntity
{

	@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private Integer userId;

	@Field(type = FieldType.String, analyzer = "ik", searchAnalyzer = "ik", store = true)
	private String userContent;

	@Field(type = FieldType.String, analyzer = "ik", searchAnalyzer = "ik", store = true)
	private String userArea;

	@Field(type = FieldType.String, analyzer = "ik", searchAnalyzer = "ik", store = true)
	private String userTags;

	@Field(type = FieldType.Integer, index = FieldIndex.not_analyzed, store = true)
	private Integer userState;

	public Integer getUserId()
	{
		return userId;
	}

	public void setUserId(Integer userId)
	{
		this.userId = userId;
	}

	public String getUserContent()
	{
		return userContent;
	}

	public void setUserContent(String userContent)
	{
		this.userContent = userContent;
	}

	public String getUserArea()
	{
		return userArea;
	}

	public void setUserArea(String userArea)
	{
		this.userArea = userArea;
	}

	public String getUserTags()
	{
		return userTags;
	}

	public void setUserTags(String userTags)
	{
		this.userTags = userTags;
	}

	public Integer getUserState()
	{
		return userState;
	}

	public void setUserState(Integer userState)
	{
		this.userState = userState;
	}

}
