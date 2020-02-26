package io.smallrye.context.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.context.spi.ThreadContextProvider;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;

import io.smallrye.context.SmallRyeContextManager;
import io.smallrye.context.api.ContextualThreadContextProvider;

public class CapturedContextState {

    private final List<ThreadContextSnapshot> threadContext = new LinkedList<>();
    private final List<ContextualThreadContextProvider> contextualContexts = new LinkedList<>();
    private final SmallRyeContextManager context;

    public CapturedContextState(SmallRyeContextManager context, ThreadContextProviderPlan plan,
            Map<ThreadContextProvider, ContextualThreadContextProvider> contextualProviders,
            Map<String, String> props) {
        this.context = context;

        for (ThreadContextProvider provider : plan.propagatedProviders) {
            ThreadContextSnapshot snapshot = provider.currentContext(props);
            if (snapshot != null) {
                threadContext.add(snapshot);
            }
            ContextualThreadContextProvider contextualProvider = contextualProviders.get(provider);
            if (contextualProvider != null) {
                this.contextualContexts.add(contextualProvider);
            }
        }
        for (ThreadContextProvider provider : plan.clearedProviders) {
            ThreadContextSnapshot snapshot = provider.clearedContext(props);
            if (snapshot != null) {
                threadContext.add(snapshot);
            }
            ContextualThreadContextProvider contextualProvider = contextualProviders.get(provider);
            if (contextualProvider != null) {
                this.contextualContexts.add(contextualProvider);
            }
        }
    }

    public ActiveContextState begin() {
        return new ActiveContextState(context, threadContext, contextualContexts);
    }
}
