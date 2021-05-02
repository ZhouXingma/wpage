package com.acthat.wpage.interceptor;


import com.acthat.wpage.page.WPage;
import org.apache.ibatis.binding.MapperMethod;

public class WPageInterceptorUtil {
    private static final int WPAGE_PARAM_INDEX = 0;
    private static final String WPAGE_PARAM_PREFIX = "arg";
    /**
     * 获取分页参数信息
     * @param parameterObject 参数对象集合
     * @return 返回分页信息对象
     */
    public static WPage getWPageSql(Object parameterObject) {
        WPage wPage = null;
        if (parameterObject instanceof MapperMethod.ParamMap) {
            // 这里有个细节，如果Configuration中设置useActualParamName为false的时候这里的参数就变成了0，而不是arg0
            Object arg0 = ((MapperMethod.ParamMap)parameterObject).get(WPAGE_PARAM_PREFIX+WPAGE_PARAM_INDEX);
            if (arg0 instanceof WPage) {
                wPage = (WPage) arg0;
            }
        } else if(parameterObject instanceof WPage) {
            wPage = (WPage) parameterObject;
        }
        return wPage;
    }

    public static class SqlCovertUtil {
        /**
         * 将一个普通的SQL转换为获取总数的SQL语句
         * @param sql 要获取的SQL语句
         * @return 返回获取获取总数的SQL语句
         */
        public static String convertCountSql(String sql) {
            StringBuilder sb = new StringBuilder("select count(1) from ");
            String newSql = sql.substring(sql.indexOf("from") + 4,sql.lastIndexOf("limit"));
            sb.append(newSql);
            System.out.println(sb.toString());
            return sb.toString();
        }

        /**
         * 将一个普通的SQL转换为分页SQL语句
         * @param sql 进行转换分页的语句
         * @param offset 偏移量
         * @param limit 数量限制
         * @return 返回分页SQL语句
         */
        public static String covertPageSql(String sql, int offset, int limit) {
            StringBuilder sb = new StringBuilder(sql);
            sb.append(" limit ");
            if (offset >= 0) {
                sb.append(offset+",");
            }
            if (limit > 0) {
                sb.append(limit);
            }
            return sb.toString();
        }
    }
}
