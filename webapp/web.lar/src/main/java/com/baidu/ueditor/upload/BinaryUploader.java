package com.baidu.ueditor.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class BinaryUploader {
	public static final State save(HttpServletRequest request, Map<String, Object> conf) {
		FileItem item = null;
		boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;

		if (!(ServletFileUpload.isMultipartContent(request))) {
			return new BaseState(false, 5);
		}

		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

		if (isAjaxUpload) {
			upload.setHeaderEncoding("UTF-8");
		}
		try {
			List items = upload.parseRequest(request);
			ListIterator iterator = items.listIterator();

			while (iterator.hasNext()) {
				item = (FileItem) iterator.next();

				if (!(item.isFormField()))
					break;
				item = null;
			}

			if (item == null) {
				return new BaseState(false, 7);
			}

			String savePath = (String) conf.get("savePath");
			String originFileName = item.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();
			long fileSize = item.getSize();
			if (!(validFileSize(fileSize, maxSize))) {
				return new BaseState(false, 1);
			}

			if (!(validType(suffix, (String[]) conf.get("allowFiles")))) {
				return new BaseState(false, 8);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = ((String) conf.get("rootPath")) + savePath;

			InputStream is = item.getInputStream();

			State storageState = StorageManager.saveFileByInputStream(is, savePath, fileSize);
			is.close();

			if (storageState.isSuccess()) {
				storageState.putInfo("url", PathFormat.format(savePath));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, 6);
		} catch (IOException localIOException) {
		}
		return new BaseState(false, 4);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List list = Arrays.asList(allowTypes);

		return list.contains(type);
	}

	private static boolean validFileSize(long fileSize, long maxSize) {
		return (fileSize < maxSize);
	}
}