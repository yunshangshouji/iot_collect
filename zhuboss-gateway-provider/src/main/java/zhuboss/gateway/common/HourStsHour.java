package zhuboss.gateway.common;

import lombok.Data;

/**
 * 近24小时合计
 */
@Data
public class HourStsHour {
    /**
     * 天/年，区分24小时循环覆盖
     */
    private int day = -1;
    private long count = 0;
    public long getCount(long day){
        if(day == this.day){
            return count;
        }else{
            return 0;
        }
    }
}
