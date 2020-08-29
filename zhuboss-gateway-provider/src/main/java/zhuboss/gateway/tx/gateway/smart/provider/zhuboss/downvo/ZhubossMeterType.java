package zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo;

import lombok.Data;
import zhuboss.gateway.adapter.bean.Dlt645Var;

import java.util.List;

@Data
public class ZhubossMeterType {
    private Integer baudRate;

    private String parity;

    private Integer byteSize;

    private Integer stopBits;

    private Integer readMillSeconds;

    /**
     * 1:Modbus
     * 2:Dlt645-2007
     */
    private String protocol;
    private String protocol2; //子协议，主要是PLC

    /**
     * 抄表指令集合(当仪表类型是MODBUS时)
     */
    private List<ZhubossRead> reads;

    /**
     * 当仪表协议是DLT645-2007时
     */
    private List<Dlt645Var> dltVars;

    /**
     * 当仪表为PLC设备时,Addr2为字符串
     */
    private List<ZhubossProfile> plcVars;

}
