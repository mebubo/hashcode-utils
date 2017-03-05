package org.hildan.hashcode.runner;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link ThreadPoolExecutor} that logs exceptions that happened during the execution of a task.
 * It supports tasks launched via {@link #execute(Runnable)} and {@link #submit(Runnable)} and all its overloads.
 */
class ExceptionLoggingExecutorService extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingExecutorService.class);

    private static final String UNCAUGHT_EXCEPTION_MSG = "Uncaught exception thrown during task execution:";

    private final UncaughtExceptionsPolicy exceptionsPolicy;

    /**
     * Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue.  At any
     * point, at most {@code nThreads} threads will be active processing tasks. If additional tasks are submitted when
     * all threads are active, they will wait in the queue until a thread is available. If any thread terminates due to
     * a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent
     * tasks. The threads in the pool will exist until it is explicitly {@link ExecutorService#shutdown shutdown}.
     *
     * @param nThreads
     *         the number of threads in the pool
     * @param exceptionsPolicy
     *         defines what to do with uncaught exceptions' stack traces
     *
     * @throws IllegalArgumentException
     *         if {@code nThreads <= 0}
     */
    ExceptionLoggingExecutorService(int nThreads, UncaughtExceptionsPolicy exceptionsPolicy) {
        // same configuration as Executors.newFixedThreadPool(int nThreads)
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.exceptionsPolicy = exceptionsPolicy;
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
            if (exceptionsPolicy.shouldLogViaSlf4J()) {
                logger.error(UNCAUGHT_EXCEPTION_MSG, t);
            }
            if (exceptionsPolicy.shouldPrintOnStdErr()) {
                System.err.println(UNCAUGHT_EXCEPTION_MSG);
                t.printStackTrace();
            }
        }
    }
}
