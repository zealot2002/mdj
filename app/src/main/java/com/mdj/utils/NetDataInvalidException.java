package com.mdj.utils;

public class NetDataInvalidException extends Exception {
	private String e;
	public NetDataInvalidException(String string) {
		e = string;
	}
	@Override
	public String toString() {
		return e;
	}
}
