package com.esctest.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.escframework.core.access.BaseDao;
import com.esctest.entity.Student;

public interface StudentDao extends BaseDao<Student, Long>
{

	@Value("#{target.studentName}")
	Student findByStudentName(String name);

	Student findByStudentNameAndAgeAndSex(String name, int age, int sex);

	@Query("from Student u where u.studentName=:name")
	List<Student> findStudent(@Param("name") String name);

	@Query("select u.studentName,u.sex from Student u where u.id=:id")
	List<Object[]> findByObjectList(@Param("id") Long id);

	@Query("select  u.studentName,u.sex from Student u where u.id=:id")
	// @Value("#{target.userName}")
	String findByMap(@Param("id") Long id);

	@Query(value = "select  * from t_student u where u.id=:id ", nativeQuery = true)
	Student findBySql(@Param("id") Long id);

}