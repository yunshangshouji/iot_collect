package zhuboss.gateway.controller.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmMeterVO {
    private String devName;

    private String stationName;

    private Date offlineTime;

}
