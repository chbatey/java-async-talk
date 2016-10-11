package info.examples.batey.async;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * A set of tests that show the behaviour of the CompletableFuture
 *
 * Each computation is either a function, consumer and runnable.
 * ---------------------
 *
 * *Apply* Function (returns a CF<T> where T is the return type of F.
 *
 * *Compose* Function that returns a CompletableFuture, think bind, flatmap
 *
 * *Accept* Consumer (doesn't return anything)
 *
 * *Run* Runnable
 *
 * Other method name
 * -------------------------
 *
 * *Async* Executed on a different thread, default pool by default or the passed in executor.
 *
 * *Combine* Takes many CFs and turns them into one
 *
 * *Both* Makes a normal method like thenAccept but takes two CFs and executes when they both complete
 *
 * *Either* Like the normal method but executed when either of the CFs complete
 *
 * Examples
 * ----------
 *
 * thenApply, takes a function and applies it to a CF result, returning the result
 * thenAccept, takes a consumer and applies it to a CF result, has to be side effecting
 */
public class CompletableFutures101 {

    private static Logger LOG = LoggerFactory.getLogger(CompletableFutures101.class);

    /**
     * What's the difference between get and join?
     */
    @Test
    public void createCompleted() throws Exception {
        CompletableFuture<String> c = CompletableFuture.completedFuture("Hello World");

        assertFalse(c.isCancelled());
        assertTrue(c.isDone());
        assertFalse(c.isCompletedExceptionally());
        assertEquals("Hello World", c.get());
        assertEquals("Hello World", c.join());
        assertEquals("Hello World", c.getNow("Chris"));
    }

    /**
     * thenApply aka map aka fmap aka <*>
     */
    @Test
    public void showThenApply() throws Exception {
        CompletableFuture<String> chris = CompletableFuture.completedFuture("Chris");
        CompletableFuture<String> helloChris = chris.thenApply(this::name);

        assertTrue(helloChris.isDone());
        assertEquals("Hello Chris", helloChris.join());
    }

    /**
     * The cat got in to a box in a box :(
     */
    @Test
    public void showThenApplyGoneWrong() throws Exception {
        CompletableFuture<String> chris = CompletableFuture.completedFuture("Chris");
        CompletableFuture<CompletableFuture<String>> helloChris = chris.thenApply(this::nameAsync);

        assertTrue(helloChris.isDone());
        // join().join() :(
        assertEquals("Hello Chris", helloChris.join().join());
    }

    /**
     * thenCompose aka flatMap aka >>= aka bind
     */
    @Test
    public void showThenCompose() throws Exception {
        CompletableFuture<String> chris = CompletableFuture.completedFuture("Chris");
        CompletableFuture<String> helloChris = chris.thenCompose(this::nameAsync);

        assertTrue(helloChris.isDone());
        assertEquals("Hello Chris", helloChris.join());
    }

    /**
     * thenAccept aka foreach
     */
    @Test
    public void showThenAccept() throws Exception {
        CompletableFuture<String> chris = CompletableFuture.completedFuture("Chris");
        // side effecting :(
        CompletableFuture<Void> helloChris = chris.thenAccept(c -> System.out.println("Hello " + c));

        assertTrue(helloChris.isDone());
    }

    /**
     * thenCombine aka
     *
     * What is the difference between this and thenApply?
     */
    @Test
    public void howThenCombine() throws Exception {
        CompletableFuture<String> chris = CompletableFuture.completedFuture("Chris");
        CompletableFuture<String> hello = CompletableFuture.completedFuture("Hello");

        CompletableFuture<String> helloChris = hello.thenCombine(chris, (h, c) -> h + " " + c);

        assertTrue(helloChris.isDone());
        assertEquals("Hello Chris", helloChris.join());
    }

    /**
     * thenRun is like a callback where you don't get the value
     */
    @Test
    public void showThenRun() throws Exception {
        CompletableFuture<String> chris = CompletableFuture.completedFuture("Chris");
        CompletableFuture<String> hello = CompletableFuture.completedFuture("Hello");
        CompletableFuture<String> helloChris = hello.thenCombine(chris, (h, c) -> h + " " + c);

        helloChris.thenRun(() -> System.out.println("We're ready to say hello to Chris"));

        assertTrue(helloChris.isDone());
        assertEquals("Hello Chris", helloChris.join());
    }


    /**
     * Run Async. Runs a Runnable and completes when the Runnable finishes.
     */
    @Test
    public void showRunAsync() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> System.out.println("Hello from the future"));

        Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

        assertTrue(future.isDone());
    }

    /**
     * Supply Async. Runs the supplier on the common pool and completes when it finishes.
     */
    @Test
    public void showSupplyAsync() throws Exception {
        CompletableFuture<String> chris = CompletableFuture.supplyAsync(() -> "Chris");
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> "Hello");

        CompletableFuture<String> helloChris = chris.thenCombine(hello, (c, h) -> h + " " + c);

        Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

        assertTrue(helloChris.isDone());
        assertEquals("Hello Chris", helloChris.get());
    }

    /**
     * Dealing with Exceptions
     */
    @Test
    public void exceptions() throws Exception {
        CompletableFuture<String> chris = failed(new RuntimeException("Oh dear"));

        chris.thenAccept(name -> {
            LOG.info("I probably won't be called ");
        });

        chris.thenRun(() -> LOG.info("Will i run? No chance"));

        chris.whenComplete((result, ex) -> {
            LOG.info("Aha I finally got something {} {}", result);
        });

        CompletableFuture<String> safeChris = chris.exceptionally(ex -> "Trevor");

        assertEquals("Trevor", safeChris.get());
        assertTrue(safeChris.isDone());
    }

    private CompletableFuture<String> failed(Throwable e) {
        CompletableFuture<String> result = new CompletableFuture<>();
        result.completeExceptionally(e);
        return result;
    }

    private CompletableFuture<String> nameAsync(String name) {
        return CompletableFuture.completedFuture("Hello " + name);
    }

    private String name(String name) {
        return "Hello " + name;
    }

}
