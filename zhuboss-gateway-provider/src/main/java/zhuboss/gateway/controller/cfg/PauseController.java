package zhuboss.gateway.controller.cfg;

import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.spring.mq.MqttSender;
import zhuboss.gateway.spring.mq.Pause;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.gateway.spring.mvc.WriteAction;

import java.util.Iterator;

@RestController
@RequestMapping("/cfg/pause")
public class PauseController {

    @GetMapping("query")
    public GridTable<Pause> query(){
        GridTable<Pause> gridTable = new GridTable<>();
        gridTable.setRows(MqttSender.pauseList);
        gridTable.setTotal(MqttSender.pauseList.size());
        return gridTable;
    }

    @RequestMapping(value = "add")
    @WriteAction
    public JsonResponse add(@RequestBody Pause pause){
        MqttSender.pauseList.add(pause);
        return new JsonResponse();
    }

    @RequestMapping(value = "update")
    @WriteAction
    public JsonResponse update(@RequestBody Pause pause){
        for(Pause item : MqttSender.pauseList){
            if(item.getId() == pause.getId()){
                BeanMapper.copy(pause,item);
            }
        }
        return new JsonResponse();
    }

    @RequestMapping(value = "delete")
    @WriteAction
    public JsonResponse delete(Long id){
        Iterator<Pause> iterator = MqttSender.pauseList.iterator();
        while(iterator.hasNext()){
            Pause pause = iterator.next();
            if(pause.getId() == id){
                iterator.remove();
            }
        }
        return new JsonResponse();
    }
}
