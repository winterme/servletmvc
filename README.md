# servletmvc
 使用 servlet 模拟一个mvc


使用方法：

1. 修改DispatchServlet 上的@ComponentScan注解，修改里面的值为你的controller包名

2. Controller 类方法中第一个参数为 HttpServletRequest， 第二个参数为 HttpServletResponse，获取参数使用 ControllerUtil.getParam(request).get(key) 来获取

3. 返回页面的时候，不加 @com.zzq.servlet.annotation.ResponseBody 注解，返回 页面路径

4. 所有请求servlet 的请求以 .do 结尾