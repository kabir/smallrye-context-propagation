package io.smallrye.context.test.contextual;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.smallrye.context.api.ContextualThreadContextProvider;


/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class TestContextualThreadContextProvider extends ContextualThreadContextProvider<TestContext> {

    @Override
    protected TestContext readCurrent() {
        return TestContext.getCurrent();
    }


    @Override
    public <R> Supplier<R> contextualize(Supplier<R> supplier) {
        TestContext ctx = getToPropagate();
        if (ctx == null) {
            return supplier;
        }
        return () -> ctx.supplier(supplier);
    }

    @Override
    public Runnable contextualize(Runnable runnable) {
        TestContext ctx = getToPropagate();
        if (ctx == null) {
            return runnable;
        }
        return () -> ctx.runnable(runnable);
    }

    @Override
    public <T> Consumer<T> contextualize(Consumer<T> consumer) {
        TestContext ctx = getToPropagate();
        if (ctx == null) {
            return consumer;
        }
        return t -> ctx.consumer(consumer, t);
    }

    @Override
    public <R, T> Function<T, R> contextualize(Function<T, R> target) {
        TestContext ctx = getToPropagate();
        if (ctx == null) {
            return target;
        }
        return t -> getToPropagate().function(target, t);
    }

    @Override
    public <R> Callable<R> contextualize(Callable<R> target) {
        TestContext ctx = getToPropagate();
        if (ctx == null) {
            return target;
        }
        return () -> getToPropagate().callable(target);
    }

    @Override
    public <U, T, R> BiFunction<T, U, R> contextualize(BiFunction<T, U, R> function) {
        TestContext ctx = getToPropagate();
        if (ctx == null) {
            return function;
        }
        return (t, u) -> getToPropagate().bifunction(function, t, u);
    }

    @Override
    public <U, T> BiConsumer<T, U> contextualize(BiConsumer<T, U> target) {
        TestContext ctx = getToPropagate();
        if (ctx == null) {
            return target;
        }
        return (t, u) -> getToPropagate().biconsumer(target, t, u);
    }

    @Override
    public String getThreadContextType() {
        return "Test";
    }
}
