package com.mvc.enterprises.form;

import java.util.List;

import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.utils.Utils;

public class UserForm {
	private List<User> resultUsers;
    private boolean showDetails;
	private User user;
	
	private Long userId;
    
	public List<User> getResultUsers() {
		return resultUsers;
	}
	public void setResultUsers(List<User> resultUsers) {
		this.resultUsers = resultUsers;
	}
	public boolean isShowDetails() {
		return showDetails;
	}
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public List<String> getRoles() {
		return Utils.Role.getAllRoles();
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
