package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.controller.console.param.ChangePidParam;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.MeterTreePOMapper;
import zhuboss.gateway.mapper.StationPOMapper;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.StationService;
import zhuboss.gateway.service.param.AddStationParam;
import zhuboss.gateway.service.param.SaveStationParam;
import zhuboss.gateway.service.param.UpdateStationParam;
import zhuboss.gateway.util.TreeUtil;

import java.util.Date;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {
    @Autowired
    StationPOMapper stationPOMapper;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterTreePOMapper meterTreePOMapper;
    @Autowired
    CollectorService collectorService;

    @Override
    public StationPO getRootStation(Integer appId) {
        return stationPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(StationPO.Fields.APP_ID,appId).andEqual("pid",0));
    }

    @Override
    public void add(Integer appId,AddStationParam addStationParam) {
        StationPO insert = new StationPO();
        BeanMapper.copy(addStationParam,insert);
        insert.setCreateTime(new Date());
        insert.setAppId(appId);

        fillParentInfo(appId, addStationParam, insert);

        //保存
        stationPOMapper.insert(insert);
    }

    private void fillParentInfo(Integer appId, SaveStationParam saveStationParam, StationPO stationPO) {
        StationPO rootStation = this.getRootStation(appId);
        StationPO parent;
        if(saveStationParam.getPid()==null /*API接口时*/ || rootStation.getId().equals(saveStationParam.getPid())){
            parent = rootStation;
            stationPO.setPid(parent.getId()+"");
        }else {
            parent =  stationPOMapper.selectByPK(saveStationParam.getPid());
        }
        //自动生成full_text
        if(parent.equals(rootStation)) {
            stationPO.setFullText(saveStationParam.getText());
        }else {
            stationPO.setFullText(parent.getFullText()+"/"+saveStationParam.getText());
        }
    }

    @Override
    @Transactional
    public void update(Integer appId,UpdateStationParam updateStationParam) {
        StationPO update = stationPOMapper.selectByPK(updateStationParam.getId());
        boolean textChanged = !updateStationParam.getText().equals(update.getText());
        Assert.isTrue(update.getAppId().equals(appId));
        BeanMapper.copy(updateStationParam,update);
        update.setModifyTime(new Date());
        fillParentInfo(update.getAppId(),updateStationParam,update);
        stationPOMapper.updateByPK(update);
        if(textChanged){
            updateFullText(update);
        }
    }

    @Override
    @Transactional
    public void delete(Integer appId,Integer stationId) {
        StationPO delete = stationPOMapper.selectByPK(stationId);
        Assert.isTrue(delete.getAppId().equals(appId));
        //TODO 删除设备目录
        //删除网关所属站点信息
        List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(new QueryClauseBuilder().andEqual(CollectorPO.Fields.APP_ID,appId).andEqual(CollectorPO.Fields.STATION_ID,stationId));
        Integer rootStationId = this.getRootStation(appId).getId();
        for(CollectorPO collectorPO : collectorPOList){
            collectorService.deleteById(collectorPO.getId());
//            collectorService.changeCollectorStation(collectorPO.getId(),stationId,rootStationId,false);
        }
        //删除站点
        stationPOMapper.deleteByPK(stationId);
    }

    @Override
    public void changePid(Integer appId, ChangePidParam changePidParam) {
        Integer sourceId = changePidParam.getSourceId();
        Integer targetId = changePidParam.getTargetId();
        String point = changePidParam.getPoint();
        if(sourceId == 0){
            throw new BussinessException("根节点不能移动");
        }

        StationPO source = stationPOMapper.selectByPK(sourceId);
        Assert.isTrue(appId.equals(source.getAppId()));

        StationPO target =stationPOMapper.selectByPK(targetId);;
        Assert.isTrue(appId.equals(target.getAppId()));
        if(target.getPid().equals("0") && !point.equals("append")){
            throw new BussinessException("不能将节点移到根节点的同级");
        }

        TreeUtil.move(sourceId,targetId,point,stationPOMapper);
        //跨层级移动，需要刷新文本
        if(!source.getPid().equals(target.getPid())){
            updateFullText(stationPOMapper.selectByPK(sourceId)); //source修改了，重新加载
        }
    }

    @Override
    public StationPO getStationPoByRefId(Integer appId, String refId) {
        return  stationPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(StationPO.Fields.APP_ID,appId).andEqual(StationPO.Fields.REF_ID,refId));
    }

    /**
     * 根节点不允许更新，所以肯定有父节点
     * @param stationPO
     */
    private void updateFullText(StationPO stationPO){
        StationPO rootStation = this.getRootStation(stationPO.getAppId());
        StationPO parent;
        if(stationPO.getPid().equals(rootStation.getId()+"")){
            parent = rootStation;
            stationPO.setFullText(stationPO.getText());
        }else{
            parent = stationPOMapper.selectByPK(Integer.parseInt(stationPO.getPid()));
            stationPO.setFullText(parent.getText()+"/"+stationPO.getText());
        }
        stationPOMapper.updateByPK(stationPO);
        //所有子节点更新full_text
        List<StationPO> childs = stationPOMapper.selectByClause(new QueryClauseBuilder().andEqual("pid", stationPO.getId()));
        for(StationPO child : childs) {
            updateFullText(child);
        }
    }
}
