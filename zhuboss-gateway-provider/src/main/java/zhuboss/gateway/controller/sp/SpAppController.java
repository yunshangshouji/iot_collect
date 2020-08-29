package zhuboss.gateway.controller.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.gateway.mapper.AppPOMapper;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.service.GatewayService;

@RestController
@RequestMapping("/sp/app")
public class SpAppController {

    @Autowired
    AppPOMapper appPOMapper;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    GatewayService gatewayService;




}
