package io.smallrye.context.test.contextual;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class TestContext {
    private static final ThreadLocal<TestContext> contexts = new ThreadLocal<>();

    private final String name;

    public TestContext(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TestContext getCurrent() {
        return contexts.get();
    }

    // Only for test to set up
    static void initForTest(String name) {
        contexts.set(new TestContext(name));
    }

    public <R> R supplier(Supplier<R> supplier) {
        contexts.set(this);
        try {
            return supplier.get();
        } finally {
            contexts.remove();
        }
    }

    public void runnable(Runnable runnable) {
        contexts.set(this);
        try {
            runnable.run();
        } finally {
            contexts.remove();
        }
    }

    public <T> void consumer(Consumer<T> consumer, T t) {
        contexts.set(this);
        try {
            consumer.accept(t);
        } finally {
            contexts.remove();
        }
    }

    public <R, T> R function(Function<T, R> function, T t) {
        contexts.set(this);
        try {
            return function.apply(t);
        } finally {
            contexts.remove();
        }
    }

    public <R> R callable(Callable<R> callable) throws Exception {
        contexts.set(this);
        try {
            return callable.call();
        } finally {
            contexts.remove();
        }
    }

    public <U, T, R> R bifunction(BiFunction<T, U, R> function, T t, U u) {
        contexts.set(this);
        try {
            return function.apply(t, u);
        } finally {
            contexts.remove();

        }
    }

    public <U, T> void biconsumer(BiConsumer<T, U> consumer, T t, U u) {
        contexts.set(this);
        try {
            consumer.accept(t, u);
        } finally {
            contexts.remove();
        }
    }

}
