package com.acthat.wpage.page;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WPageInfo<T> implements Serializable {
    protected int offset;
    protected int page;
    protected int limit;
    protected List<T> results = null;
    protected long total;

    private WPageInfo(){}

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

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * 构建器
     * @param <T> 结果类型
     */
    public static class Build<T> {
        /**
         * 分页信息
         */
        private int page = -1;
        /**
         * limit获取数量的限制信息
         */
        private int limit = -1;
        /**
         * 结果信息
         */
        private List<T> results = null;
        /**
         * 总数信息
         */
        private long total = -1L;

        public Build<T> setPage(int page) {
            this.page = page;
            return this;
        }

        public Build<T> setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public Build<T> setResults(List<T> results) {
            this.results = results;
            return this;
        }

        public Build<T> setTotal(long total) {
            this.total = total;
            return this;
        }

        /**
         * 进行构建的方法
         * @return
         */
        public WPageInfo<T> build() {
            if(null == results) {
                results = new ArrayList<>();
            }
            if(page <= 0) {
                throw new RuntimeException("页信息设置异常");
            }
            if(limit < 0) {
                throw new RuntimeException("数量限制信息设置异常");
            }
            if(total <= 0) {
                throw new RuntimeException("总数信息设置异常");
            }
            WPageInfo<T> result = new WPageInfo<>();
            result.setTotal(total);
            result.setResults(results);
            result.setLimit(limit);
            result.setPage(page);
            result.setOffset((page - 1) * limit);
            return result;
        }
    }
}
