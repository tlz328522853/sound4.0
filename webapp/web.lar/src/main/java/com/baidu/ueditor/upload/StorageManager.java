package com.baidu.ueditor.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.sdcloud.web.lar.util.FileOperationsUtils;
import com.sdcloud.web.lar.util.SardineUtil;

public class StorageManager {
	public static State saveBinaryFile(byte[] data, String path) {
		try {
			InputStream is = new ByteArrayInputStream(data);
			State state = uploadFile(is, path, data.length);
			state.putInfo("size", data.length);
			return state;
		} catch (Exception ioe) {
		}
		return new BaseState(false, 4);
	}

	public static State saveFileByInputStream(InputStream is, String path, long fileSize) {
		try {
			State state = uploadFile(is, path, fileSize);
			return state;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BaseState(false, 4);
	}

	private static State uploadFile(InputStream is, String path, long fileSize) throws Exception {
		int lastIndexOf = path.lastIndexOf("/");
		String filePath = path.substring(0, lastIndexOf + 1);
		String fileName = path.substring(lastIndexOf + 1);
		FileOperationsUtils.fileIsUpload(is, filePath, fileName);
		State state = new BaseState(true);
		state.putInfo("size", fileSize);
		state.putInfo("title", fileName);
		return state;
	}

	private static File getTmpFile() {
		File tmpDir = FileUtils.getTempDirectory();
		String tmpFileName = (Math.random() * 10000.0D + "").replace(".", "");
		return new File(tmpDir, tmpFileName);
	}

	private static State saveTmpFile(File tmpFile, String path) {
		State state = null;
		File targetFile = new File(path);

		if (targetFile.canWrite())
			return new BaseState(false, 2);
		try {
			FileUtils.moveFile(tmpFile, targetFile);
		} catch (IOException e) {
			return new BaseState(false, 4);
		}

		state = new BaseState(true);
		state.putInfo("size", targetFile.length());
		state.putInfo("title", targetFile.getName());

		return state;
	}

	private static State valid(File file) {
		File parentPath = file.getParentFile();

		if ((!(parentPath.exists())) && (!(parentPath.mkdirs()))) {
			return new BaseState(false, 3);
		}

		if (!(parentPath.canWrite())) {
			return new BaseState(false, 2);
		}

		return new BaseState(true);
	}
}