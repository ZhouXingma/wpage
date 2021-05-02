#WPAGE
## 说明
1、这是一个基于mybatis，拦截器的方式实现的自动分页插件

2、该功能并不完善，只提供学习，了解自动分页原理

3、没有计划慢慢进行完善

## 使用方式

```
public interface UserMapping {
     WPageInfo<User> pageUser(WPage wPage, @Param("userQo") UserQo userQo);
}
```

返回的结果必须为WPageInfo<T>对象。

```
<select id="pageUser" resultMap="BaseMapper">
    select * from user
    <where>
        <if test="null != userQo.minAge">
            and age >= #{userQo.minAge}
        </if>
        <if test="null != userQo.maxAge">`这里输入代码`
            and age &lt;= #{userQo.maxAge}
        </if>
    </where>
</select>
```

测试代码：

```
......

UserMapping mapper = session.getMapper(UserMapping.class);
UserQo userQo = new UserQo();
userQo.setMinAge(10);
userQo.setMaxAge(30);
WPageInfo<User> userWPageInfo = mapper.pageUser(new WPage(2, 10),userQo);
if(null != userWPageInfo) {
     System.out.println(JSON.toJSONString(userWPageInfo));
} else {
     System.out.println("is null");
}
......

```

返回结果如下：

```
{"limit":10,"offset":10,"page":2,"results":[{"age":21,"id":32,"name":"sqq"}],"total":11}
```

