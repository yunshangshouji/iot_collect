package zhuboss.gateway.service.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DevInfoControl {
    private final int pmtjn = 0; //	uint8_t	是否允许加网，本项目恒定为0
    private int type; //设备类型 0 = modbus设备;  10 = 645设备97协议; 11 = 645设备07协议
    private final List<DevInfoControlProfile> profile = new ArrayList<>();
    private final List<DevInfoControlLoop> loop = new ArrayList<>();
}
