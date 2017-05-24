package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.management.RuntimeOperationsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.RecycleChenkExport;
import com.sdcloud.api.lar.entity.RecycleInstock;
import com.sdcloud.api.lar.entity.RecycleOutstock;
import com.sdcloud.api.lar.entity.RecycleStock;
import com.sdcloud.api.lar.service.RecycleInstockPriceService;
import com.sdcloud.api.lar.service.RecycleOutstockService;
import com.sdcloud.api.lar.service.RecycleStockService;
import com.sdcloud.biz.lar.dao.RecycleOutstockDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

import io.jsonwebtoken.lang.Collections;

/**
 * lar_recycle_outstock 出库管理
 * 
 * @author luorongjie
 * @date 2016-12-07
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class RecycleOutstockServiceImpl extends BaseServiceImpl<RecycleOutstock> implements RecycleOutstockService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecycleStockService recycleStockService;

	@Autowired
	private RecycleOutstockDao recycleOutstockDao;
	
	@Autowired
	private RecycleInstockPriceService recycleInstockPriceService;

	/**
	 * 1，判断是否有充足的库存量可供出库存，库存量-锁定出库量》出库数量 2，保存出库
	 */
	@Override
	@Transactional(readOnly = false)
	public Boolean save(RecycleOutstock t) {

		LarPager<RecycleStock> larPager = new LarPager<>();
		larPager.getExtendMap().put("spec_id", t.getSpecId());
		List<Long> ids = new ArrayList<>();
		ids.add(t.getOrgId());
		recycleStockService.findStockByOrgIds(larPager, ids);
		if (!Collections.isEmpty(larPager.getResult()) && larPager.getResult().size()!=0) {
			//RecycleStock recycleStock = larPager.getResult().get(0);
			//if (recycleStock.getStockNum().compareTo(t.getNum())>=0 ) {
				logger.info("method {},stock is enough ,outstockNum is {},stockNum is {}",
						Thread.currentThread().getStackTrace()[1].getMethodName(),0 /*recycleStock.getStockNum()*/,
						t.getNum());

				//recycleStock.setLockedNum(recycleStock.getLockedNum() + t.getNum().longValue());

				//Boolean update = recycleStockService.update(recycleStock);
				logger.info("method {}, updateRecycleStock result is :{}",
						Thread.currentThread().getStackTrace()[1].getMethodName()/*, update*/);
				
				/*if(!update){
					return false;
				}*/
				Long uuid = UUIDUtil.getUUNum();
				if (t.getId() == null) {
					t.setId(uuid);
				}
				int count = recycleOutstockDao.save(t);
				logger.info("method {}, saveRecycleOutstock result is :{}",
						Thread.currentThread().getStackTrace()[1].getMethodName(), count > 0);

				if (count > 0) {
					return true;
				}
			/*} else {
				throw new IndexOutOfBoundsException("库存不足！");//本项目未自定义异常，暂时用此异常代替！
			}*/
		} else {
			throw new IndexOutOfBoundsException("库存不足！该物品规格暂未入过库，无法出库 ");
		}

		throw new RuntimeException("保存失败！");
	}

	/**
	 * 审核入库 	1，查看该机构下，该条库存是否可供出库，出库存量必须大于出库数量 
	 * 			2，更新入库单为已审核，已入库
	 * 
	 */
	@Override
	@Transactional(readOnly = false)
	public boolean audit(RecycleOutstock recycleOutstock) {
		LarPager<RecycleStock> larPager = new LarPager<>();
		larPager.getExtendMap().put("spec_id", recycleOutstock.getSpecId());
		List<Long> ids = new ArrayList<>();
		ids.add(recycleOutstock.getOrgId());
		recycleStockService.findStockByOrgIds(larPager, ids);
		if (!Collections.isEmpty(larPager.getResult()) && larPager.getResult().size() == 1) {
			RecycleStock stock = larPager.getResult().get(0);
			if (stock.getStockNum() .compareTo(recycleOutstock.getNum()) >=0) {

				logger.info("method {},stock and LockedNum is enough ,outstockNum is {},stockNum is {}",
						Thread.currentThread().getStackTrace()[1].getMethodName(), stock.getStockNum(),
						recycleOutstock.getNum());

				stock.setStockNum(stock.getStockNum() .subtract(recycleOutstock.getNum()) );
				//stock.setLockedNum(stock.getLockedNum() - recycleOutstock.getNum().longValue());
				stock.setSumOutstockNum(stock.getSumOutstockNum().add(recycleOutstock.getNum()));
				
				Boolean updateStock = recycleStockService.update(stock);
				Boolean updateOutstock = update(recycleOutstock);
				if (updateOutstock && updateStock){
					//维护月平均销售单价
					boolean checkPriceForInstock = recycleInstockPriceService.checkPriceForOutstock(recycleOutstock.getOrgId(), recycleOutstock.getAuditDate(), recycleOutstock.getSpecId(), recycleOutstock.getPriceId());
					if(!(checkPriceForInstock )){
						throw new RuntimeException("审核失败！");
					}
					return true;
				}
				
				logger.warn(
						"method {}, audit Failure rollback ,updateStock's result is {},updateOutstock's result is {} ;",
						Thread.currentThread().getStackTrace()[1].getMethodName(), updateStock, updateOutstock);
				throw new RuntimeException("审核失败！");
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 更新出库单，如果enable ==0,
	 * 证明是可能修改数量
	 * 
	 * @param outstock
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public Boolean updateNum(RecycleOutstock outstock) {
		int count = 0;
		if (outstock.getEnable() == 0) {
			LarPager<RecycleStock> larPager = new LarPager<>();
			larPager.getExtendMap().put("spec_id", outstock.getSpecId());
			List<Long> ids = new ArrayList<>();
			ids.add(outstock.getOrgId());
			recycleStockService.findStockByOrgIds(larPager, ids);
			if (!Collections.isEmpty(larPager.getResult()) && larPager.getResult().size() == 1) {
				
				/*RecycleStock stock = larPager.getResult().get(0);
				if (stock.getStockNum() .compareTo(outstock.getNum()) >=0) {
					
					logger.info("method {},stock and LockedNum is enough ,outstockNum is {},stockNum is {}",
							Thread.currentThread().getStackTrace()[1].getMethodName(), stock.getStockNum(),
							outstock.getNum());
					
					RecycleOutstock outstockOld = recycleOutstockDao.getById(outstock.getId(), null);
					if(outstockOld == null){
						logger.info("method {},stock is deleted , that stock.id is {}",
								Thread.currentThread().getStackTrace()[1].getMethodName(),outstock.getId());
						throw new RuntimeException("该出库单已删除！");
					}
										
					//Boolean updateStock = recycleStockService.update(stock);
					count = recycleOutstockDao.update(outstock);
				}*/
				count = recycleOutstockDao.update(outstock);
			}else{
				throw new RuntimeException("没有入过库的规格不能出库！");
			}
		}else{
			count = recycleOutstockDao.update(outstock);
		}
		return count > 0;
		
	}

	@Override
	public LarPager<RecycleChenkExport> chenkExport(LarPager<RecycleChenkExport> larPager, List<Long> list) {
		 List<RecycleChenkExport> result = recycleOutstockDao.chenkExport(larPager, list);
		 larPager.setResult(result);
		 larPager.setTotalCount(recycleOutstockDao.countChenkExport(larPager, list));
	        
		 return larPager;
	}

	@Override
	public Boolean existByOutstockNo(Long outstockId, String outstockNo) {
		
		return recycleOutstockDao.existByOutstockNo(outstockId,outstockNo)>0;
	}
	
	
	@Override
    @Transactional(readOnly = false)
    public Boolean update(RecycleOutstock t) {
        int count = recycleOutstockDao.update(t);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

	@Override
	@Transactional(readOnly = false)
	public Boolean updateImg(Long id) {
		int count=0;
		try {
			count = recycleOutstockDao.updateImg(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (count > 0) {
            return true;
        } else {
            return false;
        }
	}
	
}
