package zhuboss.gateway.common;

import lombok.Data;

@Data
public class HourStsResult {
    private int hour;
    private long count;

    public HourStsResult(int hour, long count) {
        this.hour = hour;
        this.count = count;
    }
}
