package com.sdcloud.web.lar.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sdcloud.api.core.entity.Org;

/**
 * @author wrs </br>
 *         根据ID从对象列表中获取相关对象的中文名称 </br>
 *         如orgId 获取到orgName
 */
public class KeyToNameUtil {
	/**
	 * orgId convert to orgName
	 * @param <T>
	 * @param orgList 所有机构列表
	 * @param dList 需要转化的对象列表
	 * @param keyMethod 获取id的方法
	 * @param keyMethod 设置value的方法
	 * 
	 */
	public static <T> void convertOrg(List<Org> orgList, List<T> dList, String keyMethod, String valueMethod)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Map orgMap = new<Long, String> HashMap();
		for (Org org : orgList) {
			orgMap.put(org.getOrgId(), org.getName());
		}
		for (T d : dList) {
			Class clazz = d.getClass();
			Method getMethod = clazz.getMethod(keyMethod, null);
			// null表示getName方法没有参数
			Method setMethod = clazz.getMethod(valueMethod, String.class);
			Long key = (Long) getMethod.invoke(d, null);
			setMethod.invoke(d, orgMap.get(key));
		}
	}
}
