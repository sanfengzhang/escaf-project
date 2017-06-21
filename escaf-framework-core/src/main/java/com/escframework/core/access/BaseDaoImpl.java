package com.escframework.core.access;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.query.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @author hanl
 *
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BaseDaoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseDao<T, ID>

{

	private final EntityManager em;

	public BaseDaoImpl(Class<T> domainClass, EntityManager em)
	{
		super(domainClass, em);
		this.em = em;

	}

	/**
	 * TODO use new API?
	 */	
	@SuppressWarnings("deprecation")
	@Override
	public List<Map<?, ?>> queryListMapResultBySql(String sql, Object... args)
	{
		Query query = getCurrentQuery(sql, args);
		query.setResultTransformer(new HibernateMapResultTransformer());
		return query.getResultList();
	}

	@Override
	public List<Object[]> queryListResultBySQL(String sql, Object... args)
	{

		return getCurrentQuery(sql, args).getResultList();

	}

	@Override
	public List<?> queryListClazzResultBySQL(String sql, Class<?> clazz, Object... args)
	{

		return getCurrentQuery(sql, args).getResultList();
	}

	public Map queryMapResultBysql(String sql, Object... args)
	{
		List<Object[]> _list = queryListResultBySQL(sql, args);
		if (CollectionUtils.isEmpty(_list))
		{
			return null;

		}
		Map resultMap = new HashMap();
		for (Object[] objects : _list)
		{
			if (null == objects)
			{
				return null;
			}
			if (objects.length < 2)
			{
				throw new IllegalArgumentException("the result type unsupported,len=" + objects.length);
			}
			resultMap.put(objects[0], objects[1]);

		}
		return resultMap;

	}

	@Override
	public int updateDataBySql(String sql, Object... args)
	{

		int count = getCurrentQuery(sql, args).executeUpdate();
		return count;
	}

	@Override
	public int deleteDataBysql(String sql, Object... args)
	{
		int count = updateDataBySql(sql, args);
		return count;
	}

	private Query getCurrentQuery(String sql, Object... args)
	{
		Query query = (Query) em.createNativeQuery(sql);
		if (!ArrayUtils.isEmpty(args))
		{
			int len = args.length;
			for (int i = 0; i < len; i++)
			{
				query.setParameter(i + 1, args[i]);
			}

		}
		return query;
	}

}
