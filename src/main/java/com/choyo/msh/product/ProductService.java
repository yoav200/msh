package com.choyo.msh.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	

	public List<Product> getAllProducts() {
		return productRepository.findByActiveTrueOrderByOrderBy();
	}

	public Product findByCode(String code) throws NoProductFoundException {
		return Optional.ofNullable(productRepository.findByCode(code)).orElseThrow(() -> new NoProductFoundException(code));
	}
}
