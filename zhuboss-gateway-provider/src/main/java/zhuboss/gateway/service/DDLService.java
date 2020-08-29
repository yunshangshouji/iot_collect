package zhuboss.gateway.service;

public interface DDLService {

    String getTableName(Integer meterKindId);

    String getColName(Integer meterKindReadId);

    void syncColumns(Integer meterKindId);

    void dropIfExists(Integer meterKindId);

    void truncateTable(Integer meterKindId);

}
