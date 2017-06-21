package com.escaf.esservice.search;

import java.util.List;

public class ElasticPage<T>
{

	private List<T> result;

	private long total;

	private int perPageCount;

	private int from;

	public ElasticPage(List<T> result, long total, int perPageCount)
	{
		super();
		this.result = result;
		this.total = total;
		this.perPageCount = perPageCount;
		hasNext();
	}

	public List<T> getResult()
	{
		return result;
	}

	public void setResult(List<T> result)
	{
		this.result = result;
	}

	public long getTotal()
	{
		return total;
	}

	public void setTotal(long total)
	{
		this.total = total;
	}

	public int getPerPageCount()
	{
		return perPageCount;
	}

	public void setPerPageCount(int perPageCount)
	{
		this.perPageCount = perPageCount;
	}

	public boolean hasNext()
	{
		return from + perPageCount > total ? false : true;
	}

}
