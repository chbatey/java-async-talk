package info.examples.batey.async;

import info.examples.batey.async.thirdparty.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class CompletableFutures {

    private static Logger LOG = LoggerFactory.getLogger(CompletableFuture.class);

    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    private UserService users = UserService.userService();
    private ChannelService channels = ChannelService.channelService();
    private PermissionsService permissions = PermissionsService.permissionsService();

    private Channel channel;
    private User user;
    private Permissions userPermissions;
    private Result result;

    @Before
    public void setup() {
        channel = null;
        user = null;
        userPermissions = null;
        result = null;
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
    public void chbatey_has_sports_blocking() throws Exception {
        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");

        // Make the blocking explicit
        user = cUser.get();

        CompletableFuture<Permissions> pFuture = permissions.permissionsCompletable(user.getUserId());

        // Explicit blocking
        userPermissions = pFuture.get();

        assertTrue(userPermissions.hasPermission("SPORTS"));
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
        // Skip
        assertTrue(userPermissions.hasPermission("SPORTS"));
    }

    @Test
    public void chbatey_has_sports_compose_no_blocking() throws Exception {
        // Skip
    }

    /**
     * Scenario:
     * A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions:
     * - Does this channel exist?
     * - Is chbatey a valid user?
     * - Does chbatey have the permissions to watch Sports?
     *
     * How do we transform like with the ListenableFuture?
     *
     * How do we add a final callback?
     */
    @Test(timeout = 1200)
    public void chbatey_watch_sky_sports_one() throws Exception {

        assertNotNull(channel);
        assertTrue(userPermissions.hasPermission("SPORTS"));
        assertNotNull(user);
    }


    /**
     * Do all of the above but also time out if we don't get all the results back
     * within 500 milliseconds
     *
     * applyToEither
     */
    @Test
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
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
}
