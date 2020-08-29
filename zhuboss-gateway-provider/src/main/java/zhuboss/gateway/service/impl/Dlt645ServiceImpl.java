package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.mapper.Dlt645POMapper;
import zhuboss.gateway.po.Dlt645PO;
import zhuboss.gateway.service.Dlt645Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Dlt645ServiceImpl implements Dlt645Service {
    @Autowired
    Dlt645POMapper dlt645POMapper;

    @Override
    public Map<Integer, Integer> getScaleMap(ProtocolEnum protocolEnum) {
        List<Dlt645PO> dlt645POList = dlt645POMapper.selectByClause(new QueryClauseBuilder());
        Map<Integer, Integer> result = new HashMap<>();
        for(Dlt645PO dlt645PO : dlt645POList){
            result.put(Integer.parseInt((protocolEnum.equals(ProtocolEnum.DLT2007)? dlt645PO.getItem2007():dlt645PO.getItem1997()),16),
                    protocolEnum.equals(ProtocolEnum.DLT2007)? dlt645PO.getScale2007():dlt645PO.getScale1997());
        }
        /*
        //电压
        scale.put(0x02010100,1);
        scale.put(0x02010200,1);
        scale.put(0x02010300,1);
        scale.put(0x02010f00,1);
        //电能
        scale.put(0x00010000,2);
        */
        return result;
    }
}
