package intellif.utils;

import intellif.consts.GlobalConsts;

import java.util.List;

import org.springframework.util.CollectionUtils;

public class SqlUtil {
    /**
     * 构造表
     * @param fieldList 需要查询字段集合
     * @param tableName 表别名
     * @return
     */
    public static String buildAllCameraTable(List<String> fieldList, String tableName) {
        StringBuffer sb = new StringBuffer();
        StringBuffer ss = new StringBuffer();
        for (String id : fieldList) {
            ss.append(",");
            ss.append(String.valueOf(id));
        }

        sb.append("(select ");
        sb.append(ss.toString().substring(1));
        sb.append(" from ");
        sb.append(GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_CAMERA_INFO);

        sb.append(" union ");

        sb.append("select ");
        sb.append(ss.toString().substring(1));
        sb.append(" from ");
        sb.append(GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_OTHER_CAMERA_INFO);
        sb.append(")");
        sb.append(tableName);
        return sb.toString();
    }
    
    /**
     * 
     * @param fieldList 需要查询字段集合
     * @param tableName 表别名
     * @return
     */
    public static String buildAllAreaTable(List<String> fieldList, String tableName) {
        StringBuffer sb = new StringBuffer();
        StringBuffer ss = new StringBuffer();
        for (String id : fieldList) {
            ss.append(",");
            ss.append(String.valueOf(id));
        }

        sb.append("(select ");
        sb.append(ss.toString().substring(1));
        sb.append(" from ");
        sb.append(GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_AREA);

        sb.append(" union ");

        sb.append("select ");
        sb.append(ss.toString().substring(1));
        sb.append(" from ");
        sb.append(GlobalConsts.INTELLIF_BASE + "." + GlobalConsts.T_NAME_OTHER_AREA);
        sb.append(")");
        sb.append(tableName);
        return sb.toString();
    }
    /**
     * 
     * @param fieldList
     * @param tableName
     * @return
     */
    public static String buildSelectSql(String tableName,List<String> FieldList, List<String> filterList) {
        StringBuffer sb = new StringBuffer();
        StringBuffer filterB = new StringBuffer();
        StringBuffer fieldB = new StringBuffer();
        for (String item : filterList) {
            filterB.append(" and ");
            filterB.append(item);
        }
        for (String item : FieldList) {
            fieldB.append(",");
            fieldB.append(item);
        }

        sb.append("select ");
        sb.append(fieldB.toString().substring(1));
        sb.append(" from ");
        sb.append(tableName);
        sb.append(" where 1 = 1 ");
        sb.append(filterB.toString());
        return sb.toString();
    }
    
    /**
     * 
     * @param fieldList
     * @param tableName
     * @return
     */
    public static String buildFilter(List<String> filterList) {
        StringBuffer filterB = new StringBuffer();
        filterB.append("1 = 1");
        if(!CollectionUtils.isEmpty(filterList)){
            for (String item : filterList) {
                filterB.append(" and ");
                filterB.append(item);
            }
        }
        return filterB.toString();
    }
}
