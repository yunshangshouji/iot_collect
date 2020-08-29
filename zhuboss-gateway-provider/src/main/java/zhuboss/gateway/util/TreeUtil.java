package zhuboss.gateway.util;

import com.alibaba.fastjson.util.TypeUtils;
import org.apache.commons.beanutils.ConvertUtils;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;

import java.io.Serializable;
import java.util.List;

public class TreeUtil {
    
    public static <PO extends TreeEntity, PK extends Serializable> void move(PK sourceId, PK targetId, String point, BaseMapper<PO,PK> mapper){
        TreeEntity target = (TreeEntity)mapper.selectByPK(targetId);
        Object pid = null;
        if(point.equals("append")){ //子节点
            pid = targetId;
        }else if(point.equals("top") || point.equals("bottom")){ //上移、下移
            pid =target.getPid();
        }else{
            throw new BussinessException("point error:" + point);
        }
        List<PO> list= mapper.selectByClause(new QueryClauseBuilder().andEqual("pid",pid).sort("seq"));
        PO targetPO = null,sourcePO = null;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getId().equals(targetId)){
                targetPO = list.get(i);
            }
            if(list.get(i).getId().equals(sourceId)){
                sourcePO = list.get(i);
            }
        }
        //修改
        if(sourcePO!=null){
            list.remove(sourcePO);
        }else{
            sourcePO = mapper.selectByPK(sourceId);
            sourcePO.setPid(point.equals("append")? (target.getId()+ ""):target.getPid());
        }
        if(targetPO !=null){
            int idx = list.indexOf(targetPO);
            if(point.equals("bottom")){
                idx ++ ;
            }
            list.add(idx,sourcePO); //插入
        }else{
            list.add(sourcePO); //末尾添加
        }
        //保存
        for(int i=0;i<list.size();i++){
            PO item = list.get(i);
            if(item.equals(sourcePO)){
                sourcePO.setSeq(i);
                mapper.updateByPK(sourcePO);
                continue;
            }
            if(item.getSeq()== null || item.getSeq()!=i){
                item.setSeq(i);
                mapper.updateByPK(item);
            }
        }
    }
    
}
