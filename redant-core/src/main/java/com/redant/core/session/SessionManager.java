package com.redant.core.session;

/**
 * Session管理器
 * @author houyi.wh
 * @date 2017/11/6
 */
public interface SessionManager {


    /**
     * 判断session是否存在
     */
    boolean sessionExists();

    /**
     * 添加一个session
     * @param session session对象
     */
    void addSession(HttpSession session);

    /**
     * 获取一个session
     * @return session对象
     */
    HttpSession getSession();

    /**
     * 获取一个session，获取不到时自动创建一个
     * @param createIfNull true：不存在时创建一个，false：不存在时也不创建
     * @return session对象
     */
    HttpSession getSession(boolean createIfNull);

    /**
     * 清除过期的session
     * 需要在定时器中执行该方法
     */
    void clearExpireSession();

}
