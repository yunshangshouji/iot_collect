package zhuboss.gateway.mapper;

import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.gateway.po.TxCollectorPO;

import java.util.Map;

public interface TxCollectorPOMapper extends BaseMapper<TxCollectorPO,Integer> {

    void insertOrUpdate(Map<String,Object> map);

}
