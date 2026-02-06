package com.mvc.enterprises.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mvc.enterprises.entities.OrderQty;
import com.mvc.enterprises.repository.OrderQtyRepository;

@ExtendWith(MockitoExtension.class)
public class OrderQtyServiceTest {
	
	@InjectMocks
	private OrderQtyService orderQtyService;
	
	@Mock
	private OrderQtyRepository orderQtyRepository;
	
	@Test
	public void testFindById() {
		OrderQty orderQty = new OrderQty();
		orderQty.setOrderId(Long.valueOf(1));
		
		Mockito.when(orderQtyRepository.findByOrderId(Long.valueOf(1))).thenReturn(Optional.of(orderQty));
		
		OrderQty resultOrderQty = orderQtyService.findByOrderId(Long.valueOf(1));
		
		assertEquals(resultOrderQty.getOrderId(), orderQty.getOrderId());
	}
	
	

}
