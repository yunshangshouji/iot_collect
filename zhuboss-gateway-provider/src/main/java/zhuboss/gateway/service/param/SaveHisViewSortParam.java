package zhuboss.gateway.service.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveHisViewSortParam {
    @NotNull
    private Integer meterKindReadId;

    @NotEmpty
    private String sort;
}
