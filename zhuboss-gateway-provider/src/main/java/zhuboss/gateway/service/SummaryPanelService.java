package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddSummaryPanelParam;
import zhuboss.gateway.service.param.AddSummaryParam;
import zhuboss.gateway.service.param.UpdateSummaryPanelParam;
import zhuboss.gateway.service.param.UpdateSummaryParam;

public interface SummaryPanelService {

    void add(AddSummaryPanelParam addSummaryParam, Integer appId);

    void update(UpdateSummaryPanelParam updateSummaryParam);

    void delete(Integer appId, Integer id);

    void changeOrder(Integer summaryId, Integer num);
}
