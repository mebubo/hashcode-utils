package org.hildan.hashcode.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HCRunner<I> {

    private static final Logger logger = LoggerFactory.getLogger(HCRunner.class);

    private static final int DEFAULT_TIMEOUT = 30;

    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.HOURS;

    private final Consumer<I> solver;

    private final int timeout;

    private final TimeUnit timeoutUnit;

    private final List<Throwable> exceptions;

    public HCRunner(Consumer<I> solver) {
        this(solver, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT);
    }

    public HCRunner(Consumer<I> solver, int timeout, TimeUnit timeoutUnit) {
        this.solver = solver;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.exceptions = new ArrayList<>();
    }

    @SafeVarargs
    public final void run(I... inputs) {
        run(inputs.length, inputs);
    }

    @SafeVarargs
    public final void run(int nThreads, I... inputs) {
        if (inputs.length < 1) {
            throw new IllegalArgumentException("No input passed as argument");
        }
//        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        ExecutorService threadPool = new ExceptionAwareExecutorService(nThreads);
        // ThreadFactory threadFactory = new SimpleThreadFactory(this::onUncaughtException);
        // ExecutorService threadPool = Executors.newFixedThreadPool(nThreads, threadFactory);
        // ExecutorService threadPool = new ExceptionAwareExecutorService(nThreads, threadFactory);
        // launchSolvers(inputs, threadPool);
        List<Future<?>> futures = submitInputs(inputs, threadPool);
        waitForTermination(futures);
        //        awaitWorkerThreadsTermination(timeout, timeoutUnit, threadPool);
        logExceptions();
        threadPool.shutdown();
    }

    private synchronized void logExceptions() {
        if (exceptions.isEmpty()) {
            System.out.println("No exceptions to log");
            return;
        }
        System.out.println(exceptions.size() + " exceptions occurred while running tasks");
        logger.error("{} exceptions occurred while running tasks", exceptions.size());
    }

    private synchronized void onUncaughtException(Thread thread, Throwable throwable) {
        logger.error("Exception during task execution in thread " + thread.getName(), throwable);
        exceptions.add(throwable);
    }

    private void launchSolvers(I[] inputs, ExecutorService threadPool) {
        for (I input : inputs) {
            logger.info("Queueing resolution of input " + input);
            threadPool.submit(() -> solver.accept(input));
        }
    }

    private List<Future<?>> submitInputs(I[] inputs, ExecutorService threadPool) {
        List<Future<?>> handles = new ArrayList<>(inputs.length);
        for (I input : inputs) {
            Future<?> future = threadPool.submit(() -> solver.accept(input));
            handles.add(future);
        }
        return handles;
    }

    private void waitForTermination(List<Future<?>> tasks) {
        for (Future<?> task : tasks) {
            try {
                task.get();
            } catch (ExecutionException e) {
                exceptions.add(e.getCause());
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for tasks to complete", e);
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
    }

    private static void awaitWorkerThreadsTermination(int timeout, TimeUnit timeUnit, ExecutorService threadPool) {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for tasks to complete", e);
        }
    }

    public static void main(String[] args) {
        HCRunner<String> runner = new HCRunner<>(input -> {
            int time = Integer.parseInt(input.substring(0, 1));
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean shouldThrow = input.endsWith("E");
            if (shouldThrow) {
                throw new RuntimeException("test");
            }
        });
        runner.run("1E", "2E", "3");
    }
}
