package zhuboss.gateway.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.SysDictDataPOMapper;
import zhuboss.gateway.po.SysDictDataPO;
import zhuboss.gateway.service.SysDictService;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysDictServiceImpl implements SysDictService {
    @Autowired
    SysDictDataPOMapper sysDictDataDomainMapper;


    @Override
    public ListMultimap<String, Item> cacheDict() {
        ListMultimap<String, Item> myMultimap = ArrayListMultimap.create();
        List<SysDictDataPO> dictDataList = sysDictDataDomainMapper.selectByClause(new QueryClauseBuilder().andEqual("status", "1").sort("dict_type").sort("sort"));
        String dictType = dictDataList.get(0).getDictType();
        List<Item> list = new ArrayList<>();
        for (SysDictDataPO dataDomain : dictDataList) {
            myMultimap.put(dataDomain.getDictType(),new Item(dataDomain.getItemValue(), dataDomain.getItemName()));
        }
        return myMultimap;
}

    @Override
    public SysDictDataPO get(String dictType, String itemValue) {
        List<SysDictDataPO> sysDictDataDomainList = sysDictDataDomainMapper.selectByClause(new QueryClauseBuilder().andEqual("dict_type",dictType).andEqual("item_value", itemValue));
        return sysDictDataDomainList.size()>0?sysDictDataDomainList.get(0):null;
    }

    @Override
    @Cacheable(value = CacheConstants.dict,key="#dictType+#itemValue")
    public String getText(String dictType, String itemValue) {
        if(itemValue == null){
            return  null;
        }
        SysDictDataPO sysDictDataPO = this.get(dictType,itemValue);
        if(sysDictDataPO == null){
            return null;
        }
        return sysDictDataPO.getItemName();
    }

    @Override
    public String getText(List<Item> items, Object value) {
        if(items == null || value == null) return null;
        for(Item item : items){
            if(item.getValue().equals(value) || item.getValue().toString().equals(value.toString())){
                return item.getText();
            }
        }
        return null;
    }

    @Override
    public boolean checkContains(String dictType, Object itemValue) {
        if( !(itemValue instanceof  String)){
            itemValue = String.valueOf(itemValue);
        }
        List<Item> itemList =  this.cacheDict().get(dictType);
        if(itemList == null) return false;
        for(Item item : itemList){
            if(item.getValue().equals(itemValue)) return true;
        }
        return false;
    }
}
