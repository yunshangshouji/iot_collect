package zhuboss.gateway.adapter.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class ModbusMeterType extends MeterType {
    private List<ReadInfo> readInfos = new ArrayList<>();

    public ReadInfo findByReadId(int readId){
        for(ReadInfo readInfo : readInfos){
            if(readInfo.getReadId() == readId){
                return readInfo;
            }
        }
        return  null;
    }

}
