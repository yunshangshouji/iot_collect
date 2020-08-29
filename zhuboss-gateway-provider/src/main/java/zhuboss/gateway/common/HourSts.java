package zhuboss.gateway.common;

import java.util.*;

public class HourSts {
    static final Map<Integer,HourSts> HOUR_STS_MAP = new HashMap<>();
    public static HourSts getHourSts(Integer appId){
        HourSts hourSts = HOUR_STS_MAP.get(appId);
        if(hourSts == null){
            hourSts = new HourSts();
            HOUR_STS_MAP.put(appId,hourSts);
        }
        return hourSts;
    }

    HourStsHour[] overLimitHours = new HourStsHour[24];
    HourStsHour[] overLimitResumHours = new HourStsHour[24];
    HourStsHour[] signalHours = new HourStsHour[24];
    HourStsHour[] tcpFlowUpperHours = new HourStsHour[24];
    HourStsHour[] tcpFlowDownHours = new HourStsHour[24];

    public HourSts() {
        for(int i=0;i<24;i++){
            overLimitHours[i] = new HourStsHour();
            overLimitResumHours[i] = new HourStsHour();
            signalHours[i] = new HourStsHour();
            tcpFlowUpperHours[i] = new HourStsHour();
            tcpFlowDownHours[i] = new HourStsHour();
        }
    }

    public void addOverLimitEvent(){
        add(overLimitHours,1);
    }

    public void addOverLimitResumEvent(){
        add(overLimitResumHours,1);
    }

    public void addSignalEvent(){
        add(signalHours,1);
    }

    public void addUpperTcpFlow(Integer flow){
        add(tcpFlowUpperHours,flow);
    }

    public void addDownTcpFlow(Integer flow){
        add(tcpFlowDownHours,flow);
    }

    public List<HourStsResult> queryOverLimit(){
        return this.query(overLimitHours);
    }

    public List<HourStsResult> queryOverLimitResume(){
        return this.query(overLimitResumHours);
    }

    public List<HourStsResult> querySignal(){
        return this.query(signalHours);
    }

    public List<HourStsResult> queryTcpFlowUpper(){
        return this.query(tcpFlowUpperHours);
    }

    public List<HourStsResult> queryTcpFlowDown(){
        return this.query(tcpFlowDownHours);
    }

    private List<HourStsResult>  query(HourStsHour[] hours){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<HourStsResult> results = new ArrayList<>();
        for(int i=hour+1;i<24;i++){
            results.add(new HourStsResult(i,hours[i].getCount(day)));
        }
        for(int i=0;i<hour+1;i++){
            results.add(new HourStsResult(i,hours[i].getCount(day)));
        }
        return results;
    }

    public static void add(HourStsHour[] hourStsHours, Integer val) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hourStsHours[hour].getDay() != day){
            hourStsHours[hour].setDay(day);
            hourStsHours[hour].setCount(val);
        }else{
            hourStsHours[hour].setCount(hourStsHours[hour].getCount() + val);
        }
    }
}
