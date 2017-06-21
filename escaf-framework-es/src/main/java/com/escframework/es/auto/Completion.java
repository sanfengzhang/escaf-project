package com.escframework.es.auto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Completion
{

	private String[] input;
	private String output;
	private Integer weight;
	private Object payload;

	@SuppressWarnings("unused")
	private Completion()
	{
		// required by mapper to instantiate object
	}

	public Completion(String[] input)
	{
		this.input = input;
	}

	public String[] getInput()
	{
		return input;
	}

	public void setInput(String[] input)
	{
		this.input = input;
	}

	public String getOutput()
	{
		return output;
	}

	public void setOutput(String output)
	{
		this.output = output;
	}

	public Object getPayload()
	{
		return payload;
	}

	public void setPayload(Object payload)
	{
		this.payload = payload;
	}

	public Integer getWeight()
	{
		return weight;
	}

	public void setWeight(Integer weight)
	{
		this.weight = weight;
	}
}
