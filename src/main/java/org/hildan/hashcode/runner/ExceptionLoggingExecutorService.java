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
 * An implementation of {@link ExecutorService} which logs exceptions that happened during the execution of a task. It
 * supports tasks launched via {@link #execute(Runnable)} and {@link #submit(Runnable)} and all its overloads.
 */
class ExceptionLoggingExecutorService extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingExecutorService.class);

    private static final String UNCAUGHT_EXCEPTION_MSG = "Uncaught exception thrown during task execution:";

    private final UncaughtExceptionsPolicy exceptionsPolicy;

    ExceptionLoggingExecutorService(int nThreads, UncaughtExceptionsPolicy exceptionsPolicy) {
        super(nThreads, nThreads, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
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
