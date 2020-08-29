package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddHisViewParam;
import zhuboss.gateway.service.param.UpdateHisViewParam;

public interface HisViewService {
    void add(AddHisViewParam addHisViewParam, Integer appId);

    void update(UpdateHisViewParam updateHisViewParam);

    void delete(Integer appId,Integer id);

    void changeOrder(Integer hisViewId,Integer num);


}
