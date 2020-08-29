package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddHisViewSortParam;
import zhuboss.gateway.service.param.UpdateHisViewSortParam;

public interface HisViewSortService {
    void add(AddHisViewSortParam addHisViewSortParam, Integer appId);

    void update(UpdateHisViewSortParam updateHisViewSortParam);

    void delete(Integer appId, Integer id);

    void changeOrder(Integer hisViewSortId, Integer num);
}
