# servletmvc
 使用 servlet 模拟一个mvc


使用方法：

1. 修改DispatchServlet.getAllRequestPaths() 方法得packagePath 改为你的controller包路径，默认 com.zzq.servlet

2. Controller 类方法中第一个参数为 HttpServletRequest， 第二个参数为 HttpServletResponse，获取参数使用 ControllerUtil.getParam(request).get(key) 来获取

3. 返回页面的时候，不加 @com.zzq.servlet.annotation.ResponseBody 注解，返回 页面路径