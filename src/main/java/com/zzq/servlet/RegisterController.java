package com.zzq.servlet;

import com.zzq.servlet.annotation.RequestPath;
import com.zzq.servlet.annotation.RequstClass;
import com.zzq.servlet.annotation.ResponseBody;
import com.zzq.util.ControllerUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequstClass("/index")
public class RegisterController {

    @RequestPath(path = "/lcm")
    @ResponseBody
    public Object lcm(HttpServletRequest request) throws IOException {
        return "lcm is best beautiful , I love she O(∩_∩)O parameter" + ControllerUtil.getParam(request).get("id");
    }

}
