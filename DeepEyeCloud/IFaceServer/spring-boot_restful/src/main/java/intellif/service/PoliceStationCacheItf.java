package intellif.service;

import com.google.code.ssm.api.InvalidateAssignCache;

import intellif.core.tree.Tree;
import intellif.database.entity.PoliceStation;
import intellif.service.impl.PoliceStationCacheImpl;

/**
 * Created by Zheng Xiaodong on 2017/4/30.
 * 单位缓存接口
 */
public interface PoliceStationCacheItf {
    Tree tree();

    void updatePoliceStationTreeValues(String fieldName,long sId,int value);

    PoliceStation save(PoliceStation station);

    void delete(long id);

}
