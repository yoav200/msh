package com.choyo.msh.product;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class Product {

	private String code;
	@EqualsAndHashCode.Exclude private String description;
	private BigDecimal price;
}
