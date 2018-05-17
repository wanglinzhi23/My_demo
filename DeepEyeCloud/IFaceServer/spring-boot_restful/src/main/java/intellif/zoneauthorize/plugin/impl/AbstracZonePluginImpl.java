package intellif.zoneauthorize.plugin.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.collect.Sets;

import intellif.core.tree.itf.TreeNode;
import intellif.core.tree.util.TreeUtil;
import intellif.zoneauthorize.conf.ZoneConfig;
import intellif.zoneauthorize.controller.SystemSwitchController;
import intellif.zoneauthorize.plugin.ZoneAuthorizePluginItf;
import intellif.database.entity.TableVersion;

public abstract class AbstracZonePluginImpl<T extends TreeNode> implements ZoneAuthorizePluginItf<T> {

    private static Logger LOG = LogManager.getLogger(AbstracZonePluginImpl.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;


    /**
     * 每条insert语句插入最大记录数
     */
    public static final int INSERT_MAX_PER_SQL = 10000;

    public AbstracZonePluginImpl() {
        ZoneConfig.getPluginMap().put(this.zoneClass(), this);
        ZoneConfig.getNodeTypeMap().put(TreeUtil.nodeType(this.zoneClass()), this.zoneClass());
    }

    @Override
    public List<T> findAll() {
        Class<?> pathTreeNodeClass = zoneClass();
        Table table = pathTreeNodeClass.getAnnotation(Table.class);
        return jdbcTemplate.query("select * from `" + table.schema() + "`.`" + table.name() + "`", new BeanPropertyRowMapper<>(zoneClass()));
    }

    @Override
    public long updateVersion() {
        Class<?> pathTreeNodeClass = zoneClass();
        Table table = pathTreeNodeClass.getAnnotation(Table.class);
        Table tableVersion = TableVersion.class.getAnnotation(Table.class);
        try {
            Long updateVersion = jdbcTemplate.queryForObject(
                    "select update_version from `" + tableVersion.schema() + "`.`" + tableVersion.name() + "` where db_name = ? and table_name = ? limit 0, 1",
                    Long.class, table.schema(), table.name());
            return null == updateVersion ? 0L : updateVersion;
        } catch (Exception e) {
            LOG.error("cache exception: ", e);
            return 0L;
        }
    }

    @Override
    public void remove(long userId) {
        Class<?> userToZoneClass = userToZoneClass();
        Table userToZoneTable = userToZoneClass.getAnnotation(Table.class);
        jdbcTemplate.update("delete from `" + userToZoneTable.schema() + "`.`" + userToZoneTable.name() + "` where user_id = ?", userId);
    }

    @Override
    public Set<Long> findIdSet(long userId) {
        Class<?> userToZoneClass = userToZoneClass();
        Table userToZoneTable = userToZoneClass.getAnnotation(Table.class);
        String sql = "select " + foreignKey() + " from `" + userToZoneTable.schema() + "`.`" + userToZoneTable.name() + "` where user_id = ?";
        List<Long> idList = jdbcTemplate.queryForList(sql, Long.class, userId);
        return Sets.newHashSet(idList);
    }

    @Override
    public void save(long userId, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        Class<?> userToZoneClass = userToZoneClass();
        Table userToZoneTable = userToZoneClass.getAnnotation(Table.class);
        int index = 0;
        StringBuilder sqlBegin = new StringBuilder().append("insert into `").append(userToZoneTable.schema()).append("`.`").append(userToZoneTable.name()).append("` (user_id, ")
                .append(foreignKey()).append(") values ");
        StringBuilder sqlBuilder = new StringBuilder(sqlBegin);
        for (Long id : ids) {
            if (index != 0) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append("(").append(userId).append(", ").append(id).append(")");
            index++;
            if (index >= INSERT_MAX_PER_SQL) {
                jdbcTemplate.update(sqlBuilder.toString());
                index = 0;
                sqlBuilder.setLength(0);
                sqlBuilder.append(sqlBegin);
            }
        }
        if (index != 0) {
            jdbcTemplate.update(sqlBuilder.toString());
        }
    }
}
