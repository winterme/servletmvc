package com.zzq.servlet;

import com.zzq.servlet.annotation.RequestPath;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

public class XXXX {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> aClass = Class.forName(RegisterController.class.getName());
        for (Method method : aClass.getMethods()) {
            if(method.isAnnotationPresent(RequestPath.class)){
                for (Class<?> parameterType : method.getParameterTypes()) {
                    if(method.getName().equals("xxx")){
                        Object invoke = method.invoke(aClass.newInstance(), 12, 34);
                        System.out.println(invoke);
                    }
                }
            }
        }
    }
}
