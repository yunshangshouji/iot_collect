package zhuboss.gateway.tx.channel.task.modbus;

import lombok.Data;
import zhuboss.gateway.adapter.bean.ReadInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ModbusReadTask extends AbstractModbusTask {


    /**
     * 应用实例，防止运行中元数据修改
     */
    private List<ReadInfo> readInfoList;

    /**
     * 返回报文隶属的Read指令
     */
    private int idxReadInfo = 0;
    private Map<String,Object> values = new HashMap<>();

    public ModbusReadTask(Integer addr, List<ReadInfo> readInfoList) {
        super.setAddr(addr);
        this.readInfoList = readInfoList;
    }

    @Override
    public String getHashAddr() {
        return "R"+ this.getAddr() ;
    }
}
