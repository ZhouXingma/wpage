package com.acthat.wpage.interceptor;

import com.acthat.wpage.page.WPage;
import com.acthat.wpage.page.WPageInfo;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class WPageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
            // StatementHandler 拦截处理
            return statementHandleInterceptor(invocation);
        } else if (target instanceof ResultSetHandler) {
            // ResultSetHandler 拦截处理
            return resultSetHandlerInterceptor(invocation);
        } else {
            // 其它方法拦截
            return invocation.proceed();
        }
    }

    /**
     * 处理statementHandle拦截
     * 这个方法主要处理的是对BoundSql的sql语句的修改
     * @param invocation 执行的信息
     * @return 执行后的结果，这里一般情况下返回的是个Statement
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object statementHandleInterceptor(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        // Statement 处理器
        StatementHandler statementHandler = (StatementHandler) target;
        // Statement 处理器的元对象
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // 获取BoundSql，主要是用来获取执行的sql语句
        BoundSql boundSql = statementHandler.getBoundSql();
        // 参数信息，调用方法的参数，不是调用当前方法，是调用Mapper方法
        Object parameterObject = boundSql.getParameterObject();
        // 获取分页信息
        WPage wPage = WPageInterceptorUtil.getWPageSql(parameterObject);
        if (null != wPage) {
            // 转换参数信息
            String pageSql = WPageInterceptorUtil.SqlCovertUtil.covertPageSql(boundSql.getSql(),wPage.getOffset(),wPage.getLimit());
            // 更换sql信息
            metaObject.setValue("delegate.boundSql.sql",pageSql);
        }
        // 拦截器进行拦截处理
        return invocation.proceed();
    }

    private Object resultSetHandlerInterceptor(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        // 执行对象
        DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) target;
        // 执行对象的元对象
        MetaObject metaObject = SystemMetaObject.forObject(defaultResultSetHandler);

        // 1 先执行，获取整体的结果
        List<Object> result = (List<Object>) invocation.proceed();

        // 2、获取分页信息
        // 2.1 设置BoundSql
        BoundSql boundSql = (BoundSql) metaObject.getValue("boundSql");
        // 2.2 参数对象
        Object parameterObject = boundSql.getParameterObject();
        // 2.3 获取到分页信息
        WPage wPageSql = WPageInterceptorUtil.getWPageSql(parameterObject);
        // 如果参数为空，说明不需要进行总数计算包装结果
        if (null == wPageSql) {
            return result;
        }

        // 3、处理总数信息
        // 3.1 参数处理器
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("parameterHandler");
        // 3.2 转换为总数数sql
        String countSql = WPageInterceptorUtil.SqlCovertUtil.convertCountSql(boundSql.getSql());
        // 3.3 获取执行的Statement
        Statement statement = (Statement) invocation.getArgs()[0];
        // 3.4 获取Connection
        Connection connection = statement.getConnection();
        // 3.5 创建预执行的PreparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(countSql);

        // 处理参数信息
        parameterHandler.setParameters(preparedStatement);
        preparedStatement.execute();
        long total = 0;
        ResultSet resultSet = preparedStatement.getResultSet();
        if(resultSet.next()) {
            total = resultSet.getLong(1);
        }

        // 这是真正的返回结果
        WPageInfo<Object> wPageInfo = new WPageInfo.Build<>().setResults(result).setPage(wPageSql.getPage())
                .setLimit(wPageSql.getLimit()).setTotal(total).setResults(result).build();
        List<Object> resultList = new ArrayList<>();
        resultList.add(wPageInfo);
        return resultList;
    }

}
