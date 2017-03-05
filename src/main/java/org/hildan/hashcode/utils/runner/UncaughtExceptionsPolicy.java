package org.hildan.hashcode.utils.runner;

/**
 * An {@code UncaughtExceptionsPolicy} defines what to do with the stack traces of exceptions that are thrown during
 * parallel tasks execution.
 */
public enum UncaughtExceptionsPolicy {

    /**
     * Does nothing with the stack traces.
     * <p>
     * Warning: when using this policy, the caller of the {@link HCRunner} will not be aware of uncaught exceptions
     * thrown within the solver. This policy should only be used when the solver is guaranteed not to throw any
     * exception (for instance if it wraps everything in a try/catch block)
     */
    HIDE(false, false),

    /**
     * Print uncaught exceptions' stack traces to the standard error stream.
     */
    PRINT_ON_STDERR(true, false),

    /**
     * Log exceptions via SLF4J logging API. This requires to provide an SLF4J implementation on the classpath,
     * otherwise nothing will be printed at all.
     */
    LOG_ON_SLF4J(false, true),

    /**
     * Does both {@link #PRINT_ON_STDERR} and {@link #LOG_ON_SLF4J}.
     */
    PRINT_AND_LOG(true, true);

    private final boolean printOnStdErr;

    private final boolean logViaSlf4J;

    UncaughtExceptionsPolicy(boolean printOnStdErr, boolean logViaSlf4J) {
        this.printOnStdErr = printOnStdErr;
        this.logViaSlf4J = logViaSlf4J;
    }

    public boolean shouldPrintOnStdErr() {
        return printOnStdErr;
    }

    public boolean shouldLogViaSlf4J() {
        return logViaSlf4J;
    }
}
