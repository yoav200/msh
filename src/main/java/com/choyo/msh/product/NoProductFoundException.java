package com.choyo.msh.product;

public class NoProductFoundException extends Exception {
	public NoProductFoundException(String code) {
		super("Product with code '" + code + "' not found.");
	}
}
