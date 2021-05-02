package com.acthat.wpage.page;

public class WPage {
    private final int DEFAULT_PAGE = 1;
    private final int DEFAULT_LIMIT = 10;
    private int offset = 0;
    private int page = DEFAULT_PAGE;
    private int limit = DEFAULT_LIMIT;

    public WPage(int page, int limit) {
        this.page = page;
        this.limit = limit;
        if(page <= 0) {
            page = DEFAULT_PAGE;
        }
        if(limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        this.offset = (page - 1) * limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
