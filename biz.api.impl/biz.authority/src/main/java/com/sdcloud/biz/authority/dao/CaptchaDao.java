package com.sdcloud.biz.authority.dao;

public interface CaptchaDao {

	void add(String key, String value);

	String get(String key);

	void remove(String key);

}
