package com.sdcloud.biz.lar.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.sdcloud.api.lar.entity.LarClientUserAddress;
import com.sdcloud.framework.entity.LarPager;

@Repository
public interface LarClientUserAddressDao {
	// 查询总数
	@Select(value = "select count(id) from lar_address")
	int countById();

	// 查询对应用户的地址总数
	@Select(value = "select count(id) from lar_address where larClientUser=#{id} and enable=0")
	int countByUserId(@Param("id") String id);
	// 查询用户和城市查询地址
	@Select(value = "select count(id) from lar_address where larClientUser=#{id} and cityId=#{city} and enable=0 ")
	int countByUserAndCity(@Param("id") String id,@Param("city") String  city);
	@Select(value="select count(id) from lar_address where id=#{id} and larClientUser is not null")
	int selectUserByAddressId(@Param("id") String id);
	
	// 根据id删除
	@Delete(value = "update lar_address set enable=1 where id = #{id}")
	int deleteById(@Param("id") String id);

	// 根据用户id删除
	@Delete(value = "update lar_address set enable=1 where larClientUser = #{id}")
	int deleteByUserId(@Param("id") String id);

	// 添加用户，所有字段
	@Insert(value = "insert into lar_address (id,userName, contact, address, region, detail, provinceId,cityId,areaId, defaultEnable,enable,larClientUser,longitude,latitude) "
			+ "values (#{id},#{userName},#{contact},#{address},#{region},#{detail},#{provinceId.regionId},#{cityId.regionId},#{areaId.regionId},#{defaultEnable},#{enable},#{larClientUser.id},longitude =#{longitude},latitude =#{latitude})")
	int insert(LarClientUserAddress larClientUserAddress);

	int insertSelective(LarClientUserAddress larClientUserAddress);

	// 添加用户，获得ID
	int insertAddressGetId(LarClientUserAddress larClientUserAddress);

	// 查询所有带分页
	List<LarClientUserAddress> selectByExample(
			@Param("larPager") LarPager<LarClientUserAddress> larPager);

	// 根据条件更新
	int updateByExampleSelective(@Param("params") Map<String, Object> params);
	
	@Update(value="update lar_address set defaultEnable=0 where larClientUser=#{larClientUser} and id!=#{id}  and enable=0")
	int updateByDefaultEnable(Map<String, Object> params);

	// 根据id更新
	@Update(value = "update lar_address set userName =#{userName},contact =#{contact},"
			+ "address =#{address},region =#{region},detail =#{detail},provinceId =#{provinceId.regionId},"
			+ "cityId =#{cityId.regionId},areaId =#{areaId.regionId},defaultEnable =#{defaultEnable},larClientUser=#{larClientUser.id},enable =#{enable},longitude =#{longitude},latitude =#{latitude} where id=#{id}")
	int updateById(LarClientUserAddress larClientUserAddress);

	// 更具ID查询
	@Select("select  id, userName, contact, address, region, detail, provinceId,cityId,areaId, defaultEnable,enable,larClientUser as \"larClientUser.id\",longitude,areaType,latitude from lar_address where id=#{id} and enable=0")
	@Results({
			@Result(property = "provinceId", column = "provinceId", one = @One(select = "com.sdcloud.biz.lar.dao.LarClientUserAddressDao.queryProvince")),
			@Result(property = "cityId", column = "cityId", one = @One(select = "com.sdcloud.biz.lar.dao.LarClientUserAddressDao.queryCity")),
			@Result(property = "areaId", column = "areaId", one = @One(select = "com.sdcloud.biz.lar.dao.LarClientUserAddressDao.queryArea")) })
	LarClientUserAddress selectByPrimaryKey1(@Param("id") String id);
	
	@Select("select  id, userName, contact, address, region, detail, provinceId,cityId,areaId,defaultEnable,enable,longitude,areaType,latitude from lar_address where id=#{id}")
	@Results({
		@Result(property = "provinceId", column = "provinceId", one = @One(select = "com.sdcloud.biz.lar.dao.LarClientUserAddressDao.queryProvince")),
		@Result(property = "cityId", column = "cityId", one = @One(select = "com.sdcloud.biz.lar.dao.LarClientUserAddressDao.queryCity")),
		@Result(property = "areaId", column = "areaId", one = @One(select = "com.sdcloud.biz.lar.dao.LarClientUserAddressDao.queryArea")) })
	LarClientUserAddress selectById(@Param("id") String id);

	// 根据用户ID查询,带分页
	List<LarClientUserAddress> selectByUserId(@Param("larPager") LarPager<LarClientUserAddress> larPager);

	@Update(value="update lar_address set defaultEnable=#{defaultEnable} where id=#{id} and enable=0")
	int updateDefaultEnableById(Map<String, Object> params);
	
	List<LarClientUserAddress> selectByIds(@Param("ids") List<String> ids);
	/**
	 * 根据城市ID，查找经纬度非空的联系人地址集合
	 * @author jzc 2016年6月16日
	 * @param cityId
	 * @return
	 */
	@Select("select  id, cityId,longitude,latitude,areaType from lar_address "
			+ "where cityId=#{cityId} and enable=0 and longitude is not null and latitude is not null")
	List<LarClientUserAddress> selectByCityId(@Param("cityId") Long cityId);
	
	/**
	 * 批量更新
	 * @author jzc 2016年6月16日
	 * @param models
	 * @return
	 */
	Integer updateBatch(List<LarClientUserAddress> models);

}