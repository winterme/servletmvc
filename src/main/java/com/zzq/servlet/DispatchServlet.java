package com.zzq.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzq.servlet.annotation.ComponentScan;
import com.zzq.servlet.annotation.RequestPath;
import com.zzq.servlet.annotation.RequstClass;
import com.zzq.servlet.annotation.ResponseBody;
import com.zzq.util.ControllerUtil;
import com.zzq.util.StringUtils;
import javassist.ClassPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

@ComponentScan("com.zzq.servlet")
@WebServlet(urlPatterns = { "*.do"})
public class DispatchServlet extends HttpServlet {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static HashMap<String, HashMap<Method, Class>> map;

    private static ArrayList<String> needRespJson;

    static {
        map = new HashMap<>();
        needRespJson = new ArrayList<>();
    }

    public DispatchServlet() {
        this.getAllRequestPaths();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");

        String path = req.getRequestURI().toString();

        if(StringUtils.isNotBlank(req.getContextPath())){
            path = ControllerUtil.distinctString(path.replace(req.getContextPath(),"/") , "/");
        }

        if (!map.keySet().contains(path)) {
            resp.getWriter().print(path + " is not found !");
            return;
        }
        Object result = executorMethod(path, req, resp);

        if (result == null) {
            return;
        }

        // 返回json 数据
        if (needRespJson.contains(path)) {
            resp.setHeader("Content-Type", "application/json;charset=UTF-8");
            resp.getWriter().print(objectMapper.writeValueAsString(result));
            return;
        }

        // 跳转页面
        resp.sendRedirect(String.valueOf(result));

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    private Object executorMethod(String mpath, HttpServletRequest request, HttpServletResponse response) {
        try {
            HashMap<Method, Class> hashMap = map.get(mpath);
            Method next = hashMap.keySet().iterator().next();


            Class aClass = hashMap.get(next);

            Object o = null;
            if (next.getParameterTypes().length == 1) {
                o = next.invoke(aClass.newInstance(), request);
            } else if (next.getParameterTypes().length == 2) {
                next.invoke(aClass.newInstance(), request, response);
            } else {
                o = next.invoke(aClass.newInstance());
            }

            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "出错了";
    }

    public void getClasses(ArrayList<Class> classes, File file, ClassPool classPool) throws IOException, ClassNotFoundException {
        for (File listFile : file.listFiles()) {
            if (listFile == null || !file.exists()) {
                return ;
            }
            if (listFile.isDirectory()) {
                getClasses(classes, listFile, classPool);
            }
            if (listFile.isFile()) {
                classes.add(Class.forName(classPool.makeClass(new FileInputStream(listFile)).getName()));
            }
        }
    }

    public void getAllRequestPaths() {
        try {
            String BASE_PACKAGE = "";
            if (this.getClass().isAnnotationPresent(ComponentScan.class)) {
                BASE_PACKAGE = this.getClass().getAnnotation(ComponentScan.class).value();
                if (!StringUtils.isNotBlank(BASE_PACKAGE)) {
                    throw new RuntimeException("DispatchServlet 必须使用 @ComponentScan 覆盖!!!并且必须指定扫描包名！");
                }
            } else {
                throw new RuntimeException("DispatchServlet 必须使用 @ComponentScan 覆盖!!!");
            }
            // class pool
            ClassPool classPool = ClassPool.getDefault();

            String classpath = this.getClass().getResource("/").getFile();
            String packagePath = classpath + BASE_PACKAGE.replace(new String(new byte[]{(byte) 46}), "/");

            ArrayList<Class> classes = new ArrayList<>();
            getClasses(classes, new File(packagePath) , classPool);

            for (Class aClass : classes) {
                if (aClass.isAnnotationPresent(RequstClass.class)) {

                    RequstClass rqueestPath = (RequstClass) aClass.getAnnotation(RequstClass.class);

                    // 根路径
                    String basePath = rqueestPath.value();
                    if (basePath.equals("") || basePath == null) {
                        basePath = "";
                    }

                    for (Method method : aClass.getMethods()) {
                        if (method.isAnnotationPresent(RequestPath.class)) {
                            RequestPath path = method.getAnnotation(RequestPath.class);

                            // 标识是否需要返回json格式数据
                            if (method.isAnnotationPresent(ResponseBody.class)) {
                                needRespJson.add(basePath + path.path());
                            }

                            HashMap<Method, Class> mc = new HashMap<>();
                            mc.put(method, aClass);

                            log.debug(String.format("Mapped \"{%s}\" onto %s", (basePath + path.path()), method.toString()));
                            map.put(basePath + path.path(), mc);
                        }
                    }

                }
            }
        } catch (Exception e) {
        }
    }


}
