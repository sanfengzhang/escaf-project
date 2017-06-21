package com.escframework.esauth.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsConfigUtils
{

	private Properties p = null;

	public static final String AUTH_PROPS = "/authConfig.properties";

	private static PropsConfigUtils propsConfigUtils = null;

	public  String getValue(String name)
	{
		return (String) p.get(name);
	}

	public static synchronized PropsConfigUtils getInstance()
	{
		if (null == propsConfigUtils)
		{
			propsConfigUtils = new PropsConfigUtils();
		}
		return propsConfigUtils;
	}

	private PropsConfigUtils()
	{

		this.initConf();
	}

	private void initConf()
	{
		p = new Properties();
		InputStream inputStream = null;
		try
		{

			inputStream = this.getClass().getResourceAsStream(AUTH_PROPS);
			p.load(inputStream);

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (null != inputStream)
			{
				try
				{
					inputStream.close();

				} catch (IOException e)
				{

					e.printStackTrace();
				}
			}

		}
	}

}
