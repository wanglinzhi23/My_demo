package intellif.service;

import intellif.database.entity.RoleInfo;


/**
 * Created by yangboz on 11/16/15.
 */
public interface RoleServiceItf {
    Iterable<RoleInfo> findAll();
}
