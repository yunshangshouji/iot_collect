package zhuboss.gateway.mapper;

import org.apache.ibatis.annotations.Param;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.gateway.po.MeterAlarmPO;
import zhuboss.gateway.po.MeterOverLimitVO;

import java.util.List;

public interface MeterAlarmPOMapper extends BaseMapper<MeterAlarmPO,Long> {

    /**
     * 某个越限条件影响的采集器
     * @param meterAalrmId
     * @return
     */
    List<Integer> getCollectorId(@Param("meterAalrmId") Long meterAalrmId);

    /**
     * 某个采集器下的越限条件
     * @param collectorId
     * @return
     */
    List<MeterOverLimitVO> queryCollectorOverLimits(@Param("collectorId") Integer collectorId);
}
