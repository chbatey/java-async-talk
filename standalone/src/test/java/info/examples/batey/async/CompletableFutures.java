package info.examples.batey.async;

import info.examples.batey.async.thirdparty.*;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CompletableFutures {

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

        CompletableFuture<User> chbateyFuture = users.lookupUserCompletable("chbatey");

        // Make the blocking explicit
        User chbatey = chbateyFuture.get();

        CompletableFuture<Permissions> pFuture = permissions.permissionsCompletable(chbatey.getUserName());

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
    public void chbatey_has_sports_callbcaks_or_transforms() throws Exception {
        boolean hasSportsPermission = false;
        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions =
                cUser.thenCompose(user -> permissions.permissionsCompletable(user.getUserName()));

        // blocks but we could have used a call back
        hasSportsPermission = cPermissions.get().hasPermission("SPORTS");

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
        User user = null;
        Channel channel = null;
        Permissions permissions = null;

        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions = cUser.thenCompose(u -> this.permissions.permissionsCompletable(u.getUserName()));
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
        User user = null;
        Channel channel = null;
        Permissions permissions = null;

        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions = cUser.thenCompose(u -> this.permissions.permissionsCompletable(u.getUserName()));
        CompletableFuture<Channel> cChannel = channels.lookupChannelCompletable("SkySportsOne");

        // todo finish


        assertNotNull(channel);
        assertTrue(permissions.hasPermission("SPORTS"));
        assertNotNull(user);

    }
}
