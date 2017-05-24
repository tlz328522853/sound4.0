package com.sdcloud.biz.lar.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.LarClientUser;
import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface LarClientUserDao {
	// 查询总数
	@Select(value = "select count(c.id) from lar_clientuser  c left outer join lar_address a on(c.id=a.larClientUser)  where c.enable = 0 and defaultEnable=1")
	int countById();
	// 根据手机号查询数量
	@Select(value = "select count(c.id) from lar_clientuser c where c.phone = #{phone}")
	int countByPhone(String phone);
	// 根据id删除
	@Delete(value = "update lar_clientuser set enable=1 where id = #{id}")
	int deleteById(@Param("id") String id);

	// 添加用户，所有字段
	@Insert(value = "insert into lar_clientuser (id,customerId, phone,`passWord`,`name`, sex, age,`level`, iconUrl, nowPoints, historyPoints,"
			+ "qrCodeId,qrCodeUrl, createDate, `enable`,regCity,regCityName) values (#{id},#{customerId},#{phone},#{passWord},#{name},"
			+ "#{sex},#{age},#{level},#{iconUrl},#{nowPoints},#{historyPoints},#{qrCodeId},#{qrCodeUrl},#{createDate},#{enable},#{regCity},#{regCityName})")
	int insert(LarClientUser larClientUser);

	// 添加用户，指定字段
	int insertSelective(LarClientUser larClientUser);

	// 添加用户，指定字段，获得ID
	int insertUserGetId(LarClientUser larClientUser);

	// 根据条件查询所有信息
	List<LarClientUser> selectByExample(@Param("larPager") LarPager<LarClientUser> larPager);
	
	// 根据用户名和密码查询用户
	LarClientUser selectUserByPass(@Param("params") Map<String, Object> params);

	// 根据条件更改删除状态
	int deleteByExample(@Param("params") Map<String, Object> params);

	// 根据id查询
	@Select(value = "select id,customerId, phone, passWord, name, sex, age, level, iconUrl, nowPoints, historyPoints,"
			+ "qrCodeId, qrCodeUrl,createDate, enable,cityName,address,addressDetail,longitude,latitude  from lar_clientuser  where id = #{id}")
	LarClientUser selectByPrimaryKey(@Param("id") String id);

	// 更新用户，指定字段(有条件的)
	int updateByExampleSelective(@Param("params") Map<String, Object> params);

	// int updateByExampleSelective(@Param("record")
	// LarClentUserrecord,@Param("params") Map<String, Object> params);
	// 根据id更新
	@Update(value = "update lar_clientuser set customerId =#{customerId},phone =#{phone},passWord =#{passWord},"
			+ "name =#{name},sex =#{sex},age =#{age},level =#{level},iconUrl =#{iconUrl},nowPoints =#{nowPoints},historyPoints =#{historyPoints},"
			+ "qrCodeId =#{qrCodeId},qrCodeUrl =#{qrCodeUrl},createDate =#{createDate},enable =#{enable} where id={id}")
	int updateById(@Param("larClientUserAddress") LarClientUserAddress larClientUserAddress);
	
	@Update(value = "update lar_clientuser set name =#{name},iconUrl =#{iconUrl},"
			+ "cityId =#{cityId},cityName =#{cityName},address =#{address},addressDetail =#{addressDetail},"
			+ "longitude =#{longitude},latitude =#{latitude} "
			+ " where id=#{id}")
	int updateInfo(LarClientUser larClientUser);

	long getClientUsersCount(@Param("params") Map<String, Object> params);

	List<LarClientUser> getClientUsers(@Param("larPager") LarPager<LarClientUser> pager);

	@Select("SELECT id,customerId,nowPoints,historyPoints FROM `lar_clientuser` WHERE customerId = #{userId} AND ENABLE=0")
	LarClientUser getMyPoints(@Param("userId") String userId);

	@Update("update lar_clientuser set nowPoints=nowPoints-#{points} where id=#{appUserId}")
	void updateUserPoints(Map<String, Object> params);
	
	@Update("update lar_clientuser set nowPoints=nowPoints+#{points} where id=#{appUserId}")
	void returnUserPoints(Map<String, Object> params);
	
	// 更新消息推送状态
	@Update(value = "update lar_clientuser set msgFlag=#{msgFlag} where id = #{userId}")
	int updateMsgFlag(@Param("userId") String userId,@Param("msgFlag") Integer msgFlag) throws Exception;
	@Select("SELECT msgFlag FROM `lar_clientuser` WHERE id = #{userId} ")
	int findMsgFlag(String userId) throws Exception;
	/**
	 * 检索客户的信息（通过手机号）
	 * 过滤非该城市的信息
	 * @author jzc 2016年6月20日
	 * @param larClientUser
	 * @return
	 */
//	@Select("SELECT id,customerId,name,phone,address,addressDetail FROM `lar_clientuser` "
//			+ "WHERE name REGEXP #{cinfo} OR phone REGEXP #{cinfo} "
//			+ "AND cityId=#{cityId} AND enable=0")
	@Select("SELECT id,customerId,name,phone,address,addressDetail,nowPoints FROM `lar_clientuser` "
			+ "WHERE phone = #{cinfo} AND enable=0")
	List<LarClientUser> matchLarClientUser(LarClientUser larClientUser) throws Exception;
	
	LarClientUser findByPhone(@Param("phone") String phone);
	
	@Update("update lar_clientuser set nowPoints=#{nowPoints},historyPoints=#{historyPoints} where id=#{id}")
	int updatePoints(LarClientUser larClientUser);
	
	//查询环卫上报的大众用户
	List<LarClientUser> queryClientUser(@Param("list")Collection<Long> list) throws Exception;
	//姓名模糊查询环卫上报的大众用户
	List<Long> queryClientUserByName(@Param("name")String name) throws Exception;
}