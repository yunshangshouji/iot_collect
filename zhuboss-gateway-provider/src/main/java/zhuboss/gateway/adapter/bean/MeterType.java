package zhuboss.gateway.adapter.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public abstract class MeterType implements Serializable {
	private String name;
	private String remark;

	private Integer baudRate;

	private String parity;

	private Integer byteSize;

	private Integer stopBits;

	private Integer readMillSeconds;
}
