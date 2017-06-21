package com.escframework.core.access;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.transform.BasicTransformerAdapter;

/**
 * 
 * @author hanl
 *
 */
public class HibernateMapResultTransformer extends  BasicTransformerAdapter
{

	private static final long serialVersionUID = 1L;

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int len = tuple.length;

		for (int i = 0; i < len; i++)
		{
			resultMap.put(aliases[i], tuple[i]);

		}

		return resultMap;
	}

	

}
