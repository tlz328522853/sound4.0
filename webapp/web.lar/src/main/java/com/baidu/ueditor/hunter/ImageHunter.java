package com.baidu.ueditor.hunter;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MIMEType;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.StorageManager;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ImageHunter {
	private String filename = null;
	private String savePath = null;
	private String rootPath = null;
	private List<String> allowTypes = null;
	private long maxSize = -1L;

	private List<String> filters = null;

	public ImageHunter(Map<String, Object> conf) {
		this.filename = ((String) conf.get("filename"));
		this.savePath = ((String) conf.get("savePath"));
		this.rootPath = ((String) conf.get("rootPath"));
		this.maxSize = ((Long) conf.get("maxSize")).longValue();
		this.allowTypes = Arrays.asList((String[]) conf.get("allowFiles"));
		this.filters = Arrays.asList((String[]) conf.get("filter"));
	}

	public State capture(String[] list) {
		MultiState state = new MultiState(true);

		for (String source : list) {
			state.addState(captureRemoteData(source));
		}

		return state;
	}

	public State captureRemoteData(String urlStr) {
		HttpURLConnection connection = null;
		URL url = null;
		String suffix = null;
		try {
			url = new URL(urlStr);

			if (!(validHost(url.getHost()))) {
				return new BaseState(false, 201);
			}

			connection = (HttpURLConnection) url.openConnection();

			connection.setInstanceFollowRedirects(true);
			connection.setUseCaches(true);

			if (!(validContentState(connection.getResponseCode()))) {
				return new BaseState(false, 202);
			}

			suffix = MIMEType.getSuffix(connection.getContentType());

			if (!(validFileType(suffix))) {
				return new BaseState(false, 8);
			}

			if (!(validFileSize(connection.getContentLength()))) {
				return new BaseState(false, 1);
			}

			String savePath = getPath(this.savePath, this.filename, suffix);

			State state = StorageManager.saveFileByInputStream(connection.getInputStream(), savePath,
					connection.getContentLengthLong());

			if (state.isSuccess()) {
				state.putInfo("url", PathFormat.format(savePath));
				state.putInfo("source", urlStr);
			}

			return state;
		} catch (Exception e) {
		}
		return new BaseState(false, 203);
	}

	private String getPath(String savePath, String filename, String suffix) {
		return PathFormat.parse(savePath + suffix, filename);
	}

	private boolean validHost(String hostname) {
		try {
			InetAddress ip = InetAddress.getByName(hostname);

			if (ip.isSiteLocalAddress())
				return false;
		} catch (UnknownHostException e) {
			return false;
		}

		return (!(this.filters.contains(hostname)));
	}

	private boolean validContentState(int code) {
		return (200 == code);
	}

	private boolean validFileType(String type) {
		return this.allowTypes.contains(type);
	}

	private boolean validFileSize(int size) {
		return (size < this.maxSize);
	}
}