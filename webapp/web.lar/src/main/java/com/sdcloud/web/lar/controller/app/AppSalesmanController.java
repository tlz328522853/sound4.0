package com.sdcloud.web.lar.controller.app;

import com.sdcloud.api.core.entity.Employee;
import com.sdcloud.api.core.entity.Project;
import com.sdcloud.api.core.service.EmployeeService;
import com.sdcloud.api.core.service.ProjectService;
import com.sdcloud.api.envmapdata.service.RecordService;
import com.sdcloud.api.lar.entity.City;
import com.sdcloud.api.lar.entity.OwnedSupplier;
import com.sdcloud.api.lar.entity.RecoveryHouse;
import com.sdcloud.api.lar.entity.Salesman;
import com.sdcloud.api.lar.service.CityService;
import com.sdcloud.api.lar.service.RecoveryHouseService;
import com.sdcloud.api.lar.service.SalesmanService;
import com.sdcloud.framework.entity.LarPager;
import com.sdcloud.framework.entity.ResultDTO;
import com.sdcloud.framework.exception.AppCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/salesman")
public class AppSalesmanController {

    @Autowired
    private CityService cityService;
    @Autowired
    private SalesmanService salesmanService;
    @Autowired
    private RecoveryHouseService recoveryHouseService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    RecordService recordService;
    @Autowired
    private ProjectService projectService;
    

    /**
	 * 根据城市查询回收亭，回收车
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */

	@SuppressWarnings({"rawtypes", "unchecked" })
	@RequestMapping("/getSalesmansAndHouse")
	@ResponseBody
	public ResultDTO getSalesmansAndHouse(@RequestBody(required = false) Map<String, Object> map) throws Exception {
		try {
			Long cityId = Long.valueOf((Integer) map.get("city") + "");// 获取城市
			City city = cityService.selectByCityId(cityId);

			LarPager<Salesman> larPager = new LarPager<>(1000000);// 回收车(业务员)
			Map<String, Object> param = new HashMap<>();
			param.put("mechanismId", city.getOrg());
			larPager.setParams(param);
			larPager = salesmanService.selectByExample(larPager);
			List devList = new ArrayList();
			for (Salesman sale : larPager.getResult()) {
				Employee emp = null;
				if(null != sale.getPersonnelId() && !"".equals(sale.getPersonnelId())){
					emp = employeeService.findById(Long.valueOf(sale.getPersonnelId()));
				}
				String device = emp == null ? null : emp.getMobileMac();
				if (device != null) {
					int length=12-device.length();
					while(length>0){
						device="0"+device;
						length--;
					}
					devList.add(device);
					sale.setMobileMac(device);
				}
			}
			Project project = projectService.findById(city.getOrg());
			List<Map<String, String>> carRecords = recordService.findRealTimeRecords(devList, "P0000001");
			Map carRecordMap = new HashMap();
			for (Map m : carRecords) {
				carRecordMap.put(m.get("devidStr"), m);
			}
			int size=larPager.getResult().size();
			List recoveryCarList=new ArrayList();
			for (int i = 0; i <size; i++) {
				Salesman sale = larPager.getResult().get(i);
				if(project != null){
					sale.setLatitude(project.getLatitude()+"");
					sale.setLongitude(project.getLongitude()+"");
				}
				Map location=(Map)carRecordMap.get(sale.getMobileMac());
				if (carRecordMap.get(sale.getMobileMac()) != null) {
					if (location.get("bd_lon") == null || location.get("bd_lat") == null) {
						continue;
					}
					sale.setLatitude(location.get("bd_lat").toString());
					sale.setLongitude(location.get("bd_lon").toString());
				} 
				recoveryCarList.add(sale);
			}
			/*
			 * for(int i = 0;i < list.size();i++){ String b = list.get(i);
			 * if(b.equals("502323232")){ list.remove(i); i--; } }
			 */
			LarPager<RecoveryHouse> lar = new LarPager<>(1000000);// 回收亭
			param.clear();
			param.put("org", city.getOrg());
			lar.setParams(param);
			lar = recoveryHouseService.findAll(lar);
			Map<String, Object> result = new HashMap<>();
			result.put("recoveryCar", recoveryCarList);
			result.put("recoveryHouse", lar.getResult());
			return ResultDTO.getSuccess(AppCode.SUCCESS, result);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.getFailure(AppCode.SYSTEM_ERROR, "系统错误");
		}
	}

    // 查询列表
    @RequestMapping("/getSalesmans")
    @ResponseBody
    public ResultDTO getSalesmans(@RequestBody(required = false) LarPager<Salesman> larPager) throws Exception {
        try {
            larPager = salesmanService.selectByExample(larPager);
        } catch (Exception e) {
            throw e;
        }
        return ResultDTO.getSuccess(larPager);
    }

    // 根据机构ID查询供应商列表
    @RequestMapping("/getOwnedSuppliersById/{id}")
    @ResponseBody
    public ResultDTO getOwnedSuppliersById(@PathVariable(value = "id") String id) throws Exception {
        try {
            if (id != null && id.trim().length() > 0) {
                List<OwnedSupplier> ownedSuppliers = salesmanService.getOwnedSuppliersById(id);
                if (ownedSuppliers != null && ownedSuppliers.size() > 0) {
                    return ResultDTO.getSuccess(ownedSuppliers);
                } else {
                    return ResultDTO.getFailure(500, "该机构没有供货商！");
                }
            } else {
                return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = "/saveSalesman", method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO save(@RequestBody(required = false) Salesman salesman) throws Exception {
        LarPager<Salesman> result = null;
        try {
            if (salesman != null && (salesman.getId() == null || salesman.getId().length() <= 0)) {
                boolean insertUserGetId = salesmanService.insertSelective(salesman);
                if (insertUserGetId) {
                    result = new LarPager<Salesman>();
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("mechanismId", salesman.getAreaSetting().getMechanismId());
                    result.setParams(params);
                    result = salesmanService.selectByExample(result);
                    return ResultDTO.getSuccess(result);
                } else {
                    return ResultDTO.getFailure(500, "订单添加失败！");
                }
            } else {
                return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = "/deleteSalesman/{id}/{mechanismId}", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO delete(@PathVariable(value = "id") String id,
                            @PathVariable(value = "mechanismId") String mechanismId) throws Exception {
        LarPager<Salesman> result = null;
        try {
            if (id != null && id.trim().length() > 0 && mechanismId != null && mechanismId.trim().length() > 0) {
                boolean deleteById = salesmanService.deleteById(id);
                if (deleteById) {
                    result = new LarPager<Salesman>();
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("mechanismId", mechanismId);
                    result.setParams(params);
                    result = salesmanService.selectByExample(result);
                    return ResultDTO.getSuccess(result);
                } else {
                    return ResultDTO.getFailure(500, "人员删除失败！");
                }
            } else {
                return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @RequestMapping(value = "/updateSalesman", method = RequestMethod.POST)
    public ResultDTO update(@RequestBody(required = false) Salesman salesman) throws Exception {
        LarPager<Salesman> result = null;
        try {
            if (salesman != null && salesman.getId() != null && salesman.getId().trim().length() > 0) {
                boolean updateByExampleSelective = salesmanService.updateByExampleSelective(salesman);
                if (updateByExampleSelective) {
                    result = new LarPager<Salesman>();
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("mechanismId", salesman.getAreaSetting().getMechanismId());
                    result.setParams(params);
                    result = salesmanService.selectByExample(result);
                    return ResultDTO.getSuccess(result);
                } else {
                    return ResultDTO.getFailure(500, "人员修改失败！");
                }
            } else {
                return ResultDTO.getFailure(400, "非法请求，请重新尝试！");
            }
        } catch (Exception e) {
            throw e;
        }
    }
}