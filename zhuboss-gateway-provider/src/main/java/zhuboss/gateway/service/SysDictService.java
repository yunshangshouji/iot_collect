package zhuboss.gateway.service;

import com.google.common.collect.ListMultimap;
import zhuboss.framework.dict.Item;
import zhuboss.framework.validate.ValidateService;
import zhuboss.gateway.po.SysDictDataPO;

import java.util.List;

/**
 * 系统数据字典
 */
public interface SysDictService extends ValidateService {

    /**
     * 根据ehcace.xml文件定义，缓存1分钟
     * @return
     */
    ListMultimap<String, Item> cacheDict();

    SysDictDataPO get(String dictType, String itemValue);

    String getText(String dictType, String itemValue);

    String getText(List<Item> items, Object value);

}
