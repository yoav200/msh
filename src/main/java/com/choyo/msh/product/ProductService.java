package com.choyo.msh.product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

	private static final Map<String, Product> products = new HashMap<>();

	static {
		products.put("1step",
				Product.builder().code("1step").description("$1 for 1 Step").price(BigDecimal.valueOf(1)).build());
		products.put("2step",
				Product.builder().code("2step").description("$2 for 2 Step").price(BigDecimal.valueOf(2)).build());
		products.put("5step",
				Product.builder().code("5step").description("$5 for 5 Step").price(BigDecimal.valueOf(5)).build());
	}

	public List<Product> getAllProducts() {
		return products.values().stream().collect(Collectors.toList());
	}

	public Product findByCode(String code) throws NoProductFoundException {
		return products.values().stream().filter(p -> p.getCode().equals(code)).findFirst()
				.orElseThrow(() -> new NoProductFoundException(code));
	}
}
