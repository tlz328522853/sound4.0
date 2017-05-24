package com.sdcloud.biz.lar.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.DicEmployee;
import com.sdcloud.api.lar.service.DicEmployeeService;
import com.sdcloud.biz.lar.dao.DicEmployeeDao;
import com.sdcloud.framework.entity.Pager;
/**
 * 
 * @author czz
 *
 */
@Service
public class DicEmployeeSerivceImpl implements DicEmployeeService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DicEmployeeDao dicEmployeeDao;
	
	@Transactional
	public Pager<DicEmployee> findByOrg(Pager<DicEmployee> pager) {
		if(pager == null){
			//创建一个page
			pager = new Pager<DicEmployee>();
		}
		try {
			logger.info("init default pager");
			if(StringUtils.isEmpty(pager.getOrderBy())){
				//默认根据id排序
				pager.setOrderBy("id");
			}
			if(StringUtils.isEmpty(pager.getOrder())){
				//默认倒序
				pager.setOrder("desc");
			}
			//查询行总数
			if(pager.isAutoCount()){
				logger.info("querying total count by orgIds : ");
				long totalCount = dicEmployeeDao.countByOrg();
				pager.setTotalCount(totalCount);
				logger.info("querying total count result : " + totalCount);
			}
			
			logger.info("querying by orgIds : " );
			
			List<DicEmployee> result = dicEmployeeDao.findByOrg(pager);
			
			pager.setResult(result);
		} catch (Exception e) {
			logger.error("err when findByOrg, OrgId : " +", includeSub : "  + ", pager ： "+pager,e);
		}
		logger.info("complete findByOrg, OrgId : " +", includeSub : " + ", pager ： "+pager);
		
		return pager;
	}
}
