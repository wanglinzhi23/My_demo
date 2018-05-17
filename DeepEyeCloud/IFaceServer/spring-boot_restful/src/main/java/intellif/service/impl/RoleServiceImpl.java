package intellif.service.impl;

import intellif.dao.RoleDao;
import intellif.database.entity.RoleInfo;
import intellif.service.RoleServiceItf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yangboz on 11/16/15.
 */
@Service
public class RoleServiceImpl implements RoleServiceItf {

    @Autowired
    private RoleDao repository;

    @Override
    public Iterable<RoleInfo> findAll() {
        return repository.findAll();
    }
}
