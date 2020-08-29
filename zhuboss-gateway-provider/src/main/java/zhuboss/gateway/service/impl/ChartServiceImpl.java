package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.ChartPOMapper;
import zhuboss.gateway.po.ChartPO;
import zhuboss.gateway.service.ChartService;
import zhuboss.gateway.service.param.AddChartParam;
import zhuboss.gateway.service.param.UpdateChartParam;

import java.util.Date;

@Service
public class ChartServiceImpl implements ChartService {
    @Autowired
    ChartPOMapper stationChartPOMapper;

    @Override
    public void addStationChart(Integer appId, AddChartParam addDevCapParam) {
        ChartPO insert = new ChartPO();
        BeanMapper.copy(addDevCapParam,insert);
        insert.setAppId(appId);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        stationChartPOMapper.insert(insert);
    }

    @Override
    public void updateStationChart(UpdateChartParam updateStationChartParam,Integer modifier) {
        ChartPO update = stationChartPOMapper.selectByPK(updateStationChartParam.getId());
        BeanMapper.copy(updateStationChartParam,update);
        update.setModifyTime(new Date());
        stationChartPOMapper.updateByPK(update);
    }

    @Override
    @Transactional
    public void deleteStationChart(Integer id) {
        stationChartPOMapper.deleteByPK(id);
    }
}
