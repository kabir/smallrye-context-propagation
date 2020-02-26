package io.smallrye.context.test.contextual;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.microprofile.context.ThreadContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ContextualThreadContextProviderTest {
    @Test
    public void testContextualThreadContextProvider() throws Exception {
        TestContext.initForTest("hello");
        ThreadContext tc = ThreadContext.builder()
                .propagated(ThreadContext.ALL_REMAINING)
                .build();

        Thread t1 = Thread.currentThread();
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<TestContext> threadedContext = new AtomicReference<>();
        Executors.newSingleThreadExecutor().submit(tc.contextualRunnable(() -> {
            Thread t2 = Thread.currentThread();
            Assert.assertNotEquals(t1, t2);
            threadedContext.set(TestContext.getCurrent());
            latch.countDown();
        }));

        latch.await(1, TimeUnit.SECONDS);
        Assert.assertNotNull(threadedContext.get());
        Assert.assertEquals("hello", threadedContext.get().getName());
    }

}
