package zhuboss.gateway.tx.channel.task.dlt645;

import lombok.Data;
import zhuboss.gateway.adapter.bean.Dlt645Var;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.tx.channel.task.AbstractTask;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.tx.meter.dlt645.Dlt645ResponseDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Dlt645ReadTask extends AbstractTask {

    ProtocolEnum protocolEnum;

    private long addr;

    private List<Dlt645Var> dlt645VarList;

    private int idxReadInfo = 0;
    private Map<String,Object> values = new HashMap<>();

    public Dlt645ReadTask(ProtocolEnum protocolEnum, long addr, List<Dlt645Var> dlt645VarList) {
        this.protocolEnum = protocolEnum;
        this.addr = addr;
        this.dlt645VarList = dlt645VarList;
    }

    @Override
    public IResponseDecoder getResponseDecoder() {
        return new Dlt645ResponseDecoder(protocolEnum);
    }

    @Override
    public String getHashAddr() {
        return addr+"";
    }

}
