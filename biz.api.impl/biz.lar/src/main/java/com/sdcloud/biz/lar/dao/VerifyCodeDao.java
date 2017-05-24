package com.sdcloud.biz.lar.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.sdcloud.api.lar.entity.VerifyCode;

@Repository
public interface VerifyCodeDao {
	// 根据phone删除
	@Delete(value = "delete from  lar_verifycode  where phone = #{phone}")
	int deleteByPhone(@Param("phone") String phone);
	//添加验证码
	@Insert(value = "insert into lar_verifycode (phone,code,createTime,endTime) values (#{phone},#{code},#{createTime},#{endTime})")
	int insert(VerifyCode verifyCode);
	// 根据phone查询
	@Select(value = "select phone,code,createTime,endTime from lar_verifycode  where phone = #{phone}")
	VerifyCode selectByPhone(@Param("phone") String phone);
}