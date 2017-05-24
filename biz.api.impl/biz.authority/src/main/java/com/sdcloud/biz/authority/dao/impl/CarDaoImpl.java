package com.sdcloud.biz.authority.dao.impl;

import java.util.Map;






import java.util.Map.Entry;

import javax.annotation.Resource;





import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sdcloud.api.authority.util.CacheKeyGenerator;
import com.sdcloud.biz.authority.dao.CarDao;
@Service
public class CarDaoImpl implements CarDao {
	
	
	@Resource(name = "redisTemplate")
    private HashOperations<String, String,String>    cardNumber_chassisType;
	
	public void addCarChassisType(Map<String, String> card_chassisType) {
		for (Entry<String, String> cardchassisType : card_chassisType.entrySet()) {
			cardNumber_chassisType.put(CacheKeyGenerator.getChassisType(cardchassisType.getKey()),"chassisType", cardchassisType.getValue());
		}
	}

	@Override
	public void updateCarCardNumber(String oldCardNumber, String newCardNumber) {
		String value=cardNumber_chassisType.get(CacheKeyGenerator.getChassisType(oldCardNumber),"chassisType");
		if(!StringUtils.isEmpty(value)){
			cardNumber_chassisType.put(CacheKeyGenerator.getChassisType(newCardNumber),"chassisType", value);
		}
		cardNumber_chassisType.getOperations().delete(CacheKeyGenerator.getChassisType(oldCardNumber));
	
	}

	@Override
	public void updateCarChassisType(String cardNumber, String newCarChassisType) {
		cardNumber_chassisType.delete(CacheKeyGenerator.getChassisType(cardNumber), "chassisType");
		cardNumber_chassisType.put(CacheKeyGenerator.getChassisType(cardNumber),"chassisType", newCarChassisType);
	}
	 public void removeCarChassisType(String cardNumber){
		 cardNumber_chassisType.delete(CacheKeyGenerator.getChassisType(cardNumber), "chassisType");
	 }
}
