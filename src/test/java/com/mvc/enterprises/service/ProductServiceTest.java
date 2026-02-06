package com.mvc.enterprises.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mvc.enterprises.entities.Product;
import com.mvc.enterprises.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	
	@InjectMocks
	private ProductService productService;
	
	@Mock
	private ProductRepository productRepository;
	
	@Test
	public void testFindByProductName() {
		Product product = new Product();
		product.setProductId(Long.valueOf(1));
		product.setProductName("product name");
		
		when(productRepository.findByProductName("product name")).thenReturn(Optional.of(product));
		
		Product productFromService = productService.findByProductName("product name");
		
		assertEquals(productFromService.getProductName(), product.getProductName());
	}

}
