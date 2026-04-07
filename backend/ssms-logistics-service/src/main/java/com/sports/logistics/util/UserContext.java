package com.sports.logistics.util;

public class UserContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USER_ROLE_HOLDER = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getCurrentUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void setCurrentUserName(String userName) {
        USER_NAME_HOLDER.set(userName);
    }

    public static String getCurrentUserName() {
        return USER_NAME_HOLDER.get();
    }

    public static void setCurrentUserRole(Integer role) {
        USER_ROLE_HOLDER.set(role);
    }

    public static Integer getCurrentUserRole() {
        return USER_ROLE_HOLDER.get();
    }

    public static void clear() {
        USER_ID_HOLDER.remove();
        USER_NAME_HOLDER.remove();
        USER_ROLE_HOLDER.remove();
    }
}
