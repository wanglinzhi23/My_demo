package intellif.service.impl;

import intellif.dao.RuleInfoDao;
import intellif.service.RuleServiceItf;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class RuleServiceImpl implements RuleServiceItf {

    @Autowired
    RuleInfoDao ruleDao;


}
