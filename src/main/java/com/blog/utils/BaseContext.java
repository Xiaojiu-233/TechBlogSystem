package com.blog.utils;

//每一个线程的共享数据区
public class BaseContext {

    //当前用户Id
    private static ThreadLocal<Long> currentId = new ThreadLocal<>();
    //当前用户是否获得管理员权限 0:还未判定 1：授权 -1:未授权 使用时需要重置！
    private static ThreadLocal<Boolean> isAdmin = new ThreadLocal<>();
    //字符串
    private static ThreadLocal<String> token = new ThreadLocal<>();

    //存数据
    public static void setCurrentId(Long id){
        currentId.set(id);
    }
    //读取数据
    public static Long getCurrentId(){
        return currentId.get();
    }

    //存数据
    public static void setIsAdmin(boolean admin){
        isAdmin.set(admin);
    }
    //读取数据
    public static boolean getIsAdmin(){
        return isAdmin.get();
    };

    //存数据
    public static void setToken(String t){
        token.set(t);
    }
    //读取数据
    public static String getToken(){return token.get();};



}
