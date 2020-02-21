package io.smallrye.context.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.microprofile.context.spi.ThreadContextController;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;

import io.smallrye.context.SmallRyeContextManager;
import io.smallrye.context.api.ContextualThreadContextProvider;

public class ActiveContextState {

    private List<ThreadContextController> activeContext;
    private final List<ContextualThreadContextProvider> contextualProviders;

    public ActiveContextState(SmallRyeContextManager context,
            List<ThreadContextSnapshot> threadContext,
            List<ContextualThreadContextProvider> contextualProviders) {
        activeContext = new ArrayList<>(threadContext.size());
        this.contextualProviders = contextualProviders;
        for (ThreadContextSnapshot threadContextSnapshot : threadContext) {
            activeContext.add(threadContextSnapshot.begin());
        }
    }

    public void endContext() {
        // restore in reverse order
        for (int i = activeContext.size() - 1; i >= 0; i--) {
            activeContext.get(i).endContext();
        }
    }

    public <R> Supplier<R> contextualize(Supplier<R> supplier) {
        return internalContextualize(supplier,
                (target, provider) -> provider.contextualize(target));
    }

    public Runnable contextualize(Runnable runnable) {
        return internalContextualize(runnable,
                (target, provider) -> provider.contextualize(target));
    }

    public <T, R> Function<T, R> contextualize(Function<T, R> function) {
        return internalContextualize(function,
                (target, provider) -> provider.contextualize(target));
    }

    public <T> Consumer<T> contextualize(Consumer<T> consumer) {
        return internalContextualize(consumer,
                (target, provider) -> provider.contextualize(target));
    }

    public <R> Callable<R> contextualize(Callable<R> callable) {
        return internalContextualize(callable,
                (target, provider) -> provider.contextualize(target));
    }

    public <T, U, R> BiFunction<T, U, R> contextualize(BiFunction<T, U, R> function) {
        return internalContextualize(function,
                (target, provider) -> provider.contextualize(function));
    }

    public <T, U> BiConsumer contextualize(BiConsumer<T, U> consumer) {
        return internalContextualize(consumer,
                (target, provider) -> provider.contextualize(target));
    }

    private <T> T internalContextualize(T target, BiFunction<T, ContextualThreadContextProvider, T> function) {
        if (contextualProviders.isEmpty()) {
            return target;
        }
        for (ContextualThreadContextProvider provider : contextualProviders) {
            target = function.apply(target, provider);
        }
        return target;
    }
}
