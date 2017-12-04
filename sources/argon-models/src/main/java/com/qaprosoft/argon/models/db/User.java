package com.qaprosoft.argon.models.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.argon.models.db.Group.Role;

@JsonInclude(Include.NON_NULL)
public class User extends AbstractEntity {
	private static final long serialVersionUID = 2720141152633805371L;

	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private List<Group> groups = new ArrayList<>();

	public User() {
	}

	public User(long id) {
		super.setId(id);
	}

	public User(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void setRoles(List<Role> roles) {
		// Do nothing just treak for dozer mapper
	}

	public List<Role> getRoles() {
		Set<Role> roles = new HashSet<>();
		for (Group group : groups) {
			roles.add(group.getRole());
		}
		return new ArrayList<>(roles);
	}
}