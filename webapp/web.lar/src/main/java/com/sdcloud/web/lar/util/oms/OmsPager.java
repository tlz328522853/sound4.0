package com.sdcloud.web.lar.util.oms;

import com.sdcloud.framework.entity.LarPager;

import java.io.Serializable;
import java.util.List;


/**
	 * {
		 "Data":Array[25],
		 "PageCount":37,
		 "PageNum":1,
		 "PageSize":25,
		 "RecordCount":911
	 }
 * @param <T>
 */
public class OmsPager<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Object> Data;

	private int PageCount;
	private int PageNum;
	private int PageSize;
	private int RecordCount;

	public OmsPager() {
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public List<Object> getData() {
		return Data;
	}

	public void setData(List<Object> data) {
		Data = data;
	}

	public int getPageCount() {
		return PageCount;
	}

	public void setPageCount(int pageCount) {
		PageCount = pageCount;
	}

	public int getPageNum() {
		return PageNum;
	}

	public void setPageNum(int pageNum) {
		PageNum = pageNum;
	}

	public int getPageSize() {
		return PageSize;
	}

	public void setPageSize(int pageSize) {
		PageSize = pageSize;
	}

	public int getRecordCount() {
		return RecordCount;
	}

	public void setRecordCount(int recordCount) {
		RecordCount = recordCount;
	}

	//转换为LarPager
	public LarPager<Object> getLarPager(){
		LarPager<Object> larPager = new LarPager();
		larPager.setPageNo(PageNum);
		larPager.setPageSize(PageSize);
		larPager.setTotalCount(RecordCount);
		larPager.setResult(Data);
		return larPager;
	}

}
