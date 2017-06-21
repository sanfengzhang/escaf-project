package com.escframework.esauth.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils
{

	public static String[] split(String str, String separatorChars)
	{
		return splitWorker(str, separatorChars, -1, false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens)
	{
		// Performance tuned for 2.0 (JDK1.4)
		// Direct code is quicker than StringTokenizer.
		// Also, StringTokenizer uses isSpace() not isWhitespace()

		if (str == null)
		{
			return null;
		}
		int len = str.length();
		if (len == 0)
		{
			return new String[0];
		}
		List list = new ArrayList();
		int sizePlus1 = 1;
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		if (separatorChars == null)
		{
			// Null separator means use whitespace
			while (i < len)
			{
				if (Character.isWhitespace(str.charAt(i)))
				{
					if (match || preserveAllTokens)
					{
						lastMatch = true;
						if (sizePlus1++ == max)
						{
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else if (separatorChars.length() == 1)
		{
			// Optimise 1 character case
			char sep = separatorChars.charAt(0);
			while (i < len)
			{
				if (str.charAt(i) == sep)
				{
					if (match || preserveAllTokens)
					{
						lastMatch = true;
						if (sizePlus1++ == max)
						{
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		} else
		{
			// standard case
			while (i < len)
			{
				if (separatorChars.indexOf(str.charAt(i)) >= 0)
				{
					if (match || preserveAllTokens)
					{
						lastMatch = true;
						if (sizePlus1++ == max)
						{
							i = len;
							lastMatch = false;
						}
						list.add(str.substring(start, i));
						match = false;
					}
					start = ++i;
					continue;
				}
				lastMatch = false;
				match = true;
				i++;
			}
		}
		if (match || (preserveAllTokens && lastMatch))
		{
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
}
