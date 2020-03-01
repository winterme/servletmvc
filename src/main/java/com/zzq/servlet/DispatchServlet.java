package com.zzq.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzq.servlet.annotation.RequestPath;
import com.zzq.servlet.annotation.RequstClass;
import com.zzq.servlet.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet(urlPatterns = {"/index", "/index/*"})
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

        if (!map.keySet().contains(path)) {
            resp.getWriter().print(path + " is not found !");
            return;
        }
        Object result = executorMethod(path , req , resp);

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

    private Object executorMethod(String mpath , HttpServletRequest request , HttpServletResponse response) {
        try {
            HashMap<Method, Class> hashMap = map.get(mpath);
            Method next = hashMap.keySet().iterator().next();


            Class aClass = hashMap.get(next);

            Object o = null;
            if(next.getParameterTypes().length==1){
                o = next.invoke(aClass.newInstance() , request);
            }else if(next.getParameterTypes().length==2){
                next.invoke(aClass.newInstance(), request, response);
            }else{
                o = next.invoke(aClass.newInstance());
            }

            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "出错了";
    }

    public void getAllRequestPaths() {
        try {
            ArrayList<Class> classes = new ArrayList<>();

            String classpath = this.getClass().getResource("/").getFile();
            String packagePath = classpath + "/com/zzq/servlet";

            for (File file : new File(packagePath).listFiles()) {
                if (file.isFile()) {
                    try {
                        String name = file.getName();
                        name = name.substring(0, name.indexOf((byte) 46));
                        classes.add(Class.forName("com.zzq.servlet." + name));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

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
