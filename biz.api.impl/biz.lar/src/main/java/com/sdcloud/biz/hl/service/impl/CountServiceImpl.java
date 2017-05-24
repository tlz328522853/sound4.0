package com.sdcloud.biz.hl.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdcloud.api.hl.entity.CountChartEntry;
import com.sdcloud.api.hl.entity.CountNumEntry;
import com.sdcloud.api.hl.service.CountService;
import com.sdcloud.biz.hl.dao.CountDao;

/**
* @author jzc
* @version 2017年5月12日 下午1:58:21
* CountServiceImpl描述:
*/
@Service
public class CountServiceImpl implements CountService {

	@Autowired
	private CountDao countDao;
	
	@Override
	public CountNumEntry countNum() {
		// TODO Auto-generated method stub
		return countDao.countNum();
	}


	@Override
	public List<CountChartEntry> countUserChartMonth(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countUserChartMonth(startTime,endTime);
	}



	@Override
	public List<CountChartEntry> countUserChartDate(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countUserChartDate(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countExpressChartDate(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countExpressChartDate(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countExpressChartMonth(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countExpressChartMonth(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countRecyleChartDate(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countRecyleChartDate(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countRecyleChartMonth(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countRecyleChartMonth(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countLifeChartDate(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countLifeChartDate(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countLifeChartMonth(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countLifeChartMonth(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countConveRecyleDate(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countConveRecyleDate(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countConveRecyleMonth(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countConveRecyleMonth(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countExpressDate(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countExpressDate(startTime,endTime);
	}

	@Override
	public List<CountChartEntry> countExpressMonth(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countExpressMonth(startTime,endTime);
	}


	@Override
	public List<CountChartEntry> countRecyle(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countRecyle(startTime, endTime);
	}


	@Override
	public List<CountChartEntry> countExpress(Date startTime, Date endTime) {
		// TODO Auto-generated method stub
		return countDao.countExpress(startTime, endTime);
	}

	

}
