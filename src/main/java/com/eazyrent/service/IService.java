package com.eazyrent.service;


import com.eazyrent.repository.ICrud;

import java.util.List;
import java.util.Optional;

public interface IService<T, ID> extends ICrud<T, ID> {
	T save(T entity);
	
	Iterable<T> saveAll(Iterable<T> entities);
	
	boolean deleteById(ID id);
	
	T update(T entity);
	
	Optional<T> findById(ID id);
	
	List<T> findAll();
	
	boolean existsById(ID id);
}