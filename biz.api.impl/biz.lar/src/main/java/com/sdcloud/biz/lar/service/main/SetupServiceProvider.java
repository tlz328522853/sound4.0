package com.sdcloud.biz.lar.service.main;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sdcloud.framework.common.InitLogger;
import com.sdcloud.framework.common.ProjectHome;
import com.sdcloud.framework.common.ServiceConstants;

public class SetupServiceProvider {
	/**
	 * 
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SetupServiceProvider.class);
	/**
	 * FileLock 对象。
	 */
	private static FileLock lock = null;

	public static void main(String[] args) {

		try {

			boolean runningInEclipse = true;

			ProjectHome.getInstance().setPROJECT_HOME(runningInEclipse);

			InitLogger o = new InitLogger();
			o.initLogger();

			if (!isRunning()) {

				FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
						new String[] { ServiceConstants.DUBBO_CONTEXT_CONF_FILE });
				
				context.start();
				
				logger.info(" provider is setting up!! ");
				
				System.in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过是否获得指定文件的锁，判断本进程是否已启动。
	 * 
	 * @return true表示本进程已经被启动，false表示本进程尚未被启动。
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private static boolean isRunning() throws Exception {

		boolean isRun = false;
		String lockFile = ServiceConstants.PROCESS_INSTANCE;
		try {
			// 获得实例标志文件
			File file = new File(lockFile);
			// 如果不存在就新建一个
			if (!file.exists()) {
				file.createNewFile();
			}
			// 获得文件锁
			lock = new FileOutputStream(lockFile).getChannel().tryLock();

			// 返回空表示文件已被运行的实例锁定.即已有其他进程实例被启动。
			if (lock == null) {
				isRun = true;
				logger.info("process.instance文件已被运行的实例锁定.退出启动!");
				System.exit(0);
			}

		} catch (Exception e) {
			throw new Exception("执行isRunning方法时发生异常！", e);
		}
		return isRun;
	}
}
