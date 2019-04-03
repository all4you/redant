package com.redant.core.executor;

import com.redant.core.common.constants.CommonConstants;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author houyi
 */
public abstract class AbstractExecutor<T> implements Executor<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractExecutor.class);

    private java.util.concurrent.Executor eventExecutor;

    public AbstractExecutor() {
        this(null);
    }

    public AbstractExecutor(java.util.concurrent.Executor eventExecutor) {
        this.eventExecutor = eventExecutor == null ? EventExecutorHolder.eventExecutor : eventExecutor;
    }

    @Override
    public T execute(Object... request) {
        return doExecute(request);
    }

    @Override
    public Future<T> asyncExecute(Promise<T> promise, Object... request) {
        if (promise == null) {
            throw new IllegalArgumentException("promise should not be null");
        }
        // 异步执行
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    T response = doExecute(request);
                    promise.setSuccess(response);
                } catch (Exception e) {
                    promise.setFailure(e);
                }
            }
        });
        // 返回promise
        return promise;
    }

    /**
     * 执行具体的方法
     *
     * @param request 请求对象
     * @return 返回结果
     */
    public abstract T doExecute(Object... request);

    private static final class EventExecutorHolder {
        private static java.util.concurrent.Executor eventExecutor = new ThreadPoolExecutor(
                                                                            CommonConstants.EVENT_EXECUTOR_POOL_CORE_SIZE,
                                                                            CommonConstants.EVENT_EXECUTOR_POOL_MAX_SIZE,
                                                                            CommonConstants.EVENT_EXECUTOR_POOL_KEEP_ALIVE_SECONDS,
                                                                            TimeUnit.SECONDS,
                                                                            new ArrayBlockingQueue<>(CommonConstants.EVENT_EXECUTOR_POOL_BLOCKING_QUEUE_SIZE));
    }

}
