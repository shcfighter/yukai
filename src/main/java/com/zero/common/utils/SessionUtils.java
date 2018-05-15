package com.zero.common.utils;

import com.zero.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class SessionUtils {

    private static final String USER_SESSION = "user_session";

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    public static User getCurrentUser(HttpServletRequest request){
        return (User) request.getSession().getAttribute(USER_SESSION);
    }

    public static void putCurrentUser(HttpServletRequest request, User user){
        request.getSession().setAttribute(USER_SESSION, user);
    }

    public static Integer getCurrentUserId(HttpServletRequest request){
        User loginUser = getCurrentUser(request);
        if(Objects.isNull(loginUser)){
            return null;
        }
        return loginUser.getId();
    }

    public static String getCurrentUserName(HttpServletRequest request){
        User loginUser = getCurrentUser(request);
        if(Objects.isNull(loginUser)){
            return null;
        }
        return loginUser.getLoginName();
    }

    public static void removeCurrentUser(HttpServletRequest request){
        request.getSession().invalidate();
    }
}
