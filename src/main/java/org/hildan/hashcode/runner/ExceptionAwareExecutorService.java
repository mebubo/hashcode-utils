package org.hildan.hashcode.runner;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ExceptionAwareExecutorService extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAwareExecutorService.class);

    public ExceptionAwareExecutorService(int nThreads) {
        super(nThreads, nThreads, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    }

    public ExceptionAwareExecutorService(int nThreads, ThreadFactory threadFactory) {
        super(nThreads, nThreads, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException e) {
                t = e;
            } catch (ExecutionException e) {
                t = e.getCause();
            } catch (InterruptedException e) {
                logger.error("Internal error, future.get() was interrupted while future.isDone() is true", e);
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
            logger.error("Exception occurred while executing a task", t);
        }
    }
}
