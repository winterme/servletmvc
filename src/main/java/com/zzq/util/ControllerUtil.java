package com.zzq.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerUtil {

    /**
     * 从 url，json，cookie 里面获取数据， 返回 map
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Map<String, String> getParam(HttpServletRequest request) throws IOException {
        Map<String, String> result = new HashMap<>();
        if (request.getParameterMap() != null) {
            for (String s : request.getParameterMap().keySet()) {
                result.put(s, request.getParameterMap().get(s)[0]);
            }
        }
        if (StringUtils.isNotBlank(request.getQueryString())) {
            String queryString = request.getQueryString();
            for (String s : queryString.split("&")) {
                String[] split = s.split("=");
                result.put(split[0], split[1]);
            }
        }
        if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").contains("application/json")) {
            BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream());
            int ind = 0;
            byte[] data = new byte[2048];
            StringBuffer param = new StringBuffer();
            while ((ind = inputStream.read(data)) != -1) {
                param.append(new String(data, 0, ind));
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(param.toString().getBytes(), Map.class);
            result.putAll(map);
        }
        if (request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                result.put(cookie.getName(), cookie.getValue());
            }
        }
        return result;
    }

    public static String getRequestUrl(HttpServletRequest request) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(request.getRequestURL());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            stringBuffer.append("?").append(request.getQueryString());
        }

        return stringBuffer.toString();
    }

}
