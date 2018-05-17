package intellif.dto;

public class CommonQueryDto {
protected long userId;
private String ids; //主健ID串，逗号拼接
private String searchName;
private int page;
private int pageSize;

public String getIds() {
    return ids;
}
public void setIds(String ids) {
    this.ids = ids;
}
public String getSearchName() {
    return searchName;
}
public void setSearchName(String searchName) {
    this.searchName = searchName;
}
public int getPage() {
    return page;
}
public void setPage(int page) {
    this.page = page;
}
public int getPageSize() {
    return pageSize;
}
public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
}
public long getUserId() {
    return userId;
}
public void setUserId(long userId) {
    this.userId = userId;
}

}
