package zhuboss.gateway.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.MeterValues;

import java.util.List;

public interface MeterPOMapper extends BaseMapper<MeterPO,Integer> {

    @Select("SELECT DISTINCT dev_no AS gwNo FROM `meter`,`collector` WHERE meter.`collector_id` = collector.`id` AND  meter_type_id = #{meterTypeId}")
    List<String> queryCollectorsByMeterType(@Param("meterTypeId") Integer meterTypeId);

    @Select("SELECT DISTINCT dev_no AS gwNo FROM `meter`,`collector`,meter_type WHERE meter.`collector_id` = collector.`id` AND meter.`meter_type_id` = meter_type.`id` AND  meter_type.`meter_kind_id` = #{meterKindId}")
    List<String> queryCollectorsByMeterKind(@Param("meterKindId") Integer meterKindId);

    @Select("SELECT  id as meterId,last_values AS lastValues FROM meter LEFT JOIN tx_meter ON meter.`id` = tx_meter.`meter_id` \n" +
            "WHERE meter.id IN (SELECT meter_id FROM `summary_panel_item` WHERE summary_id = #{summaryId})  AND offline_flag = 0")
    List<MeterValues> getMeterValuesBySummaryId(@Param("summaryId") Integer summaryId);
}
