package org.hildan.hashcode.utils.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code HCRunner} provides a simple way to execute a given solver on multiple inputs in separate parallel tasks.
 * Simply use {@link #run(I...)} or {@link #run(int, I...)} and you're good to go.
 * <p>
 * Note: Depending on your choice of {@link UncaughtExceptionsPolicy}, you may need to provide an SLF4J implementation
 * on your classpath to be able to see error logs.
 *
 * @param <I>
 *         the type of input that the solver handles
 */
public class HCRunner<I> {

    private static final Logger logger = LoggerFactory.getLogger(HCRunner.class);

    private final Consumer<I> solver;

    private final List<Throwable> exceptions;

    private final UncaughtExceptionsPolicy exceptionsPolicy;

    /**
     * Creates a new {@code HCRunner}.
     *
     * @param solver
     *         the solver to run on the inputs given to {@link #run(I...)} or {@link #run(int, I...)}
     * @param exceptionsPolicy
     *         defines what to do with uncaught exceptions thrown by the solver
     */
    public HCRunner(Consumer<I> solver, UncaughtExceptionsPolicy exceptionsPolicy) {
        this.solver = solver;
        this.exceptionsPolicy = exceptionsPolicy;
        this.exceptions = new ArrayList<>();
    }

    /**
     * Executes the solver given in the constructor on the given inputs, each in its own thread. This method blocks
     * until the execution on all inputs is complete.
     *
     * @param inputs
     *         the inputs to run the solver on
     */
    @SafeVarargs
    public final void run(I... inputs) {
        run(inputs.length, inputs);
    }

    /**
     * Executes the solver given in the constructor on the given inputs, each in its own task. The tasks are distributed
     * among the given number of threads. This method blocks until the execution on all inputs is complete.
     *
     * @param nThreads
     *         the number of threads to use in the pool
     * @param inputs
     *         the inputs to run the solver on
     */
    @SafeVarargs
    public final void run(int nThreads, I... inputs) {
        if (inputs.length < 1) {
            throw new IllegalArgumentException("No input passed as argument");
        }
        ExecutorService threadPool = new ExceptionLoggingExecutorService(nThreads, exceptionsPolicy);
        List<Future<?>> futures = submitInputs(inputs, threadPool);
        waitForTermination(futures);
        shutdownAndWaitForTermination(threadPool); // also waits for logging of last exceptions
        remindExceptions(inputs);
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
                // the exception was already logged in ExecutionAwareExecutorService when the task ended
                // we track it here to also log everything at the end (to avoid having to scroll up the output)
                exceptions.add(e.getCause());
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for tasks to complete", e);
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
    }

    private void shutdownAndWaitForTermination(ExecutorService threadPool) {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for thread pool to shut down", e);
            Thread.currentThread().interrupt(); // ignore/reset
        }
    }

    private void remindExceptions(I[] inputs) {
        if (exceptions.isEmpty()) {
            return;
        }
        if (exceptionsPolicy.shouldLogViaSlf4J()) {
            logExceptions(inputs);
        }
        if (exceptionsPolicy.shouldPrintOnStdErr()) {
            printExceptionsOnStdErr(inputs);
        }
    }

    private void logExceptions(I[] inputs) {
        logger.error("{} tasks terminated abruptly by throwing exceptions", exceptions.size());
        for (int i = 0; i < exceptions.size(); i++) {
            Throwable e = exceptions.get(i);
            logger.error("Reminder: this exception was thrown while running on input " + inputs[i] + ":", e);
        }
    }

    private void printExceptionsOnStdErr(I[] inputs) {
        System.err.println(exceptions.size() + " tasks terminated abruptly by throwing exceptions");
        for (int i = 0; i < exceptions.size(); i++) {
            Throwable e = exceptions.get(i);
            System.err.println("Reminder: this exception was thrown while running on input " + inputs[i] + ":");
            e.printStackTrace();
        }
    }
}
