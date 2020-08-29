package zhuboss.gateway.controller.browser;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.tree.CommonTree;
import zhuboss.gateway.dict.MeterTreeTypeEnum;
import zhuboss.gateway.mapper.MeterTreePOMapper;
import zhuboss.gateway.mapper.StationPOMapper;
import zhuboss.gateway.po.MeterTreePO;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.util.MeterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/station")
public class BrowseStationController {
    @Autowired
    StationPOMapper stationPOMapper;
    @Autowired
    MeterTreePOMapper meterTreePOMapper;

    @GetMapping("/tree")
    @ApiOperation("站点树")
    public List<StationPO> stationTree() {
        Integer appId = UserSession.getAppId();
        List<StationPO> meterTreePOList = stationPOMapper.selectByClause(new QueryClauseBuilder().andEqual(StationPO.Fields.APP_ID,appId));
        try {
            CommonTree<StationPO> authMenuTree = new CommonTree<StationPO>(meterTreePOList, null);
            List<StationPO> childs = authMenuTree.getRootNodes();
            childs.get(0).setIconCls("icon-zhu_prg");
            return childs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/dev/tree")
    public List<Object> devTree(@RequestParam(required = true) Integer stationId) {
        StationPO stationPO = stationPOMapper.selectByPK(stationId);
        List<MeterTreePO> meterTreePOList = meterTreePOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTreePO.Fields.STATION_ID,stationId));
        for (MeterTreePO meterTreePO : meterTreePOList) {
            meterTreePO.setExpanded(false);
            if(meterTreePO.getType().equals(MeterTreeTypeEnum.METER.name())){
                meterTreePO.setText(MeterUtil.getMeterName(meterTreePO.getDevName(),meterTreePO.getInterfaceType(),meterTreePO.getComPort(),meterTreePO.getAddr(),meterTreePO.getIp(),meterTreePO.getPort()));
            }
        }
        try {
            CommonTree<MeterTreePO> authMenuTree = new CommonTree<MeterTreePO>(meterTreePOList, null);
            List<MeterTreePO> childs = authMenuTree.getRootNodes();
            //根节点
            List<Object> results = new ArrayList<>();
            Map<String,Object> root = new HashMap<>();
            root.put("id",0);
            root.put("text",stationPO.getText());
            root.put("stationId",stationId);
            root.put("iconCls","icon-zhu_place");
            root.put("children",childs);
            results.add(root);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/dev/tree/echart")
    public Object devTreeEchart(@RequestParam(required = true) Integer stationId) {
        StationPO stationPO = stationPOMapper.selectByPK(stationId);
        List<MeterTreePO> meterTreePOList = meterTreePOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTreePO.Fields.STATION_ID,stationId));
        for (MeterTreePO meterTreePO : meterTreePOList) {
            meterTreePO.setExpanded(false);
            if(meterTreePO.getType().equals(MeterTreeTypeEnum.METER.name())){
                meterTreePO.setText(MeterUtil.getMeterName(meterTreePO.getDevName(),meterTreePO.getInterfaceType(),meterTreePO.getComPort(),meterTreePO.getAddr(),meterTreePO.getIp(),meterTreePO.getPort()));
            }
        }
        try {
            CommonTree<MeterTreePO> authMenuTree = new CommonTree<MeterTreePO>(meterTreePOList, null);
            List<MeterTreePO> childs = authMenuTree.getRootNodes();
            //根节点
            Map<String,Object> root = new HashMap<>();
            root.put("id",0);
            root.put("name",stationPO.getText());
            root.put("children",childs);
            //总节点数
            int leafCount = 0 ;
            for(MeterTreePO node : meterTreePOList){
                if(node.getChildren() == null || node.getChildren().size() == 0){
                    leafCount ++;
                }
            }
            root.put("leafCount",leafCount);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
