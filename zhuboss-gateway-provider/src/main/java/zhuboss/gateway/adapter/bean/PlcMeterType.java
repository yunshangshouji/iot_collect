package zhuboss.gateway.adapter.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlcMeterType extends MeterType {
    private String protocol; //PLC 协议：厂家&型号
    private List<ProfileInfo> profileInfos = new ArrayList<>();
}
