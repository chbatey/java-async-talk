package info.examples.batey.async;

import info.examples.batey.async.thirdparty.Channel;
import info.examples.batey.async.thirdparty.Permissions;
import info.examples.batey.async.thirdparty.User;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Observables {
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


        assertNotNull(channel);
        assertTrue(permissions.hasPermission("SPORTS"));
        assertNotNull(user);

    }
}
