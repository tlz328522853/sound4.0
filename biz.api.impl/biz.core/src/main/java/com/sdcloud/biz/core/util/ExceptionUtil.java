package com.sdcloud.biz.core.util;

import org.springframework.dao.DuplicateKeyException;

public class ExceptionUtil {

	/**
	 * 分析异常类型，若违反某种数据库索引机制，则抛出异常，前端弹窗显示
	 * 
	 * @param e
	 * @param dbIndexName
	 * @param exceptionMsg
	 * @throws Exception
	 */
	public static void analysisException(Exception e, final String dbIndexName, String exceptionMsg) throws Exception {
		if (e instanceof DuplicateKeyException) {
			String msg = e.getMessage();
			int a = msg.indexOf("for key '");
			msg = msg.substring(a + 9);
			int b = msg.indexOf("'");
			msg = msg.substring(0, b);
			if (msg != null && msg.equals(dbIndexName)) {
				throw new RuntimeException(exceptionMsg);
			}
		}
	}

}
