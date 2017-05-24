package com.sdcloud.biz.lar.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import com.sdcloud.api.lar.entity.UserLogin;

@Repository
public interface LoginDao {

	//验证用户名和密码
	@Select(value="select count(id) from lar_clientuser where phone=#{phone} and passWord=#{passWord} and enable=0")
	public int Login();
	
	//验证token，查询内存表
	@Select(value="select id,token,createDate,endDate,expireSecond,clientUserId from lar_tokenheap where token = #{token}")
	public  UserLogin getTokenHeap(@Param("token") String token);
	
	//验证token，查询事务表
	@Select(value="select id,token,createDate,endDate,expireSecond,clientUserId from lar_tokendb where token = #{token}")
	public UserLogin getTokenDb(@Param("token") String token);
	
	//添加token
	@Insert(value="insert into lar_tokenheap (id,token,createDate,endDate,expireSecond,clientUserId) values (#{id},#{token},#{createDate},#{endDate},#{expireSecond},#{clientUserId})")
	public int insertTokenHeap(UserLogin userLogin);
	
	//添加token
	@Insert(value="insert into lar_tokendb (id,token,createDate,endDate,expireSecond,clientUserId) values (#{id},#{token},#{createDate},#{endDate},#{expireSecond},#{clientUserId})")
	public int insertTokenDb(UserLogin userLogin);
	
	//删除token
	@Delete(value="delete from lar_tokenheap where token = #{token}")
	public int deleteTokenHeap(@Param("token") String token);
	
	//删除token
	@Delete(value="delete from lar_tokendb where token = #{token}")
	public int deleteTokenDb(@Param("token") String token);
	
	//删除token
	@Delete(value="delete from lar_tokenheap where clientUserId = #{id}")
	public int deleteTokenHeapById(@Param("id") String id);
	
	//删除token
	@Delete(value="delete from lar_tokendb where clientUserId = #{id}")
	public int deleteTokenDbById(@Param("id") String id);
	
	//修改token
	@Update(value="update lar_tokenheap set id=#{id},token=#{token},createDate=#{createDate},endDate=#{endDate},expireSecond=#{expireSecond},clientUserId=#{clientUserId} where token=#{token}")
	public int updateTokenHeap(UserLogin userLogin);
	
	//修改token
	@Update(value="update lar_tokendb set id=#{id},token=#{token},createDate=#{createDate},endDate=#{endDate},expireSecond=#{expireSecond},clientUserId=#{clientUserId} where token=#{token}")
	public int updateTokenDb(UserLogin userLogin);
}
