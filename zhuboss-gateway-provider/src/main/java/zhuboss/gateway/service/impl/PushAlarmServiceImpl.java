package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.gateway.service.PushAlarmService;
import zhuboss.gateway.wx.vo.TemplateMessageDataItem;
import zhuboss.gateway.wx.wx.WeixinAdpater;

import java.util.HashMap;
import java.util.Map;

/**
 * 报警频率，每台设备，在不做报警归位的情况下，每天只推送一次？
 */
@Service
public class PushAlarmServiceImpl implements PushAlarmService {
    final String MESSAGE_ID = "rwS1Z_X9sO46Q2lPke2EO8gN1BNIJ_lwdjhNrvU2x4w";

    @Autowired
    WeixinAdpater weixinAdpater;
    @Override
    public void pushWx() {
        Map<String, TemplateMessageDataItem> data = new HashMap<>();
        data.put("first",new TemplateMessageDataItem("详细内容","#173177"));
        data.put("system",new TemplateMessageDataItem("系统名称","#173177"));
        data.put("time",new TemplateMessageDataItem("2019-1-1","#173177"));
        data.put("account",new TemplateMessageDataItem("1","#173177"));
        data.put("remark",new TemplateMessageDataItem("备注说明","#173177"));
        weixinAdpater.sendTemplateMessage("oWl8Mj15lPDRCRYARmTwHc8O0i5c",MESSAGE_ID,"https://zutai.vip",data);
    }

}
