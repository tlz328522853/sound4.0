package com.sdcloud.biz.authority.dao;

import java.util.Map;

public interface CarDao {
	public void addCarChassisType(Map<String, String> cardNumber_chassisType);
	public void updateCarCardNumber(String oldCardNumber, String newCardNumber);
	public void updateCarChassisType(String cardNumber,String newCarChassisType) ;
	 public void removeCarChassisType(String cardNumber);
}
