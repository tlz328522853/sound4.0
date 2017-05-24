package com.sdcloud.biz.lar.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.Voucher;

/**
 * Created by 韩亚辉 on 2016/4/7.
 */

@Repository
public interface VoucherDao extends BaseDao<Voucher> {
	/**
	 * 根据客户ID查找优惠券
	 * @param customerId
	 * @return
	 */
	 List<Voucher>  findByCus(Long customerId);
	 
     /**
      * 获取优惠券发放人用户列表
      * @author jzc 2016年6月25日
      * @return
      */
    @Select(value="select release_user from lar_voucher group by release_user")
	List<Long> getRelUsers() throws Exception;
}
