package info.examples.batey.async;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import info.examples.batey.async.thirdparty.*;
import org.junit.Test;

import static com.google.common.util.concurrent.Futures.transformAsync;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ListenableFutures {
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
     *
     *
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


    // TODO
    /**
     * Executors for ListenableFutures
     */
}
