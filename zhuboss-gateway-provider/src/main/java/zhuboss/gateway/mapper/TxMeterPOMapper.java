package zhuboss.gateway.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.gateway.po.TxMeterPO;

import java.util.Map;

public interface TxMeterPOMapper extends BaseMapper<TxMeterPO,Integer> {

    @Select("select online_flag as onlineFlag from tx_meter where meter_id = #{meterId}")
    Integer getOnlineFlag(@Param("meterId") Integer meterId);

    void insertOrUpdate(Map<String,Object> map);

}
