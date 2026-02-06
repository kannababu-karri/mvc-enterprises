package com.mvc.enterprises.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Test
	public void testFindId() {
		User user = new User();
		user.setUserId(Long.valueOf(1));
		
		Mockito.when(userRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(user));
		
		User resultUser = userService.findById(Long.valueOf(1));
		
		assertEquals(resultUser.getUserId(), user.getUserId());	
	}
}