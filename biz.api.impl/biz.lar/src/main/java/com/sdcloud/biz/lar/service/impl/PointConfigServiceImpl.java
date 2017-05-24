package com.sdcloud.biz.lar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sdcloud.api.lar.entity.PointConfig;
import com.sdcloud.api.lar.service.PointConfigService;
import com.sdcloud.biz.lar.dao.PointConfigDao;
import com.sdcloud.framework.common.UUIDUtil;
/**
 * 
 * @author dingx
 *
 */
@Service
public class PointConfigServiceImpl implements PointConfigService{
	
	private final Logger logger = LoggerFactory.getLogger(super.getClass());

	@Autowired
	private PointConfigDao pointConfigDao;

	@Override
	@Transactional(readOnly=true)
	public PointConfig findByOrg(String org) {
		// TODO Auto-generated method stub
		logger.info("start findByOrg method, param is org:"+org);
		PointConfig pointConfig = pointConfigDao.findByOrg(org);
		return pointConfig;
	}

	@Transactional
	public Boolean updatePointConfig(PointConfig pointConfig) {
		logger.info("start updatePointConfig menthod,param is pointConfig:"+pointConfig);
		if(null != pointConfig){
			
			if(pointConfig.getPointRate()==""){
				pointConfig.setPointRate(0+"");
			}
			if(pointConfig.getPointLine()==""){
				pointConfig.setPointLine(0+"");
			}
			pointConfigDao.updatePointConfig(pointConfig);
		}else{
			logger.info("update pointConfig method param pointConfig is null");
		}
		return true;
		
	}

	@Override
	public Boolean savePointConfig(PointConfig pointConfig) {
		// TODO Auto-generated method stub
		logger.info("start savePointConfig menthod,param is pointConfig:"+pointConfig);
		try{
			if(pointConfig==null){
				logger.info("param 'pointConfig' cannot be null !");			
			}
			Long uuid = UUIDUtil.getUUNum();
			pointConfig.setId(uuid);
			if(pointConfig.getId()==null){
				logger.info("Column 'id' connot be null !");
			}
			Boolean br = pointConfigDao.savePointConfig(pointConfig);
			logger.info("savePointConfig method is success !");
			return br;
			
		}catch (Exception e){
			logger.info("savePointConfig method is error:"+e);
			throw e;
		}
		
		
	}

}