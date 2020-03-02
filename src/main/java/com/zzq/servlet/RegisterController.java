package com.zzq.servlet;

import com.zzq.servlet.annotation.RequestPath;
import com.zzq.servlet.annotation.RequstClass;
import com.zzq.servlet.annotation.ResponseBody;
import com.zzq.util.ControllerUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RequstClass("/index")
public class RegisterController {

    @RequestPath(path = "/lcm")
    @ResponseBody
    public Object lcm(HttpServletRequest request) throws IOException {
        // map里面存储的就是 前端传过来的参数集
        // 例如 link?id=xx&name=xxx
        // map 里面就是  {id:xx,name:xxx}
        // 需要 id 则就是 param.get("id")
        Map<String, String> param = ControllerUtil.getParam(request);
        String id = param.get("id");

        return "lcm is best beautiful , I love she O(∩_∩)O parameter =>" + id ;
    }

}
