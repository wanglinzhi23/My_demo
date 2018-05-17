package intellif.database.dao.impl;

import intellif.database.dao.TaskInfoDao;
import intellif.database.entity.TaskInfo;

import javax.persistence.Table;

import org.springframework.stereotype.Service;

@Service
public class TaskInfoDaoImpl  extends AbstractCommonDaoImpl<TaskInfo> implements TaskInfoDao<TaskInfo>{
    

  
    @Override
    public Class<TaskInfo> getEntityClass() {
        // TODO -generated method stub
        return TaskInfo.class;
    }

    @Override
    public String getEntityTable() {
        Table table = TaskInfo.class.getAnnotation(Table.class);
        return table.schema()+"."+table.name();
    }
}
