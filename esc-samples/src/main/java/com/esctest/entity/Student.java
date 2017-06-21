package com.esctest.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_student")
public class Student implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "stu_name")
	private String studentName;

	@Column(name = "age")
	private int age;
	
	@Column(name = "sex")
	private int sex;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "classes")
	private int classes;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getStudentName()
	{
		return studentName;
	}

	public void setStudentName(String studentName)
	{
		this.studentName = studentName;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public int getSex()
	{
		return sex;
	}

	public void setSex(int sex)
	{
		this.sex = sex;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public int getClasses()
	{
		return classes;
	}

	public void setClasses(int classes)
	{
		this.classes = classes;
	}

	@Override
	public String toString()
	{
		return "Student [id=" + id + ", studentName=" + studentName + ", age=" + age + ", sex=" + sex + ", address=" + address + ", classes="
				+ classes + "]";
	}
	
	
	
	
}
