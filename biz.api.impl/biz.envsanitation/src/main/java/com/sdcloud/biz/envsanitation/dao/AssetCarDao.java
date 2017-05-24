package com.sdcloud.biz.envsanitation.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.AssetCar;

/**
 * 
 * @author lms
 */
public interface AssetCarDao extends AssetDao<AssetCar> {
	
	AssetCar findAssetCarByCarNo(@Param("carNumber")String carNumber);
	
	List<AssetCar> findAssetCarByCarNos(Map<String,Object> pram);

	List<Map<String, String>> findAllCarChassisNumber();
	
	List<AssetCar> findAssetCarByCompanyIds(Map<String, Object> param);

	List<AssetCar> findAssetCarByParam(Map<String, Object> param);
	
}