package info.examples.batey.async;

import info.examples.batey.async.thirdparty.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.junit.Assert.*;

public class CompletableFutures {

    private static Logger LOG = LoggerFactory.getLogger(CompletableFuture.class);
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    private UserService users = UserService.userService();
    private ChannelService channels = ChannelService.channelService();
    private PermissionsService permissions = PermissionsService.permissionsService();

    /**
     * Scenario:
     * A web request comes in asking if chbatey has the SPORTS permission
     * <p>
     * Questions:
     * - Does the user exist?
     * - Is the user allowed to watch the channel?
     */
    @Test
    public void chbatey_has_sports_blocking() throws Exception {
        boolean hasSportsPermission = false;

        CompletableFuture<User> chbateyFuture = users.lookupUserCompletable("chbatey");

        // Make the blocking explicit
        User chbatey = chbateyFuture.get();

        CompletableFuture<Permissions> pFuture = permissions.permissionsCompletable(chbatey.getUserId());

        // Explicit blocking
        Permissions p = pFuture.get();

        hasSportsPermission = p.hasPermission("SPORTS");

        assertTrue(hasSportsPermission);
    }

    /**
     * Scenario:
     * A web request comes in asking if chbatey has the SPORTS permission
     * <p>
     * Questions:
     * - Does the user exist?
     * - Is the user allowed to watch the channel?
     */
    @Test
    public void chbatey_has_sports_compose_and_block() throws Exception {
        boolean hasSportsPermission = false;
        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions =
                cUser.thenCompose(user -> permissions.permissionsCompletable(user.getUserId()));

        // blocks but we could have used a call back
        hasSportsPermission = cPermissions.get().hasPermission("SPORTS");

        assertTrue(hasSportsPermission);
    }

    @Test
    public void chbatey_has_sports_compose_no_blocking() throws Exception {
        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions =
                cUser.thenCompose(user -> permissions.permissionsCompletable(user.getUserId()));

        cPermissions.thenAccept((Permissions p) -> {
            p.hasPermission("SPORTS");
        });
    }

    /**
     * Scenario:
     * A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions:
     * - Does this channel exist?
     * - Is chbatey a valid user?
     * - Does chbatey have the permissions to watch Sports?
     */
    @Test
    public void chbatey_watch_sky_sports_one() throws Exception {
        User user = null;
        Channel channel = null;
        Permissions permissions = null;

        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions = cUser.thenCompose(u -> this.permissions.permissionsCompletable(u.getUserId()));
        CompletableFuture<Channel> cChannel = channels.lookupChannelCompletable("SkySportsOne");

        channel = cChannel.get();
        permissions = cPermissions.get();
        user = cUser.get(); // will definitely be done as permissions is done

        assertNotNull(channel);
        assertTrue(permissions.hasPermission("SPORTS"));
        assertNotNull(user);
    }


    /**
     * Do all of the above but also time out if we don't get all the results back
     * within 500 milliseconds
     */
    @Test
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
        Result result = null;

        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions = cUser.thenCompose(u -> permissions.permissionsCompletable(u.getUserId()));
        CompletableFuture<Channel> cChannel = channels.lookupChannelCompletable("SkySportsOne");

        CompletableFuture<Result> cResult = cPermissions.thenCombine(cChannel, (p, c) -> new Result(c , p));
        CompletableFuture<Result> cTimeout = timeout(500);
        CompletableFuture<Result> cResultWithTimeout = cResult.applyToEither(cTimeout, Function.identity());

        blockUntilComplete(cResultWithTimeout);

        assertFalse(cResultWithTimeout.isCompletedExceptionally());
        result = cResultWithTimeout.get();
        assertNotNull(result.channel);
        assertTrue(result.permissions.hasPermission("SPORTS"));
    }

    private void blockUntilComplete(CompletableFuture<?> cf) {
        try {
            cf.get();
        } catch (Exception e) {
            LOG.warn("Future failed", e);
        }
    }

    private CompletableFuture<Result> timeout(int millis) {
        CompletableFuture<Result> cf = new CompletableFuture<>();
        ses.schedule(() -> cf.completeExceptionally(new TimeoutException("OMG we timed out")), millis, TimeUnit.MILLISECONDS);
        return cf;
    }

    public static class Result {
        private Channel channel;
        private Permissions permissions;

        public Result(Channel channel, Permissions permissions) {
            this.channel = channel;
            this.permissions = permissions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Objects.equals(channel, result.channel) &&
                    Objects.equals(permissions, result.permissions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(channel, permissions);
        }
    }
}
