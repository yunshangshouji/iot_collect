package zhuboss.gateway.adapter.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ReadInfo  implements Serializable {
    private int readId; //系统编码
    private int cmd; //modbus 功能码
    private int startAddr;
    private int len;
    private byte[] readCommand;//抄表，不含ADDR、CRC
    private List<ProfileInfo> profileInfos = new ArrayList<>();
}
