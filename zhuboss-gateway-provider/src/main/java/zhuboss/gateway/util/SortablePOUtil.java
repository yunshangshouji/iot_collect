package zhuboss.gateway.util;

import org.springframework.util.Assert;
import zhuboss.framework.mybatis.mapper.BaseMapper;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.po.AbstractSortablePO;

import java.util.List;

public class SortablePOUtil {

    public  static <PO extends AbstractSortablePO> void sort(BaseMapper<PO,Integer> mapper, Enum groupProperty,PO sortablePO, Integer num){
        Assert.isTrue(num ==1 || num ==-1);
        PO po = mapper.selectByPK(sortablePO.get_Id());
        List<PO> poList = mapper.selectByClause(new QueryClauseBuilder()
                .andEqual(groupProperty,sortablePO.get_GroupId())
                .sort("seq")
        );
        int index = 0;
        for(int i=0;i<poList.size();i++){
            if(poList.get(i).get_Id().equals(sortablePO.get_Id())){
                index = i;
                break;
            }
        }
        //
        if(
                (index ==0 && num == -1) || (index == poList.size()-1 && num ==1)
        ){ //首行上升,尾行下降
            return;
        }
        //
        if(num == -1){
            PO pre = poList.get(index-1);
            pre.set_Seq(pre.get_Seq()+1);
            mapper.updateByPK(pre);
            sortablePO.set_Seq(sortablePO.get_Seq() - 1);
            mapper.updateByPK(sortablePO);
        }else if(num == 1){
            PO after = poList.get(index+1);
            after.set_Seq(after.get_Seq()-1);
            mapper.updateByPK(after);
            sortablePO.set_Seq(sortablePO.get_Seq() + 1);
            mapper.updateByPK(sortablePO);
        }

    }
}
