package com.sports.sports.config;

import com.sports.sports.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("X-User-ID");
        String userName = request.getHeader("X-User-Name");
        String userRole = request.getHeader("X-User-Role");
        String userGender = request.getHeader("X-User-Gender");

        if (userId != null && !userId.isEmpty()) {
            UserContext.setCurrentUserId(Long.valueOf(userId));
        }
        if (userName != null) {
            UserContext.setCurrentUserName(userName);
        }
        if (userRole != null && !userRole.isEmpty()) {
            UserContext.setCurrentUserRole(Integer.valueOf(userRole));
        }
        if (userGender != null && !userGender.isEmpty()) {
            UserContext.setCurrentUserGender(Integer.valueOf(userGender));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
