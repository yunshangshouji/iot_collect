package zhuboss.gateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.gateway.common.SysErrorType;
import zhuboss.gateway.mapper.SysErrorLogPOMapper;
import zhuboss.gateway.po.SysErrorLogPO;
import zhuboss.gateway.service.SysErrorLogService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

@Service
@Slf4j
public class SysErrorLogServiceImpl implements SysErrorLogService {
    @Autowired
    SysErrorLogPOMapper sysErrorLogPOMapper;

    @Override
    public void log(SysErrorType errorType, String message, Exception e) {
        String content = getErrorInfoFromException(e);
        this.log(errorType,message,content);
    }

    @Override
    public void log(SysErrorType errorType, String message, String content) {
        /**
         * 触发短信通知，未清空的情况下，同一类别错误每天只发送一条
         */
        /*Integer existsCount = sysErrorLogPOMapper.selectCountByClause(new QueryClauseBuilder()
                .andEqual("type",errorType.name())
                .andGreat("create_time",DateUtils.truncate(new Date(), Calendar.DATE)) //
        );
        if(existsCount == 0){
            SysParamPO mobileParam = sysParamService.findByCode("error_notice_mobile");
            if(mobileParam !=  null){
                try {
                    String mobile = mobileParam.getParamValue();
                    Map<String,Object> data = new HashMap<>();
                    data.put("msg",message+","+ DateFormatUtils.format(new Date(),"HH:mm:ss"));
                    SendSmsParam sendSmsParam = new SendSmsParam();
                    sendSmsParam.setMobile(new String[]{mobile});
                    sendSmsParam.setEventCode("SYS_ERROR_NOTICE");
                    sendSmsParam.setBizId("1");
                    sendSmsParam.setContent(new Object[]{data});
                    smsFacade.send(sendSmsParam);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }*/

        SysErrorLogPO sysErrorLogPO = new SysErrorLogPO();
        sysErrorLogPO.setType(errorType.name());
        sysErrorLogPO.setCreateTime(new Date());
        sysErrorLogPO.setMessage(message);
        sysErrorLogPO.setContent(content);
        sysErrorLogPOMapper.insert(sysErrorLogPO);
    }

    public String getErrorInfoFromException(Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString() ;
    }

}
