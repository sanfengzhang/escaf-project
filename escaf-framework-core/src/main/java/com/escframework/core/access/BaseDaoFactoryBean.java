package com.escframework.core.access;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class BaseDaoFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I>
{

	public BaseDaoFactoryBean(Class<? extends R> repositoryInterface)
	{
		super(repositoryInterface);

	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager)
	{

		return new BaseDaoFactory<>(entityManager);
	}

	private static class BaseDaoFactory<T, I extends Serializable> extends JpaRepositoryFactory
	{

		private final EntityManager entityManager;

		public BaseDaoFactory(EntityManager entityManager)
		{
			super(entityManager);
			this.entityManager = entityManager;

		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getTargetRepository(RepositoryInformation information)
		{

			return new BaseDaoImpl<T, I>((Class<T>) information.getDomainType(), entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata)
		{

			return BaseDaoImpl.class;
		}

	}

}
