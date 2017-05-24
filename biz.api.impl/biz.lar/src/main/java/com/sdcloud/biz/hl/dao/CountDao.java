package com.sdcloud.biz.hl.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.hl.entity.CountChartEntry;
import com.sdcloud.api.hl.entity.CountNumEntry;

/**
* @author jzc
* @version 2017年5月12日 下午1:51:58
* CountService描述: 所有统计图表的接口
*/
public interface CountDao {
    
	/**
	 * 统计四个类型总数
	 * @return 
	 */
	CountNumEntry countNum();
	/**
	 * 统计用户图表日
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countUserChartDate(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计用户图表月
	 * @param endTime 
	 * @param startTime 
	 */
	List<CountChartEntry> countUserChartMonth(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计快递收发图表日
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countExpressChartDate(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计快递收发图表月
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countExpressChartMonth(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计回收量图表日
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countRecyleChartDate(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计回收量图表月
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countRecyleChartMonth(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计生活缴费图表日
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countLifeChartDate(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计生活缴费图表月
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countLifeChartMonth(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计便民回收图表日
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countConveRecyleDate(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计生活缴费图表月
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countConveRecyleMonth(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计便民快递图表日
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countExpressDate(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 统计便民快递图表月
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countExpressMonth(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 根据服务厅统计回收量
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countRecyle(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
	/**
	 * 根据便民厅统计便民快递量
	 * @param endTime 
	 * @param startTime 
	 * @return 
	 */
	List<CountChartEntry> countExpress(@Param("startTime") Date startTime,@Param("endTime")  Date endTime);
}
