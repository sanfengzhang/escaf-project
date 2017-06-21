package com.escframework.core.access;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 
 * @author hanl
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends JpaRepository<T, ID>
{
	
	public List<Map<?,?>> queryListMapResultBySql(String sql, Object... args);

	/** SQL native query */
	public List<Object[]> queryListResultBySQL(String sql, Object... args);

	public List<?> queryListClazzResultBySQL(String sql, Class<?> clazz, Object... args);

	/** SQL native query,返回的是一个Map结果,只取结果集前两列的值做为k/v */
	public Map<?, ?> queryMapResultBysql(String sql, Object... args);

	/**更新数据*/
	public int updateDataBySql(String sql, Object... args);
	
	/**删除数据，只支持逻辑删除*/
	public int deleteDataBysql (String sql,Object...args);

}
