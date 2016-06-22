package info.examples.batey.async;

import info.examples.batey.async.thirdparty.*;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class Synchronous {

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
     * Show how the user service works
     */
    @Test
    public void userService() {
        assertNull("I don't expect charlie to exist", users.lookupUser("charlie"));

        assertEquals(new User("Christopher Batey", "chbatey", 1), users.lookupUser("chbatey"));
    }

    /**
     * Show how the permissions service works
     */
    @Test
    public void permissionsService() {
        assertNull("I don't expect charlie to have any permissions", permissions.permissions(3));

        assertEquals(Permissions.permissions("ENTS", "SPORTS"), permissions.permissions(1));
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
        user = users.lookupUser("chbatey");
        userPermissions = permissions.permissions(user.getUserId());

        assertTrue(userPermissions.hasPermission("SPORTS"));
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
        user = users.lookupUser("chbatey");             // ~500ms
        userPermissions = permissions.permissions(user.getUserId());  // ~500ms
        channel = channels.lookupChannel("SkySportsOne");  // ~500ms

        assertNotNull(channel);
        assertTrue(userPermissions.hasPermission("SPORTS"));
        assertNotNull(user);
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
    @Test(timeout = 1200)
    public void chbatey_watch_sky_sports_one_fast() throws Exception {

        assertNotNull(channel);
        assertTrue(userPermissions.hasPermission("SPORTS"));
        assertNotNull(user);
    }

    /**
     * Do all of the above but also time out if we don't get all the results back
     * within 1200 milliseconds
     */
    @Test(timeout = 1200)
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
       
        assertNotNull(result.channel);
        assertTrue(result.permissions.hasPermission("SPORTS"));
    }
}
