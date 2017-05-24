package com.sdcloud.biz.envsanitation.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.envsanitation.entity.AssetObject;
import com.sdcloud.api.envsanitation.entity.OrgAssetCount;

/**
 * 
 * @author lms
 */
public interface AssetObjectDao {
	
//	void insert(AssetObject assetObject);
	void insert(List<AssetObject> assetObjects);
	
	void delete(@Param("assetIds") List<Long> assetIds);
	
	void update(AssetObject assetObject);
	
	List<OrgAssetCount> findOrgAssetCount();
	
}
