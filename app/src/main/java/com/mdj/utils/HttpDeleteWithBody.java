package com.mdj.utils;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

import org.apache.http.annotation.NotThreadSafe;

/**
 * @Title: HttpDeleteWithBody.java
 * @Package com.mdj.utils
 * @Description: HttpDelete没有对应的setEntity()方法,重新封装的httpdelete请求
 * @author hwk
 * @date 2015-10-15 下午6:41:55
 */

@NotThreadSafe
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

	public static final String METHOD_NAME = "DELETE";

	public String getMethod() {
		return METHOD_NAME;
	}

	public HttpDeleteWithBody(final String uri) {
		super();
		setURI(URI.create(uri));
	}

	public HttpDeleteWithBody(final URI uri) {
		super();
		setURI(uri);
	}

	public HttpDeleteWithBody() {
		super();
	}
}
