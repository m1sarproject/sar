package com.m1sar;

public class CourtierNotFoundException extends Exception {

	
	String msg;

	public CourtierNotFoundException(String msg) {
		super();
		this.msg = msg;
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return msg;
	}
	
	
	
	
	
	
}
