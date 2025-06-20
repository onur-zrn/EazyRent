package com.eazyrent.repository;

import com.eazyrent.utility.JPAUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

public abstract class RepositoryManager<T, ID> implements ICrud<T, ID> {

	private Class<T> entityClass;

	public RepositoryManager(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	// Yeni EntityManager her işlem için açılacak
	private EntityManager getEntityManager() {
		return JPAUtility.getEntityManager();
	}

	@Override
	public T save(T entity) {
		EntityManager em = getEntityManager();
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			em.persist(entity);
			transaction.commit();
			return entity;
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			em.close(); // işlem sonrasında em'i kapatıyoruz
		}
	}

	@Override
	public Iterable<T> saveAll(Iterable<T> entities) {
		EntityManager em = getEntityManager();
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			for (T entity : entities) {
				em.persist(entity);
			}
			transaction.commit();
			return entities;
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	@Override
	public boolean deleteById(ID id) {
		EntityManager em = getEntityManager();
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			T entityToRemove = em.find(entityClass, id);
			if (entityToRemove != null) {
				em.remove(entityToRemove);
				transaction.commit();
				return true;
			}
			transaction.rollback();
			return false;
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	@Override
	public T update(T entity) {
		EntityManager em = getEntityManager();
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			T updatedEntity = em.merge(entity);
			transaction.commit();
			return updatedEntity;
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}

	@Override
	public Optional<T> findById(ID id) {
		EntityManager em = getEntityManager();
		T entity = em.find(entityClass, id);
		em.close();
		return Optional.ofNullable(entity);
	}

	@Override
	public List<T> findAll() {
		EntityManager em = getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = cb.createQuery(entityClass);
		Root<T> root = criteriaQuery.from(entityClass);
		criteriaQuery.select(root);
		TypedQuery<T> q = em.createQuery(criteriaQuery);
		List<T> result = q.getResultList();
		em.close();
		return result;
	}

	@Override
	public boolean existsById(ID id) {
		EntityManager em = getEntityManager();
		T entity = em.find(entityClass, id);
		em.close();
		return entity != null;
	}
}