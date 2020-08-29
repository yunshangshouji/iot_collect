package zhuboss.gateway.wx.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.ObjectId;
import zhuboss.gateway.mapper.WxQrcodePOMapper;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.wx.po.WxQrcodePO;
import zhuboss.gateway.wx.vo.CreateQrCodeParam;
import zhuboss.gateway.wx.vo.WechatRemoteException;
import zhuboss.gateway.wx.wx.WeixinAdpater;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/qrcode")
@Api(description = "用户")
public class QrcodeController {
    @Autowired
    WxQrcodePOMapper wxQrcodePOMapper;
    @Autowired
    WeixinAdpater weixinAdpater;

    @GetMapping(value="/query")
    @ApiOperation("用户列表")
    public GridTable<WxQrcodePO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            String type,
            String code
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page,rows,null,null);
        if(StringUtils.hasText(type)) {
            qcb.andEqual("type", type);
        }
        if(StringUtils.hasText(code)) {
            qcb.andEqual("code", code);
        }
        List<WxQrcodePO> list = wxQrcodePOMapper.selectByClause(qcb);
        Integer cnt = wxQrcodePOMapper.selectCountByClause(qcb);
        return new GridTable<WxQrcodePO>(list,cnt);
    }

    @PostMapping(value="/create")
    @ApiOperation("创建二维码")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid CreateQrCodeParam createQrCodeParam) throws WechatRemoteException {
        JSONObject result = weixinAdpater.genQrcode(createQrCodeParam.getSceneStr());
        String ticket = result.getString("ticket");
        String url = result.getString("url");
        WxQrcodePO insert = new WxQrcodePO();
        insert.setSceneStr(createQrCodeParam.getSceneStr());
        insert.setActionName("QR_LIMIT_STR_SCENE");
        insert.setTicket(ticket);
        insert.setUrl(url);
        wxQrcodePOMapper.insert(insert);
        return new JsonResponse();
    }

}
