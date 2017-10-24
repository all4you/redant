# 欢迎使用RedAnt


**RedAnt**是一款基于Netty的轻量级HTTP框架。特点概述：
 
- **对象管理** ：通过Bean注解，实现对象的全局、统一管理；
- **自动注入** ：通过Autowired注解，实现Bean对象的自动注入；
- **自由路由** ：使用RouterController、RouterMapping、RouterParam注解实现路由的自定义；
- **参数转换** ：通过TypeConverter接口，实现http请求的参数转换（目前支持将参数转换为：基础类型、Map、List、JavaBean）；
- **结果渲染** ：通过自定义的Render接口，对返回结果进行渲染，目前支持渲染html、xml、plain、json等数据。

-------------------
