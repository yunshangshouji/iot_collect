package zhuboss.gateway.controller.browser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.ChartPOMapper;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.po.ChartPO;
import zhuboss.gateway.po.HisViewPO;
import zhuboss.gateway.po.MeterPO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chart")
@Api(description = "图形")
@Slf4j
public class BrowserChartController {
    @Autowired
    ChartPOMapper chartPOMapper;

    @GetMapping(value="chart")
    @ApiOperation("查看图")
    public void viewSvg(Integer chartId, HttpServletResponse response) throws IOException {
        ChartPO stationChartPO = chartPOMapper.selectByPK(chartId);
        response.getOutputStream().write(stationChartPO.getSvg().getBytes());
    }

}
