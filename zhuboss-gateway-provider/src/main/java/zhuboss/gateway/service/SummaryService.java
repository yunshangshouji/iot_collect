package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddSummaryParam;
import zhuboss.gateway.service.param.UpdateSummaryParam;

public interface SummaryService {

    void add(AddSummaryParam addSummaryParam, Integer appId);

    void update(UpdateSummaryParam updateSummaryParam);

    void delete(Integer appId,Integer id);

    void changeOrder(Integer summaryId,Integer num);
}
