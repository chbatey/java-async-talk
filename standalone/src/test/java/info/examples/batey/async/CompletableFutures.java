package info.examples.batey.async;

import com.google.common.util.concurrent.ListenableFuture;
import info.examples.batey.async.thirdparty.*;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

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

//        CompletableFuture<Permissions> pFuture =  cUser.thena
//
//        Permissions p = pFuture.get();
//
//        hasSportsPermission = p.hasPermission("SPORTS");
//
//        assertTrue(hasSportsPermission);
    }

}
