package com.sdcloud.biz.lar.service.impl;

import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.entity.Score;
import com.sdcloud.api.lar.service.SalesManScoreService;
import com.sdcloud.biz.lar.dao.SalesManScoreDao;
import com.sdcloud.biz.lar.dao.SalesmanDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 韩亚辉 on 2016/4/11.
 */
@Service
public class SalesManScoreServiceImpl extends BaseServiceImpl<Score> implements SalesManScoreService {
    @Autowired
    private SalesManScoreDao salesManScoreDao;
    @Autowired
    private SalesmanDao salesmanDao;

    @Override
    @Transactional(readOnly = false)
    public ResultDTO saveScore(Score t) {
        Long uuid = UUIDUtil.getUUNum();
        Date date = new Date();
        t.setId(uuid);
        t.setCreateDate(date);
        t.setCreateUser(uuid);
        int count = salesManScoreDao.save(t);
        if (count > 0) {
            Salesman salesman = salesmanDao.getSalesManById(t.getSalesMan() + "");
            int score = 0;
            if (null != t.getRechargeScore()) {
                score += t.getRechargeScore().intValue();
                // 累计充值积分量
                salesman.setRechargeIntegral(salesman.getRechargeIntegral()+t.getRechargeScore());
            }
            if (null != t.getGiveScore()) {
                score += t.getGiveScore();
                // 累计赠送的
                salesman.setGiveIntegral(salesman.getGiveIntegral()+t.getGiveScore());
            }
            salesman.setIntegral(score+salesman.getIntegral());
            Map<String, Object> map = new HashMap<>();
            map.put("salesman", salesman);
            salesmanDao.updateByExampleSelective(map);
          
            return ResultDTO.getSuccess(200, "充值成功！",null);
        } else {
            return ResultDTO.getFailure(500, "充值失败！");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ResultDTO updateScore(Score t) {
        Score score = salesManScoreDao.getById(t.getId(),null);
        Salesman salesman = salesmanDao.getSalesManById(t.getSalesMan() + "");
        int resultScore = 0;//当前充值记录上的充值积分和赠送积分总和
        int recharge_score = 0;//当前充值记录上的充值积分
        int give_score = 0;//当前充值记录上的赠送积分
        t.setUpdateDate(new Date());
        t.setUpdateUser(UUIDUtil.getUUNum());
        if (null != score.getGiveScore() && null != t.getGiveScore()) {
            resultScore += t.getGiveScore() - score.getGiveScore();
            give_score += t.getGiveScore() - score.getGiveScore(); 
        }
        if (null != score.getRechargeScore() && null != t.getRechargeScore()) {
            resultScore += t.getRechargeScore() - score.getRechargeScore();
            recharge_score += t.getRechargeScore() - score.getRechargeScore();
        }
        if(salesman.getIntegral() + resultScore>=0){
	        int count = salesManScoreDao.update(t);
	        if (count > 0) {
	            salesman.setIntegral(salesman.getIntegral() + resultScore);
	            salesman.setRechargeIntegral(salesman.getRechargeIntegral() + recharge_score);
	            salesman.setGiveIntegral(salesman.getGiveIntegral() + give_score);
	            Map<String, Object> map = new HashMap<>();
	            map.put("salesman", salesman);
	            salesmanDao.updateByExampleSelective(map);
	            return ResultDTO.getSuccess(200, "修改成功！",null);
	        } else {
	            return ResultDTO.getFailure(500, "修改失败！");
	        }
        }else{
        	return ResultDTO.getFailure(500, "前后修改积分差值大于业务员当前积分，不准修改！");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ResultDTO deleteScore(Long id) {
        Score score = salesManScoreDao.getById(id,null);
        Salesman salesman = salesmanDao.getSalesManById(score.getSalesMan() + "");
        int resultScore = 0;//当前充值记录上的充值积分和赠送积分总和
        int recharge_score = 0;//当前充值记录上的充值积分
        int give_score = 0;//当前充值记录上的赠送积分
        if (null != score.getGiveScore()) {
            resultScore += score.getGiveScore();
            give_score+= score.getGiveScore();
        }
        if (null != score.getRechargeScore()) {
            resultScore += score.getRechargeScore();
            recharge_score+= score.getRechargeScore();
        }
        if((salesman.getIntegral() - resultScore)>=0){
	        int count = salesManScoreDao.delete(id);
	        if (count > 0) {
	            salesman.setIntegral(salesman.getIntegral() - resultScore);
	            salesman.setRechargeIntegral(salesman.getRechargeIntegral() - recharge_score);
	            salesman.setGiveIntegral(salesman.getGiveIntegral() - give_score);
	            Map<String, Object> map = new HashMap<>();
	            map.put("salesman", salesman);
	            salesmanDao.updateByExampleSelective(map);
	            return ResultDTO.getSuccess(200, "删除成功！",null);
	        } else {
	            return ResultDTO.getFailure(500, "删除失败！");
	        }
        }else{
        	return ResultDTO.getFailure(500, "当前记录的充值积分和赠送积分之和大于业务员当前积分，不准删除！");
        }
    }

	@Override
	public List<Long> getEchargeUser(LarPager<Score> larPager, List<Long> ids) {
		if(null != ids){
			larPager.getParams().put("org", null);
		}
		return salesManScoreDao.getEchargeUser(larPager,ids);
	}
}
