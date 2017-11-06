package com.redant.core.session;

import com.redant.common.exception.InvalidSessionException;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session管理器
 * @author gris.wang
 * @since 2017/11/6
 */
public class SessionManager {

    /**
     * 保存session对象的map
     */
    private Map<ChannelHandlerContext,HttpSession> sessionMap;

    private static SessionManager manager;

    private SessionManager(){

    }



    //======================================


    /**
     * 获取单例
     * @return
     */
    public static SessionManager instange(){
        synchronized (SessionManager.class) {
            if (manager == null) {
                manager = new SessionManager();
                if (manager.sessionMap == null) {
                    // 需要线程安全的Map
                    manager.sessionMap = new ConcurrentHashMap<ChannelHandlerContext, HttpSession>();
                }
            }
        }
        return manager;
    }

    /**
     * 添加一个session
     * @param context
     * @param session
     */
    public void addSession(ChannelHandlerContext context,HttpSession session){
        if(context==null || session==null){
            throw new InvalidSessionException("context or session is null");
        }
        manager.sessionMap.put(context,session);
    }

    /**
     * 获取一个session
     * @param context
     * @return
     */
    public HttpSession getSession(ChannelHandlerContext context){
        if(context==null){
            throw new InvalidSessionException("context is null");
        }
        return manager.sessionMap.get(context);
    }

    /**
     * 获取一个session，获取不到时自动创建一个
     * @param context
     * @param createIfNull
     * @return
     */
    public HttpSession getSession(ChannelHandlerContext context,boolean createIfNull){
        HttpSession session = getSession(context);
        if(session==null && createIfNull){
            session = new HttpSession(context.channel().id(),context);
        }
        return session;
    }

    /**
     * 清除过期的session
     * 需要在定时器中执行该方法
     */
    public void clearExpireSession(){
        Iterator<Map.Entry<ChannelHandlerContext,HttpSession>> iterator = manager.sessionMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<ChannelHandlerContext,HttpSession> sessionEntry = iterator.next();
            if(sessionEntry.getValue()==null || sessionEntry.getValue().isExpire()){
                iterator.remove();
            }
        }
    }




}
