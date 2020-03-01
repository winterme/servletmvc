package com.zzq.servlet;

import com.zzq.servlet.annotation.RequestPath;
import com.zzq.servlet.annotation.RequstClass;
import com.zzq.servlet.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequstClass("/index")
public class IndexController {

    @RequestPath(path = "/index")
    public String index(){
        return "/upload.html";
    }

    @ResponseBody
    @RequestPath(path = "/zhangzq")
    public Object zhangzq(){
        return "controller by zhangzq.... \n" +
                "controller by servlet....";
    }

    @ResponseBody
    @RequestPath(path = "/getJson")
    public Map getJson(){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("name","zhangzq");
        map.put("age",18);
        map.put("job", "program dev");

        return map;
    }

}
