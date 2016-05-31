package info.examples.batey.async;

import info.examples.batey.async.thirdparty.*;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class Synchronous {

    private UserService users = UserService.userService();
    private ChannelService channels = ChannelService.channelService();
    private PermissionsService permissions = PermissionsService.permissionsService();

    /**
     * Show how the user service works
     */
    @Test
    public void userService() {
        assertNull("I don't expect charlie to exist", users.lookupUser("charlie"));

        assertEquals(new User("Christopher Batey", "chbatey"), users.lookupUser("chbatey"));
    }

    /**
     * Show how the permissions service works
     */
    @Test
    public void permissionsService() {
        assertNull("I don't expect charlie to have any permissions", permissions.permissions("charilie"));

        assertEquals(Permissions.permissions("ENTS", "SPORTS"), permissions.permissions("chbatey"));
    }

    /**
     * Show how the ChannelService works
     */
    @Test
    public void channelService() {
        assertNull("No channel named charlie", channels.lookupChannel("charlie"));

        assertEquals(new Channel("SkySportsOne"), channels.lookupChannel("SkySportsOne"));
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
    public void chbatey_has_sports() throws Exception {
        boolean hasSportsPermission = false;

        User chbatey = users.lookupUser("chbatey");

        Permissions p = permissions.permissions(chbatey.getUserName());

        hasSportsPermission = p.hasPermission("SPORTS");

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
    public void chbatey_watch_sky_sports_one() {
        User chbatey = users.lookupUser("chbatey");

        Permissions p = permissions.permissions(chbatey.getUserName());

        Channel channel = channels.lookupChannel("SkySportsOne");

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
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Channel> channelCallable = es.submit(() -> channels.lookupChannel("SkySportsOne"));
        User chbatey = users.lookupUser("chbatey");
        Permissions p = permissions.permissions(chbatey.getUserName());
        Channel channel = channelCallable.get();

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(chbatey);
    }
}
