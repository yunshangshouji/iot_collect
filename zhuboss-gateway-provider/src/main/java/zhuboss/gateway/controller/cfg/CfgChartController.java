package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.ChartPOMapper;
import zhuboss.gateway.po.ChartPO;
import zhuboss.gateway.service.ChartService;
import zhuboss.gateway.service.param.AddChartParam;
import zhuboss.gateway.service.param.UpdateChartParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(value="/cfg/chart")
@Api(description = "图")
@Slf4j
public class CfgChartController {
    @Autowired
    ChartPOMapper chartPOMapper;
    @Autowired
    ChartService chartService;

    @RequestMapping(value="/query",method = RequestMethod.GET)
    @ApiOperation("列表")
    public GridTable<ChartPO> query(
            @RequestParam(value="page",required = false,defaultValue="1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page,rows,null, null);
        qcb.andEqual(ChartPO.Fields.APP_ID, UserSession.getAppId());
        qcb.sort("id", ESortOrder.DESC);

        List<ChartPO> list = chartPOMapper.selectByClause(qcb);
        for(ChartPO chartPO : list){
            chartPO.setSvg(null); //列表不需要svg，提高响应速度
        }
        Integer cnt = chartPOMapper.selectCountByClause(qcb);
        return new GridTable<ChartPO>(list,cnt);
    }

    @RequestMapping(value="/add",method = RequestMethod.POST)
    @ApiOperation("新增图形")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddChartParam addStationChartParam) {
        chartService.addStationChart(UserSession.getAppId(),addStationChartParam);
        return new JsonResponse();
    }

    @RequestMapping(value="/update",method = RequestMethod.POST)
    @ApiOperation("修改图形")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateChartParam updateStationChartParam) {
        chartService.updateStationChart(updateStationChartParam,UserSession.getUserId());
        return new JsonResponse();
    }

    @RequestMapping(value="/delete",method = RequestMethod.GET)
    @ApiOperation("删除图形")
    @WriteAction
    public JsonResponse delete(Integer id) {
        chartService.deleteStationChart(id);
        return new JsonResponse();
    }

    @PostMapping(value="/uploadSVG")
    @ApiOperation("上传SVG图")
    @WriteAction
    public ModelAndView uploadSVG(@ApiParam(value = "图",required = true) @RequestParam(required = true)
                                              Integer chartId,
                                  HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest;
        if(request instanceof ContentCachingRequestWrapper){
            request = (HttpServletRequest)((ContentCachingRequestWrapper)request).getRequest();
        }

        if(request instanceof ShiroHttpServletRequest){
            CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
            multipartRequest = commonsMultipartResolver.resolveMultipart((HttpServletRequest) ((ShiroHttpServletRequest) request).getRequest());
        }else{
            multipartRequest = (MultipartHttpServletRequest)request;
        }

        /**
         * 读取流数据
         */
        byte[] data = null;
        long  startTime=System.currentTimeMillis();
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
        //检查form中是否有enctype="multipart/form-data"
        if(multipartResolver.isMultipart(request))
        {
            //将request变成多部分request
            //获取multiRequest 中所有的文件名
            Iterator iter=multipartRequest.getFileNames();
            while(iter.hasNext())
            {
                //一次遍历所有文件
                MultipartFile file=multipartRequest.getFile(iter.next().toString());
                if(file!=null)
                {
                    data = new byte[(int)file.getSize()];
                    IOUtils.read(file.getInputStream(),data);
                    break;
                }
            }
        }
        long  endTime=System.currentTimeMillis();
        log.info("上传占用时间："+String.valueOf(endTime-startTime)+"ms");
        if(data == null){
            throw new BussinessException("未读到数据");
        }

        /**
         * 保存数据
         */
        ChartPO stationChartPO = chartPOMapper.selectByPK(chartId);
        stationChartPO.setSvg(new String(data,"UTF-8"));
        stationChartPO.setModifyTime(new Date());
        chartPOMapper.updateByPK(stationChartPO);
        return   new ModelAndView("redirect:/static/cfg/chart.html");
    }

    @GetMapping(value="/downloadSVG")
    @ApiOperation("下载SVG图")
    @WriteAction
    public Object downloadSVG(@ApiParam(value = "图",required = true) @RequestParam(required = true)
                                          Integer chartId,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {

        ChartPO stationChartPO = chartPOMapper.selectByPK(chartId);

        //设置文件输出类型
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename="
                + new String((stationChartPO.getChartName()+".svg").getBytes("utf-8"), "ISO8859-1"));
        //设置输出长度
        byte[] data = stationChartPO.getSvg().getBytes();
        response.setHeader("Content-Length", String.valueOf(data.length));
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping(value="/preview")
    public Object preview(@ApiParam(value = "图",required = true) @RequestParam(required = true)
                                      Integer chartId){
        return new RedirectView("/static/browser/distribute.html?full=true&chartId=" + chartId);
    }
}
