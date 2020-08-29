package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.controller.console.param.ChangePidParam;
import zhuboss.gateway.dict.MeterTreeTypeEnum;
import zhuboss.gateway.mapper.MeterTreePOMapper;
import zhuboss.gateway.po.MeterTreePO;
import zhuboss.gateway.service.MeterTreeService;
import zhuboss.gateway.service.param.AddMeterTreeParam;
import zhuboss.gateway.service.param.UpdateMeterTreeParam;
import zhuboss.gateway.util.TreeUtil;

import java.util.Date;

@Service
public class MeterTreeServiceImpl implements MeterTreeService {
    @Autowired
    MeterTreePOMapper meterTreePOMapper;

    @Override
    public void add(Integer appId, AddMeterTreeParam addMeterTreeParam) {
        Integer stationId;
        if(addMeterTreeParam.getPid() !=0){
            MeterTreePO parent = meterTreePOMapper.selectByPK(addMeterTreeParam.getPid());
            Assert.isTrue(parent!=null && parent.getAppId().equals(appId),"pid错误");
            stationId = parent.getStationId();
            Assert.isTrue(stationId.equals(addMeterTreeParam.getStationId()));
        }else{
            stationId = addMeterTreeParam.getStationId();
        }
        MeterTreePO insert = new MeterTreePO();
        insert.setAppId(appId);
        insert.setType(MeterTreeTypeEnum.DIR.name());
        insert.setStationId(stationId);
        insert.setSeq(0);
        BeanMapper.copy(addMeterTreeParam,insert);
        insert.setCreateTime(new Date());
        meterTreePOMapper.insert(insert);

    }

    @Override
    public void update(Integer appId,UpdateMeterTreeParam updateMeterTreeParam) {
        MeterTreePO update = meterTreePOMapper.selectByPK(updateMeterTreeParam.getId());
        Assert.isTrue(update.getAppId().equals(appId));
        BeanMapper.copy(updateMeterTreeParam,update);
        update.setModifyTime(new Date());
        meterTreePOMapper.updateByPK(update);
    }

    @Override
    @Transactional
    public void deleteById(Integer appId,Integer id) {
        if(id == 0){
            throw new BussinessException("根节点不能删除");
        }
        MeterTreePO delete = meterTreePOMapper.selectByPK(id);
        if(!delete.getType().equals(MeterTreeTypeEnum.DIR.name())){
            throw new BussinessException("只有目录节点才可以删除");
        }
        Assert.isTrue(delete.getAppId().equals(appId));
        meterTreePOMapper.deleteByPK(id);
        //纠正无父节点
        checkStationPid(delete.getStationId());
    }

    @Override
    public void checkStationPid(Integer stationId) {
        //递归删除所有的子节点，都挂到根节点上
        int count;
        while((count = meterTreePOMapper.resetStationNoParentNode(stationId))>0){
            System.out.println();
        }
    }


    @Override
    public void changePid(Integer appId, ChangePidParam changePidParam) {
        Integer sourceId = changePidParam.getSourceId();
        Integer targetId = changePidParam.getTargetId();
        if(sourceId == 0){
            throw new BussinessException("根节点不能移动");
        }
        Assert.isTrue(appId.equals(meterTreePOMapper.selectByPK(sourceId).getAppId()));

        MeterTreePO target = null;
        if(targetId != 0 ){
            target = meterTreePOMapper.selectByPK(targetId);
            Assert.isTrue(appId.equals(target.getAppId()));
        }
        String point =changePidParam.getPoint();

        TreeUtil.move(sourceId,targetId,point,meterTreePOMapper);

    }
}
