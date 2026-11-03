package com.pet.config;

import com.pet.common.JwtUtil;
import com.pet.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器：校验JWT Token
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        if (token == null || token.isEmpty()) {
            returnNeedLogin(response, "未登录，请先登录");
            return false;
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            Integer role = claims.get("role", Integer.class);
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            request.setAttribute("username", claims.get("username", String.class));
            return true;
        } catch (Exception e) {
            returnNeedLogin(response, "登录已过期，请重新登录");
            return false;
        }
    }

    private void returnNeedLogin(HttpServletResponse response, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.error(401, msg);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
