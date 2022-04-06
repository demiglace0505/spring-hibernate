package com.demiglace.springdata.product.repos;

import org.springframework.data.repository.CrudRepository;

import com.demiglace.springdata.product.entities.Product;

public interface ProductRepository extends CrudRepository<Product, Integer> {

}
