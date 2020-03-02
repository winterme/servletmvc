package com.zzq.servlet.controller;

import com.zzq.servlet.annotation.RequestPath;
import com.zzq.servlet.annotation.RequstClass;
import com.zzq.servlet.annotation.ResponseBody;

@RequstClass("/index")
public class UserInfoController {

    @RequestPath(path = "/pkusoft.do")
    @ResponseBody
    public String lcm(){
        return "pkusoft";
    }

}
