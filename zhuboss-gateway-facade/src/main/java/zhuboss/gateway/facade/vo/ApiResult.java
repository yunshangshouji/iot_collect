package zhuboss.gateway.facade.vo;


import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class ApiResult<T> implements Serializable {
	public static final String SUCCESS_MSG = "成功" ;
	
	
	/** 系统返回码 */
	@ApiModelProperty("返回码，true成功,false失败")
	private Boolean result;

	@ApiModelProperty("失败信息")
	private String msg;

	@ApiModelProperty("数据,一般为空")
	private T data;
	
	public ApiResult(Boolean result, String msg) {
		this.result = result;
		this.msg = msg;
	}

	public ApiResult(Boolean result, String msg, T value) {
		this.result = result;
		this.msg = msg;
		this.data = value;
	}

	public ApiResult() {
		result = true;
		msg = SUCCESS_MSG;
	}

	public Boolean getResult() {
		return result;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
