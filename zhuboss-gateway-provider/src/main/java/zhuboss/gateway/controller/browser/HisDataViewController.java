package zhuboss.gateway.controller.browser;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.controller.vo.DataGridField;
import zhuboss.gateway.facade.vo.DataId;
import zhuboss.gateway.facade.vo.GridTable;
import zhuboss.gateway.mapper.HisDataMapper;
import zhuboss.gateway.mapper.HisViewColumnPOMapper;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.po.HisViewColumnPO;
import zhuboss.gateway.po.HisViewPO;
import zhuboss.gateway.service.DDLService;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.service.vo.HisDataQueryCondition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/browser/his_data/view")
public class HisDataViewController {
    @Autowired
    HisViewColumnPOMapper hisViewColumnPOMapper;
    @Autowired
    HisViewPOMapper hisViewPOMapper;
    @Autowired
    HisDataMapper hisDataMapper;
    @Autowired
    MeterTypeService meterTypeService;
    @Autowired
    DDLService ddlService;

    @GetMapping("data")
    public GridTable<Map<String,Object>> data(Integer hisViewId,
                                              Integer meterId,
                                              String conditions,
                                              String[] var,
                                              String[] compare,
                                               BigDecimal[] value,
                                               @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                              @RequestParam(value="rows",defaultValue="10") Integer rows){
        HisViewPO hisViewPO = hisViewPOMapper.selectByPK(hisViewId);
        List<HisViewColumnPO> hisViewColumnPOList = hisViewColumnPOMapper.selectByClause(new QueryClauseBuilder().andEqual(HisViewColumnPO.Fields.VIEW_ID,hisViewId));
        List<String> cols = new ArrayList<>();
        for(HisViewColumnPO hisViewColumnPO : hisViewColumnPOList){
            cols.add(ddlService.getColName(hisViewColumnPO.getMeterKindReadId()));
        }
        Integer start = (page - 1) * rows;
        String tableName = ddlService.getTableName(hisViewPO.getMeterKindId());
        List<HisDataQueryCondition> filters = new ArrayList<>();
        if(meterId != null){
            filters.add(new HisDataQueryCondition("meter_id","=",meterId));
        }
        if(var!=null){
            for(int i=0;i<var.length;i++){
                if(StringUtils.hasText(var[i]) && StringUtils.hasText(compare[i]) && value[i]!=null){
                    filters.add(new HisDataQueryCondition(ddlService.getColName(Integer.parseInt(var[i])),compare[i],value[i]));
                }
            }
        }
        //TODO 首先是用户定义的排序,再按读取时间排序
        //给用户增加read_time字段。用户未添加字段， 默认安read-time
        //支持用户点击字段头后台排序
        String orderBy = "order by read_time desc";
        List<Map<String,Object>> list = hisDataMapper.query(tableName,cols,filters,orderBy,start,rows);
        Integer count = hisDataMapper.count(tableName,filters);
        GridTable<Map<String,Object>> gridTable = new GridTable<>();
        gridTable.setRows(list);
        gridTable.setTotal(count);
        return gridTable;
    }

    @GetMapping("columns")
    public List<DataGridField> queryColumns(Integer hisViewId){
        List<HisViewColumnPO> meterKindReadPOList = hisViewColumnPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(HisViewColumnPO.Fields.VIEW_ID,hisViewId)
                .sort(HisViewColumnPO.Fields.SEQ)
        );
        List<DataGridField> results = new ArrayList<>();
        results.add(new DataGridField("readTime","读取时间",100));
        results.add(new DataGridField("devName","设备名称",150));
        for(HisViewColumnPO hisViewColumnPO : meterKindReadPOList){
            results.add(new DataGridField(ddlService.getColName(hisViewColumnPO.getMeterKindReadId()),
                    hisViewColumnPO.getTargetName(),
                    100
                    ));
        }
        return results;
    }

    @RequestMapping(value="/fields",method = RequestMethod.GET)
    @ApiOperation("变量字典")
    public List<DataId> vars(@RequestParam(required = true) Integer hisViewId){
        HisViewPO hisViewPO =  hisViewPOMapper.selectByPK(hisViewId);
        List<DataId> itemList = meterTypeService.queryMeterKindVar(hisViewPO.getMeterKindId(),true);
        for(DataId dataId : itemList){
            dataId.setText(dataId.getValue()+"【"+dataId.getText()+"】");
        }
        return itemList;
    }

}
