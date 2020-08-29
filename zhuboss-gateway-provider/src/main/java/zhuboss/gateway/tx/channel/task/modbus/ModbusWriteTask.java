package zhuboss.gateway.tx.channel.task.modbus;

import lombok.Data;
import zhuboss.gateway.tx.meter.modbus.ModbusMessage;

@Data
public class ModbusWriteTask extends AbstractModbusTask {

    private String taskUUID;

    private int cmd;

    private byte[] writeBytes;

    private ModbusMessage receive;

    @Override
    public String getHashAddr() {
        return "W"+ this.getAddr() ;
    }

    public ModbusWriteTask(String taskUUID, int cmd, byte[] writeBytes) {
        this.taskUUID = taskUUID;
        this.cmd = cmd;
        this.writeBytes = writeBytes;
    }

}
