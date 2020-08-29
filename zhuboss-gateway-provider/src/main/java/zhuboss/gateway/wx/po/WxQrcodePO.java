package zhuboss.gateway.wx.po;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import zhuboss.framework.mybatis.mapper.AbstractPO;
import zhuboss.framework.mybatis.mapper.Table;

import java.util.Date;

@Data
@Table("wx_qrcode")
@FieldNameConstants(asEnum = true)
public class WxQrcodePO extends AbstractPO {
    private String sceneStr;
    private String actionName;
    private String ticket;
    private String pic_buffer64;
    private String url;
    private Date createDate;
}
