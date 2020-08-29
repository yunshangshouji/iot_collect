package zhuboss.gateway.wx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.utils.JAXBParserUtil;
import zhuboss.framework.utils.JavaUtil;
import zhuboss.gateway.mapper.AttentionPOMapper;
import zhuboss.gateway.wx.po.AttentionPO;
import zhuboss.gateway.wx.service.IAttentionService;
import zhuboss.gateway.wx.service.MenuService;
import zhuboss.gateway.wx.vo.AutoReplyMessage;
import zhuboss.gateway.wx.wx.ReceiveMessage;
import zhuboss.gateway.wx.wx.WeixinAdpater;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("weixin")
@ApiIgnore
@Slf4j
public class WeixinController {

    @Autowired
    MenuService menuService;
    @Autowired
    WeixinAdpater weixinAdpater;
    @Autowired
    AttentionPOMapper attentionPOMapper;
    @Autowired
    IAttentionService attentionService;

    @RequestMapping(value = "receive",method = RequestMethod.GET)
    public void validate(HttpServletRequest request, HttpServletResponse response,
                         String echostr,
                         String signature,
                         String timestamp,
                         String nonce) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.println(echostr);
        response.flushBuffer();
    }

    @PostMapping("receive")
    public ResponseEntity<String> receiveMessage(HttpServletRequest request,
                                                String echostr,
                                                String signature,
                                                String timestamp,
                                                String nonce)
            throws Exception {
        log.info("parameters:"+request.getParameterMap());
        String receiveContent = JavaUtil.InputStreamTOString(request.getInputStream(), "UTF-8");
        log.info("获取原始值 ： "+receiveContent);

        ReceiveMessage receiveMessage = (ReceiveMessage) JAXBParserUtil
                .unserializeFromXml(receiveContent,
                        ReceiveMessage.class);
        String msgType = receiveMessage.getMsgType();
        Integer replyId = null;
        if(msgType.equals("event")){
            String event = receiveMessage.getEvent();
            if(event.equals("subscribe")){
                //同步关注人员名单
                try{
                    this.synchronizedFromWeixin(receiveMessage.getFromUserName()); //未申请公众号认证将审核失败
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
                //发送欢迎消息
                AutoReplyMessage sendMessage = new AutoReplyMessage();
                sendMessage.setToUserName(receiveMessage.getFromUserName());//开发者微信号
                sendMessage.setFromUserName(receiveMessage.getToUserName());
                sendMessage.setCreateTime(new Date().getTime());
                sendMessage.setMsgType("text");
                sendMessage.setContent("领先的工业互联网解决方案提供商已具有电力监控、能耗管理、消防火灾、公寓预付费管理、追溯标刻、自动化成套设备、定制软件开发等成熟解决方案。秉承安全、专业、量身定制的服务理念，助力客户成长。");
                String responseText = JAXBParserUtil.serializeToXml(sendMessage);
                return new ResponseEntity<>(responseText, HttpStatus.OK);

            }else if(event.equals("unsubscribe")){
                //TODO nothing
            }else if(event.equals("CLICK")){
                //TODO nothing
            }
        }else if(msgType.equals("text")){
            //TODO nothing
        }

        return null;
    }


    @RequestMapping("genMenu")
    public @ResponseBody
    JsonResponse genMenu(){
        menuService.genWeixinMenu();
        return new JsonResponse();
    }

    @GetMapping("syncrhonize")
    public @ResponseBody
    JsonResponse synchronizedFromWeixin(String openid){
        List<String> openIdList = StringUtils.hasText(openid)? Arrays.asList(new String[]{openid}):  weixinAdpater.getAllOpenId();
        for(String openId : openIdList){
            AttentionPO attentionPO = attentionPOMapper.selectByPK(openId);
            boolean exists = false;
            if(attentionPO != null){
                exists = true;
            }else{
                attentionPO = new AttentionPO();
            }
            Map<String,Object> userInfoMap = weixinAdpater.getUserInfo(openId);
            attentionPO.setNickname((String)userInfoMap.get("nickname"));
            attentionPO.setCity((String)userInfoMap.get("city"));
            attentionPO.setCountry((String)userInfoMap.get("country"));
            attentionPO.setHeadimgurl((String)userInfoMap.get("headimgurl"));
            attentionPO.setSex(String.valueOf(userInfoMap.get("sex")));
            attentionPO.setProvince((String)userInfoMap.get("province"));
            Integer st = (Integer)userInfoMap.get("subscribe_time");
            attentionPO.setSubscribeTime(new Date(new Long(st)*(long)1000));
            if(exists == true){
                attentionService.update(attentionPO);
            }else{
                attentionPO.setOpenid((String)userInfoMap.get("openid"));
                attentionService.insert(attentionPO);
            }
        }
        return new JsonResponse();
    }
}
