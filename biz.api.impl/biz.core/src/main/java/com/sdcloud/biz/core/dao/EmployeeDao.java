
package com.sdcloud.biz.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.sdcloud.api.core.entity.Employee;

/**
 * 
 * @author czz
 *
 */
public interface EmployeeDao {

	/**
	 * 查找部门集合下所有的员工
	 * @param orgIds 部门id集合
	 * @param pager 分页
	 * @return 员工集合
	 */
//	List<Employee> findByOrg(@Param("orgIds")List<Long> orgIds, @Param("pager")Pager<Employee> pager);
	List<Employee> findByOrg(Map<String, Object> param);
	
	/*
	 * 部门下员工总数
	 */
//	long countByOrg(@Param("orgIds")List<Long> orgIds);
	long countByOrg(Map<String, Object> param);
	
	
//	void insert(Employee employee);
	void insert(List<Employee> employees);

	void delete(@Param("employeeIds")List<Long> employeeIds);

	void updateInfo(Employee employee);
	
	void updateHireInfo(Employee employee);
	
	void updateInnerInfo(Employee employee);

	//批量修改员工的部门
	void updateOrg(@Param("employeeIds")List<Long> employeeIds, @Param("newOrgId")long newOrgId);

	List<Employee> findByIds(@Param("employeeIds") List<Long> employeeIds);
	
	Employee findById(Long employeeId);
	Employee findByEmployeeNo(@Param("employeeNo")String employeeNo);
	
	/**
	 * 查询导入文件中已有的员工
	 * @param idents 员工身份证号列表
	 * @return 已有员工身份证列表
	 */
	List<String> findByIdentNo(@Param("idents") List<String> idents);
	
	List<Employee> findAll(Map<String, Object> param);

	List<Employee> findByCompanyIds(@Param("companyIds") List<Long> companyIds);

	
	/**
	 * 批量更改导入文件中已有的员工
	 * @param employees 
	 */
	void batchUpdateEmps(@Param("employees") List<Employee> employees);

	/**
	 * 根据身份证号、通讯卡号查询人员
	 * @param identNo
	 * @return
	 */
	List<Employee> findEmployeeByParam(Map<String, Object> param);
	
}
