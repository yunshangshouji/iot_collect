package zhuboss.gateway.facade.mq;

public class TopicPath {

    /**
     * 担心多个告警项，被丢失
     * @param mqTypeEnum
     * @param appId
     * @param devNo
     * @return
     */
    public static String getUpperPathVar(MqTypeEnum mqTypeEnum,Integer appId, String devNo){
        return "/"+mqTypeEnum.name()+"/"+appId+"/"+devNo+"/"+"/upper";
    }

    public static String getReadPath(Integer appId, String devNo){
        return "/"+MqTypeEnum.read.name()+"/"+appId+"/"+devNo+"/upper";
    }

    public static String getSignalPath(Integer appId, String devNo){
        return "/"+MqTypeEnum.signal.name()+"/"+appId+"/"+devNo+"/upper";
    }

    public static String getCollectorOfflinePath(Integer appId, String devNo){
        return "/"+ MqTypeEnum.collector_offline.name() +"/"+appId+"/"+devNo+"/upper";
    }

    public static String getMeterOfflinePath(Integer appId, String devNo){
        return "/"+ MqTypeEnum.meter_offline.name() +"/"+appId+"/"+devNo+"/upper";
    }

    public static String getMeterOnlinePath(Integer appId, String devNo){
        return "/"+ MqTypeEnum.meter_online.name() +"/"+appId+"/"+devNo+"/upper";
    }
}
