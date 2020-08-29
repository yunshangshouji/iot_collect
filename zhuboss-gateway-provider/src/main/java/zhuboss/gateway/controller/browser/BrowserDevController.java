package zhuboss.gateway.controller.browser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.po.HisViewPO;
import zhuboss.gateway.po.MeterPO;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/browser/dev")
@Api(description = "图形")
@Slf4j
public class BrowserDevController {
    @Autowired
    HisViewPOMapper hisViewPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;

    @RequestMapping(value="/devs",method = RequestMethod.GET)
    @ApiOperation("设备列表")
    public List<Item> queryDevs(
            @RequestParam(required = false) Integer meterKindId,
            @RequestParam(required = false) Integer hisViewId
    ) {
        if(hisViewId != null){
            HisViewPO hisViewPO = hisViewPOMapper.selectByPK(hisViewId);
            meterKindId = hisViewPO.getMeterKindId();
        }
        List<MeterPO> meterPOList = meterPOMapper.selectByClause(new QueryClauseBuilder()
                .andSQL("EXISTS(SELECT 1 FROM meter_type WHERE id= meter.`meter_type_id` AND  meter_kind_id = "+meterKindId+" )")
                .sort("collector.dev_no").sort(MeterPO.Fields.COM_PORT).sort(MeterPO.Fields.ADDR)
        );
        List<Item> itemList = new ArrayList<>();
        for(MeterPO meterPO : meterPOList){
            Item item = new Item(meterPO.getId(),meterPO.getDevNullName());
            itemList.add(item);
        }
        return itemList;
    }

}
