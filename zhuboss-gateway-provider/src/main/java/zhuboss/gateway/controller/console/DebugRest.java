package zhuboss.gateway.controller.console;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("debug")
@Slf4j
public class DebugRest {
	
	@RequestMapping("/{collectorId}")
	public String index(@PathVariable("collectorId") String collectorId, ModelMap map) {
		map.put("gwNo", collectorId);

		return "debug";
	}
	
}
