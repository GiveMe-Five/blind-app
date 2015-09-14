package com.livhong.connect;

public class User {

	private int id;
	private String name;
	private String password;
	
/*
 * '\u00ff' to splits entries => item;
 * '\u00fc' to splits items;
*/
	private String pending;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPending() {
		return pending;
	}
	public void setPending(String pending) {
		this.pending = pending;
	}
	
}
