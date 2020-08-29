package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddHisViewColumnParam;
import zhuboss.gateway.service.param.AddHisViewParam;
import zhuboss.gateway.service.param.UpdateHisViewColumnParam;
import zhuboss.gateway.service.param.UpdateHisViewParam;

public interface HisViewColumnService {
    void add(AddHisViewColumnParam addHisViewColumnParam, Integer appId);

    void update(UpdateHisViewColumnParam updateHisViewColumnParam);

    void delete(Integer appId, Integer id);

    void changeOrder(Integer hisViewColumnId, Integer num);


}
