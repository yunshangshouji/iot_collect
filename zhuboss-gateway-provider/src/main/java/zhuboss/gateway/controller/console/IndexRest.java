package zhuboss.gateway.controller.console;

import org.springframework.beans.factory.annotation.Autowired;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.vo.CachedCollector;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.tx.channel.ChannelKeys;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import zhuboss.gateway.tx.channel.MyChannelGroup;

import java.util.*;

@Controller
@RequestMapping("console")
@Slf4j
public class IndexRest {
	@Autowired
	CollectorService collectorService;

	@RequestMapping("")
	public String index(ModelMap map) {
		//遍历所有在线连接
		List<Map<String,Object>> list = new ArrayList<>();
		Iterator<Channel> iterator = MyChannelGroup.allChannels.iterator();
		while(iterator.hasNext()) {
			Map<String,Object> row = new HashMap<>();
			Channel channel = iterator.next();
			if(channel.hasAttr(ChannelKeys.COLLECTOR_NO)) {
				String collectorId = ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_NO);
				CachedCollector cachedCollector = collectorService.getCachedCollector(collectorId);
				if(!cachedCollector.getCollector().getAppId().equals(UserSession.getAppId())){
					continue;
				}
				row.put("devNo", collectorId);
				row.put("ip", channel.remoteAddress().toString());
				row.put("text",ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_TEXT));
				list.add(row);
			}
		}
		map.put("list", list);
		return "index";
	}
	
	
}
