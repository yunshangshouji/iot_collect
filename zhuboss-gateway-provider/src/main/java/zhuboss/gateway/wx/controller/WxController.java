package zhuboss.gateway.wx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.mapper.AppPOMapper;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.spring.shiro.ShiroConfig;
import zhuboss.gateway.spring.web.filter.SessionKey;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.wx.wx.WxConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("")
@Slf4j
public class WxController {

    HttpClient httpClient;

    public WxController() {
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
        connectionManager.setMaxTotal(2000);
        connectionManager.setDefaultMaxPerRoute(200);
        this.httpClient = new DefaultHttpClient(connectionManager);

    }

    @Autowired
    AppPOMapper appPOMapper;

    @GetMapping("wx_web/app_list")
    public List<Item> appList(){

        QueryClauseBuilder qcb = new QueryClauseBuilder()
                .andEqual(AppPO.Fields.USER_ID, UserSession.getUserId());
        List<AppPO> wxBindPOList = appPOMapper.selectByClause(qcb);
        List<Item> itemList = new ArrayList<>();
        for(AppPO appPO : wxBindPOList){
            itemList.add(new Item(appPO.getAppId(),appPO.getAppName()));
        }
        return itemList;

    }



    @GetMapping("_wx")
    @ApiOperation("因为微信浏览不带头标识，只能根据url识别来自微信浏览器")
    public void wxRedirect(@RequestParam(value = "redirect",required = true) String redirect, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


    }


}
