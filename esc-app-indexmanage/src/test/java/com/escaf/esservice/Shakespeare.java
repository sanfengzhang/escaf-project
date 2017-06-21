package com.escaf.esservice;

import java.io.Serializable;

public class Shakespeare implements Serializable
{

	private static final long serialVersionUID = 1L;

	private String play_name;

	private int speech_number;

	private String text_entry;

	public String getPlay_name()
	{
		return play_name;
	}

	public void setPlay_name(String play_name)
	{
		this.play_name = play_name;
	}

	public int getSpeech_number()
	{
		return speech_number;
	}

	public void setSpeech_number(int speech_number)
	{
		this.speech_number = speech_number;
	}

	public String getText_entry()
	{
		return text_entry;
	}

	public void setText_entry(String text_entry)
	{
		this.text_entry = text_entry;
	}

	@Override
	public String toString()
	{
		return "Shakespeare [play_name=" + play_name + ", speech_number=" + speech_number + ", text_entry=" + text_entry + "]";
	}

}
