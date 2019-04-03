package com.redant.core.executor;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

/**
 * @author houyi
 */
public interface Executor<T> {

    /**
     * 同步执行任务获得结果
     * @param request 请求对象
     * @return  结果
     */
    T execute(Object... request);

    /**
     * 异步执行任务获得 Future 结果
     * @param promise 异步结果包装类
     * @param request 请求对象
     * @return  异步结果
     */
    Future<T> asyncExecute(Promise<T> promise, Object... request);

}
