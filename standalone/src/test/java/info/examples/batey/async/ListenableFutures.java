package info.examples.batey.async;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import info.examples.batey.async.thirdparty.*;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Futures.*;
import static com.google.common.util.concurrent.Futures.transformAsync;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ListenableFutures {

    private final ScheduledExecutorService es = Executors.newScheduledThreadPool(10);
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
    public void chbatey_has_sports() throws Exception {
        boolean hasSportsPermission = false;

        ListenableFuture<User> chbateyFuture = users.lookupUserListenable("chbatey");

        // Make the blocking explicit
        User chbatey = chbateyFuture.get();

        ListenableFuture<Permissions> pFuture = permissions.permissionsListenable(chbatey.getUserName());

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
    public void chbatey_has_sports_callbacks_or_transforms() throws Exception {
        boolean hasSportsPermission = false;

        ListenableFuture<User> chbateyFuture = users.lookupUserListenable("chbatey");

        // Transform async takes a Future -> Function that produces a future -> Future
        ListenableFuture<Permissions> permissionsListenableFuture = transformAsync(chbateyFuture,
                input -> permissions.permissionsListenable(input.getUserName()));

        Permissions p = permissionsListenableFuture.get();
        hasSportsPermission = p.hasPermission("SPORTS");

        // Explicit blocking
        assertTrue(hasSportsPermission);
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
        Channel channel = null;
        Permissions p = null;
        ListenableFuture<User> chbatey = users.lookupUserListenable("chbatey");
        ListenableFuture<Permissions> permissionsListenableFuture = transformAsync(chbatey, user -> permissions.permissionsListenable(user.getUserName()));
        ListenableFuture<Channel> skySportsOne = channels.lookupChannelListenable("SkySportsOne");

        channel = skySportsOne.get();
        p = permissionsListenableFuture.get();

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(chbatey);
    }

     /**
     * Scenario:
     * A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions:
     * - Does this channel exist?
     * - Is chbatey a valid user?
     * - Does chbatey have the permissions to watch Sports?
     * <p>
     * Take a 2/3 of the response time.
     */
    @Test
    public void chbatey_watch_sky_sports_one_fast() throws Exception {
        Channel channel = null;
        Permissions p = null;
        ListenableFuture<User> chbatey = users.lookupUserListenable("chbatey");
        ListenableFuture<Permissions> permissionsListenableFuture = transformAsync(chbatey, user -> permissions.permissionsListenable(user.getUserName()));
        ListenableFuture<Channel> skySportsOne = channels.lookupChannelListenable("SkySportsOne");

        channel = skySportsOne.get();
        p = permissionsListenableFuture.get();

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(chbatey);
   }


    /**
     * Do all of the above but also time out if we don't get all the results back
     * within 500 milliseconds
     */
    @Test
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
        Channel channel = null;
        Permissions p = null;
        ListenableFuture<User> chbatey = users.lookupUserListenable("chbatey");
        ListenableFuture<Permissions> permissionsListenableFuture = transformAsync(chbatey, user -> permissions.permissionsListenable(user.getUserName()));
        ListenableFuture<Channel> skySportsOne = channels.lookupChannelListenable("SkySportsOne");

        ListenableFuture<?> totalOperation = allAsList(permissionsListenableFuture, skySportsOne);
        ListenableFuture<?> totalOperationWithTimeout = Futures.withTimeout(totalOperation, 500, TimeUnit.MILLISECONDS, es);

        totalOperationWithTimeout.get();

        channel = skySportsOne.get();
        p = permissionsListenableFuture.get();

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(chbatey);
    }

    // TODO
    /**
     * Executors for ListenableFutures
     */
}
