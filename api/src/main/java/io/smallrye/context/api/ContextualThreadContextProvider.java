package io.smallrye.context.api;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.microprofile.context.spi.ThreadContextController;
import org.eclipse.microprofile.context.spi.ThreadContextProvider;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public abstract class ContextualThreadContextProvider<T> implements ThreadContextProvider {
    private static final ThreadLocal<Object> SNAPSHOTS = new ThreadLocal<>();

    public ThreadContextSnapshot currentContext(Map<String, String> props) {
        return noopContext(props, readCurrent());
    }

    public ThreadContextSnapshot clearedContext(Map<String, String> props) {
        SNAPSHOTS.set(null);
        return noopContext(props, null);
    }

    private ThreadContextSnapshot noopContext(Map<String, String> props, T t) {
        return new ThreadContextSnapshot() {
            @Override
            public ThreadContextController begin() {
                SNAPSHOTS.set(t);
                return new ThreadContextController() {
                    @Override
                    public void endContext() throws IllegalStateException {
                        SNAPSHOTS.remove();
                    }
                };
            }
        };
    }

    protected abstract T readCurrent();

    protected T getToPropagate() {
        return (T) SNAPSHOTS.get();
    }

    public abstract <R> Supplier<R> contextualize(Supplier<R> supplier);

    public abstract Runnable contextualize(Runnable runnable);

    public abstract <T> Consumer<T> contextualize(Consumer<T> target);

    public abstract <R, T> Function<T, R> contextualize(Function<T, R> target);

    public abstract <R> Callable<R> contextualize(Callable<R> target);

    public abstract <U, T, R> BiFunction<T, U, R> contextualize(BiFunction<T, U, R> function);

    public abstract <U, T> BiConsumer<T, U> contextualize(BiConsumer<T, U> target);
}
