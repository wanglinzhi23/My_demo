package intellif.audit;

/**
 * Created by yangboz on 12/2/15.
 */
public interface AuditableItf {

    // 日志（消息）标题缩略信息
    public String getTitle();

    public void setTitle(String title);

    // 日志（消息）描述信息
    public String getMessage();

    public void setMessage(String message);

    // 操作CRUD
    public String getOperation();

    public void setOperation(String operation);

    // 操作的对象（表名）
    public String getObject();

    public void setObject(String object);

    // 操作的对象id
    public long getObjectId();

    public void setObjectId(long object_id);

    // 对象变更后的状态（主要针对人的各种分类消息，已入库、已过期、已完成、已删除、已更新，抓捕）
    public long getObject_status();

    public void setObject_status(long object_status);

    // 发起人（无则为system）
    public String getOwner();

    public void setOwner(String owner);
}
