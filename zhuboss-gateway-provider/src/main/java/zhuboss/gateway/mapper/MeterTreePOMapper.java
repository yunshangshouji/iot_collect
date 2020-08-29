package zhuboss.gateway.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.gateway.po.MeterTreePO;

public interface MeterTreePOMapper extends BaseMapper<MeterTreePO,Integer> {

    @Update("UPDATE meter_tree SET pid = 0 WHERE station_id = #{stationId} AND pid<>0 AND pid NOT IN (SELECT id FROM (SELECT id FROM meter_tree WHERE station_id = #{stationId}) AS X)")
    Integer resetStationNoParentNode(@Param("stationId") Integer stationId);

    /**
     * 站点删除，移到根站点
     * @param stationId
     * @param newStationId
     * @return
     */
    @Update("UPDATE meter_tree SET station_id = #{newStationId} WHERE station_id = #{stationId}")
    Integer moveStationToStation(@Param("stationId") Integer stationId, @Param("newStationId") Integer newStationId);

    @Update(" UPDATE meter_tree " +
            " SET station_id = #{newStationId}  , pid='0',seq = NULL " +
            " WHERE EXISTS(SELECT 1 FROM meter WHERE meter.`id` = meter_tree.`meter_id` AND meter.`collector_id` = #{collectorId})")
    Integer onCollectorStationChanged(@Param("collectorId") Integer collectorId, @Param("newStationId") Integer newStationId);

}
