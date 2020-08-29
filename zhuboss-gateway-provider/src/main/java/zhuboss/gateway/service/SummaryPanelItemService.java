package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddSummaryPanelItemParam;

public interface SummaryPanelItemService {

    void add(AddSummaryPanelItemParam addSummaryItemParam, Integer appId);

    void delete(Integer appId, Integer id);

    void changeOrder(Integer summaryItemId, Integer num);
}
