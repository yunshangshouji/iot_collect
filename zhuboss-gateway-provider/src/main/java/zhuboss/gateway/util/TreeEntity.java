package zhuboss.gateway.util;

import lombok.Data;
import zhuboss.framework.mybatis.mapper.PrimaryKey;
import zhuboss.framework.utils.tree.AbsTreeEntity;

@Data
public abstract class TreeEntity<PK> extends AbsTreeEntity {
    public abstract PK getId();
}
