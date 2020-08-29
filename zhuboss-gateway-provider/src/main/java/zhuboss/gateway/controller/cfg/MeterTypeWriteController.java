package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.utils.JavaUtil;
import zhuboss.gateway.adapter.ModbusUtil;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.MeterKindWritePOMapper;
import zhuboss.gateway.mapper.MeterTypeWritePOMapper;
import zhuboss.gateway.mapper.LogWritePOMapper;
import zhuboss.gateway.service.vo.SaveMeterTypeWriteParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.tx.meter.modbus.ModbusRequestMessage;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.po.MeterTypeWritePO;
import zhuboss.gateway.service.MeterTypeWriteService;
import zhuboss.gateway.service.MeterWriteService;
import zhuboss.gateway.service.vo.AddMeterTypeWriteParam;
import zhuboss.gateway.service.vo.UpdateMeterTypeWriteParam;
import org.springframework.beans.factory.annotation.Autowired;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_type/write")
@Slf4j
public class MeterTypeWriteController {
    @Autowired
    MeterTypeWriteService meterTypeWriteService;
    @Autowired
    MeterTypeWritePOMapper meterTypeWritePOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    MeterKindWritePOMapper meterKindWritePOMapper;
    @Autowired
    LogWritePOMapper logWritePOMapper;
    @Autowired
    MeterWriteService meterWriteService;

    @RequestMapping("query")
    public GridTable<MeterTypeWritePO> query(
            @RequestParam(value="start",defaultValue="0")  Integer start,
            @RequestParam(value="limit",defaultValue="100") Integer limit,
            @RequestParam(value = "meterTypeId",required = true) Integer meterTypeId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.andEqual(MeterTypeWritePO.Fields.APP_ID, UserSession.getAppId());
        qcb.page(start, limit);
        if(meterTypeId !=null){
            qcb.andEqual(MeterTypeWritePO.Fields.METER_TYPE_ID,meterTypeId);
        }
        List<MeterTypeWritePO> list = meterTypeWritePOMapper.selectByClause(qcb);
        Collections.sort(list, new Comparator<MeterTypeWritePO>() {
            @Override
            public int compare(MeterTypeWritePO o1, MeterTypeWritePO o2) {
                return o1.getTargetCode().compareTo(o2.getTargetCode());
            }
        });
        Integer cnt = meterTypeWritePOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody AddMeterTypeWriteParam param) {
        autoFill(param);
        MeterKindWritePO meterKindWritePO = meterKindWritePOMapper.selectByPK(param.getMeterKindWriteId());
        Assert.isTrue(meterKindWritePO.getAppId().equals(UserSession.getAppId()));
        meterTypeWriteService.add(param);
        return new JsonResponse();
    }

    private void autoFill(SaveMeterTypeWriteParam saveMeterTypeWriteParam){
        if(saveMeterTypeWriteParam.getCmd() == 0x10){
            //写多线圈，字节数是寄存器数的2倍
            String writeByteSize =  zhuboss.gateway.util.JavaUtil.int2hexString(zhuboss.gateway.util.JavaUtil.hexString2Int(saveMeterTypeWriteParam.getWriteUnits()) * 2,2) ;
            saveMeterTypeWriteParam.setWriteByteSize(writeByteSize);
        }
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody UpdateMeterTypeWriteParam param) {
        autoFill(param);
        meterTypeWriteService.update(param);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterTypeWritePO meterTypeWritePO = meterTypeWritePOMapper.selectByPK(id);
            Assert.isTrue(meterTypeWritePO.getAppId().equals(UserSession.getAppId()));
            meterTypeWriteService.delete(id);
        }
        return new JsonResponse();
    }

    @RequestMapping("varDict")
    public List<Item> loadVarDict(@RequestParam(required = true) Integer meterKindId){
        List<MeterKindWritePO> poList = meterKindWritePOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterKindWritePO.Fields.METER_KIND_ID,meterKindId)
                .sort("target_code"));
        List<Item> list = new ArrayList<>();
        for(MeterKindWritePO dictMeterTargetPO : poList){
            list.add(new Item(dictMeterTargetPO.getId()+"",dictMeterTargetPO.getTargetCode()+"-"+dictMeterTargetPO.getTargetName()));
        }
        return list;
    }

    @RequestMapping(value = "list_meters",method = RequestMethod.GET)
    public List<Item> listCollectorMeterId(@RequestParam(required = true) Integer meterTypeId){
        List<MeterPO> meterPOList = meterPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.METER_TYPE_ID,meterTypeId));
        List<Item> list = new ArrayList<>();
        for(MeterPO meterPO : meterPOList){
            list.add(new Item(meterPO.getId()+"", meterPO.getDevNo()+"-"+ meterPO.getComPort()+"-"+ meterPO.getAddr()));
        }
        return list;
    }

    @RequestMapping(value = "test_view_addr_cmd",method = RequestMethod.GET)
    public JsonResponse testViewAddrCmd( @RequestParam(required = true) @NotNull Integer meterTypeWriteId,
                                         @RequestParam(required = true) @NotNull Integer addr,
                                         String writeData){
        MeterTypeWritePO meterTypeWritePO = meterTypeWritePOMapper.selectByPK(meterTypeWriteId);
        String dataHex = StringUtils.hasText(writeData) ? writeData : meterTypeWritePO.getDataHex();
        ModbusUtil.checkWrite(meterTypeWritePO.getCmd(),meterTypeWritePO.getAddr(),meterTypeWritePO.getWriteUnits(),meterTypeWritePO.getWriteByteSize(),dataHex);
        byte[] bytes = ModbusUtil.buildData(meterTypeWritePO.getCmd(),meterTypeWritePO.getAddr(),meterTypeWritePO.getWriteUnits(),meterTypeWritePO.getWriteByteSize(),dataHex);
        ModbusRequestMessage modbusRequestMessage = new ModbusRequestMessage((byte)addr.intValue(),(byte)meterTypeWritePO.getCmd().intValue(),bytes);
        return new JsonResponse(true,JavaUtil.bytesToHexString(modbusRequestMessage.getEncodeBytes()));
    }

    @RequestMapping(value = "test_view_device_cmd",method = RequestMethod.GET)
    public JsonResponse testViewDeviceCmd(
                                  @RequestParam(required = true) @NotNull  Integer meterTypeWriteId,
                                  @RequestParam(required = true) @NotNull Integer collectorMeterId,
                                  String writeData) throws InterruptedException {

        MeterPO meterPO = meterPOMapper.selectByPK(collectorMeterId);
        MeterTypeWritePO meterTypeWritePO = meterTypeWritePOMapper.selectByPK(meterTypeWriteId);
        String dataHex = StringUtils.hasText(writeData) ? writeData : meterTypeWritePO.getDataHex();
        ModbusUtil.checkWrite(meterTypeWritePO.getCmd(),meterTypeWritePO.getAddr(),meterTypeWritePO.getWriteUnits(),meterTypeWritePO.getWriteByteSize(),dataHex);
        byte[] bytes = ModbusUtil.buildData(meterTypeWritePO.getCmd(),meterTypeWritePO.getAddr(),meterTypeWritePO.getWriteUnits(),meterTypeWritePO.getWriteByteSize(),dataHex);
        ModbusRequestMessage modbusRequestMessage = new ModbusRequestMessage((byte) meterPO.getAddr().intValue(),(byte)meterTypeWritePO.getCmd().intValue(),bytes);
        return new JsonResponse(true,JavaUtil.bytesToHexString(modbusRequestMessage.getEncodeBytes()));

    }

    @RequestMapping(value = "test_write",method = RequestMethod.GET)
    public JsonResponse testWrite(@RequestParam(required = true) String taskUUID,
                                  @RequestParam(required = true) Integer meterTypeWriteId,
                                  @RequestParam(required = true) Integer collectorMeterId,
                                  @ApiParam("空则使用默认配置") String writeData) throws InterruptedException {
        return meterWriteService.write(taskUUID,meterTypeWriteId,collectorMeterId,writeData,true);

    }

}
