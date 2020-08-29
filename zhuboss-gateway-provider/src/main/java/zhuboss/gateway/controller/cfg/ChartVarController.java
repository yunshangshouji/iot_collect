package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.controller.console.vo.StationChartVar;
import zhuboss.gateway.facade.vo.DataId;
import zhuboss.gateway.mapper.ChartPOMapper;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.po.HisViewPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/cfg/chart/var")
@Api(description = "图形-绑定变量")
@Slf4j
public class ChartVarController {
    @Autowired
    ChartPOMapper stationChartPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    MeterTypeService meterTypeService;


    @RequestMapping(value="/query",method = RequestMethod.GET)
    @ApiOperation("列表")
    public GridTable<StationChartVar> query(
            @RequestParam(value="page",required = false,defaultValue="1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            @RequestParam(required = true) Integer meterKindId,
            Integer meterId,
            String targetCode

    ) {
        List<StationChartVar> stationChartVarList = new ArrayList<>();
        List<DataId> dataIdList = meterTypeService.queryMeterKindVar(meterKindId,null);
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual(MeterPO.Fields.APP_ID, UserSession.getAppId());
        if(meterId != null){
            qcb.andEqual(MeterPO.Fields.ID,meterId);
        }
        List<MeterPO> meterPOList = meterPOMapper.selectByClause(qcb);
        //
        //
        for(DataId dataId : dataIdList){
            if(StringUtils.hasText(targetCode) && !dataId.getValue().equals(targetCode)){
                continue;
            }
            for(MeterPO meterPO : meterPOList){
                stationChartVarList.add(new StationChartVar(dataId.getId()+"-"+meterPO.getId(),dataId.getText(),meterPO.getDevNullName()));
            }
        }

        return new GridTable<StationChartVar>(stationChartVarList,stationChartVarList.size());
    }

    @RequestMapping(value="/dict/vars",method = RequestMethod.GET)
    @ApiOperation("变量字典")
    public List<DataId> vars(@RequestParam(required = true) Integer meterKindId){
        List<DataId> itemList = meterTypeService.queryMeterKindVar(meterKindId,null);
        for(DataId dataId : itemList){
            dataId.setText(dataId.getValue()+"【"+dataId.getText()+"】");
        }
        return itemList;
    }

}
