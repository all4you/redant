package com.redant.core.session;

import com.redant.core.common.exception.InvalidSessionException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session辅助器
 * @author gris.wang
 * @date 2017/11/6
 */
public class SessionHelper {

    /**
     * 保存session对象的map
     */
    private Map<ChannelId,HttpSession> sessionMap;

    private static SessionHelper manager;

    private SessionHelper(){

    }


    //======================================


    /**
     * 获取单例
     * @return
     */
    public static SessionHelper instange(){
        synchronized (SessionHelper.class) {
            if (manager == null) {
                manager = new SessionHelper();
                if (manager.sessionMap == null) {
                    // 需要线程安全的Map
                    manager.sessionMap = new ConcurrentHashMap<ChannelId,HttpSession>();
                }
            }
        }
        return manager;
    }

    /**
     * 判断session是否存在
     * @param context
     * @return
     */
    public boolean containsSession(ChannelHandlerContext context){
        return context!=null && context.channel()!=null && context.channel().id()!=null && manager.sessionMap.get(context.channel().id())!=null;
    }

    /**
     * 添加一个session
     * @param context
     * @param session
     */
    public void addSession(ChannelHandlerContext context,HttpSession session){
        if(context==null || context.channel()==null || context.channel().id()==null || session==null){
            throw new InvalidSessionException("context or session is null");
        }
        manager.sessionMap.put(context.channel().id(),session);
    }

    /**
     * 获取一个session
     * @param context
     * @return
     */
    public HttpSession getSession(ChannelHandlerContext context){
        if(context==null || context.channel()==null || context.channel().id()==null){
            throw new InvalidSessionException("context is null");
        }
        return manager.sessionMap.get(context.channel().id());
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
            session = new HttpSession(context);
            manager.sessionMap.put(context.channel().id(),session);
        }
        return session;
    }

    /**
     * 清除过期的session
     * 需要在定时器中执行该方法
     */
    public void clearExpireSession(){
        Iterator<Map.Entry<ChannelId,HttpSession>> iterator = manager.sessionMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<ChannelId,HttpSession> sessionEntry = iterator.next();
            if(sessionEntry.getValue()==null || sessionEntry.getValue().isExpire()){
                iterator.remove();
            }
        }
    }

}
