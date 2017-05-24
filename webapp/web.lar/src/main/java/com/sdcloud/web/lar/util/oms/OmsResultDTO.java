package com.sdcloud.web.lar.util.oms;

import com.sdcloud.framework.entity.ResultDTO;

/**
 * return result class for oms web response like
 * {
	 "exceptionMsg":null,
	 "message":Object{...},
	 "result":true
	 }
 * 
 * @author lrj
 *
 */
public class OmsResultDTO {

	private String exceptionMsg;

	private OmsPager<Object> message;

	private boolean result;

	public OmsResultDTO() {
	}

	//OmsResultDTO转换为ResultDTO
	public ResultDTO getResultDTO(){
		if(result)
			return ResultDTO.getSuccess(200, exceptionMsg, message.getLarPager());
		else
			return ResultDTO.getFailure(500, exceptionMsg);
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public OmsPager<Object> getMessage() {
		return message;
	}

	public void setMessage(OmsPager<Object> message) {
		this.message = message;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
}
