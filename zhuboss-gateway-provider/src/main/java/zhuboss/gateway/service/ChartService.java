package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddChartParam;
import zhuboss.gateway.service.param.UpdateChartParam;

public interface ChartService {

    void addStationChart(Integer appId, AddChartParam addDevCapParam);

    void updateStationChart(UpdateChartParam updateStationChartParam,Integer modifier);

    void deleteStationChart(Integer id);

}
