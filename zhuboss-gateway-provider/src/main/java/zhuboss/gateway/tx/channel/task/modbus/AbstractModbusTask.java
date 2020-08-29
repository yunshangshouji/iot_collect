package zhuboss.gateway.tx.channel.task.modbus;

import lombok.Data;
import zhuboss.gateway.tx.channel.task.AbstractTask;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.tx.meter.modbus.ModbusResponseDecoder;

@Data
public abstract class AbstractModbusTask extends AbstractTask {

    private int addr;

    @Override
    public IResponseDecoder getResponseDecoder() {
        return new ModbusResponseDecoder();
    }
}
