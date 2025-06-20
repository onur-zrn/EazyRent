package com.eazyrent.service;


import com.eazyrent.repository.RepositoryManager;

import java.util.List;
import java.util.Optional;

public abstract class ServiceManager<T, ID> implements IService<T, ID> {
	
	private final RepositoryManager<T, ID> repository;
	
	public ServiceManager(RepositoryManager<T, ID> repository) {
		this.repository = repository;
	}
	
	@Override
	public T save(T entity) {
		return repository.save(entity);
	}
	
	@Override
	public Iterable<T> saveAll(Iterable<T> entities) {
		return repository.saveAll(entities);
	}
	
	@Override
	public boolean deleteById(ID id) {
		return repository.deleteById(id);
	}
	
	@Override
	public T update(T entity) {
		return repository.update(entity);
	}
	
	@Override
	public Optional<T> findById(ID id) {
		return repository.findById(id);
	}
	
	@Override
	public List<T> findAll() {
		return repository.findAll();
	}
	
	@Override
	public boolean existsById(ID id) {
		return repository.existsById(id);
	}
	
	protected RepositoryManager<T, ID> getRepository() {
		return repository;
	}
}