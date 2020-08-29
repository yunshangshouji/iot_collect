package zhuboss.gateway.service.vo;

import lombok.Data;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.CollectorPO;

import java.io.Serializable;
import java.util.List;

@Data
public class CachedCollector implements Serializable {
    private CollectorPO collector;
    private List<MeterPO> meterPOList;

    public CachedCollector(CollectorPO collector, List<MeterPO> meterPOList) {
        this.collector = collector;
        this.meterPOList = meterPOList;
    }
}
