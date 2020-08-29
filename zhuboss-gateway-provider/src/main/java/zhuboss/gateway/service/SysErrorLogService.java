package zhuboss.gateway.service;


import zhuboss.gateway.common.SysErrorType;

public interface SysErrorLogService {

    void log(SysErrorType errorType, String message, Exception e);

    void log(SysErrorType errorType, String message, String content);

}
