package zhuboss.gateway.service.vo;

import lombok.Data;

@Data
public class CheckRegisterResult {

    public static final int success = 0;
    public static final int fail = 1;

    private int result;
    private long timestamp;
    /**
     * 每 N 秒钟上传数据
     */
    private Integer reportPeriod;
}
