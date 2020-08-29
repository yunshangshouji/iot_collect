package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddHisViewSortParam extends SaveHisViewSortParam {
    @NotNull
    private Integer viewId;
}
