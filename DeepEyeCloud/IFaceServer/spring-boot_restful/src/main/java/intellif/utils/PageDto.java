package intellif.utils;

import java.util.List;

/**
 * Helper class that implements paging using count
 *
 * @author Zheng Xiaodong
 */
public class PageDto<T> {
    private static final int DEFALT_PAGE_SIZE = 10;

    private List<T> data;

    private long count;

    private int page;

    private int pageSize;
    public PageDto(List<T> data) {
        this.data = data;
        this.count = 0;
        this.page = 1;
        this.pageSize = DEFALT_PAGE_SIZE;
    }
    public PageDto(List<T> data, long count, int page, int pageSize) {
        if (count < 0)
            count = 0;
        if (page < 0)
            page = 1;
        if (pageSize < 0)
            pageSize = DEFALT_PAGE_SIZE;

        this.data = data;
        this.count = count;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
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

    public long getMaxPages() {
        long maxPages = 0;

        if (pageSize > 0) {
            if (count % pageSize == 0) {
                maxPages = count / pageSize;
            } else {
                maxPages = (count / pageSize) + 1;
            }
        }

        if (maxPages == 0)
            maxPages = 1;

        return maxPages;
    }
}

