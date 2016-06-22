package info.examples.batey.async;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import info.examples.batey.async.thirdparty.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

import static com.google.common.util.concurrent.Futures.transformAsync;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ListenableFutures {

    private static final Logger LOG = LoggerFactory.getLogger(ListenableFutures.class);
    private final ScheduledExecutorService es = Executors.newScheduledThreadPool(10);
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
        ListenableFuture<User> lUser = users.lookupUserListenable("chbatey");

        // Make the blocking explicit
        user = lUser.get();

        ListenableFuture<Permissions> lPermissions = permissions.permissionsListenable(user.getUserId());

        // Explicit blocking
        userPermissions = lPermissions.get();

        assertTrue(userPermissions.hasPermission("SPORTS"));
    }

    /**
     * Same scenario, try it without the blocking calls.
     */
    @Test
    public void chbatey_has_sports_callbacks() throws Exception {
        ListenableFuture<User> lUser = users.lookupUserListenable("chbatey");
        Futures.addCallback(lUser, new FutureCallback<User>() {
            @Override
            public void onSuccess(User result) {
                ListenableFuture<Permissions> lPermissions = permissions.permissionsListenable(result.getUserId());

                Futures.addCallback(lPermissions, new FutureCallback<Permissions>() {
                    @Override
                    public void onSuccess(Permissions result) {
                        // We can do it!
                        result.hasPermission("SPORTS");
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
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
    public void chbatey_has_sports_transform_and_block() throws Exception {

        ListenableFuture<User> lUser = users.lookupUserListenable("chbatey");

        // Transform async takes a Future -> Function that produces a future -> Future
        ListenableFuture<Permissions> permissionsListenableFuture = transformAsync(lUser,
                input -> permissions.permissionsListenable(input.getUserId()));

        userPermissions = permissionsListenableFuture.get();

        // Explicit blocking
        assertTrue(userPermissions.hasPermission("SPORTS"));
    }

    @Test
    public void chbatey_has_sports_transform_no_blocking() throws Exception {
        ListenableFuture<User> lUser = users.lookupUserListenable("chbatey");

        // Transform async takes a Future -> Function that produces a future -> Future
        ListenableFuture<Permissions> lPermissions = transformAsync(lUser,
                input -> permissions.permissionsListenable(input.getUserId()));


        Futures.addCallback(lPermissions, new FutureCallback<Permissions>() {
            @Override
            public void onSuccess(Permissions result) {
                // call resume

            }

            @Override
            public void onFailure(Throwable t) {

            }
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
        ListenableFuture<User> lUser = users.lookupUserListenable("chbatey");
        ListenableFuture<Permissions> permissionsListenableFuture = transformAsync(lUser, user -> permissions.permissionsListenable(user.getUserId()));
        ListenableFuture<Channel> skySportsOne = channels.lookupChannelListenable("SkySportsOne");

        channel = skySportsOne.get();
        userPermissions = permissionsListenableFuture.get();

        assertNotNull(channel);
        assertTrue(userPermissions.hasPermission("SPORTS"));
        assertNotNull(lUser);
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
        ListenableFuture<Channel> lChannel = channels.lookupChannelListenable("SkySportsOne");
        ListenableFuture<User> lUser = users.lookupUserListenable("chbatey");
        ListenableFuture<Permissions> lPermissions = transformAsync(lUser, user -> permissions.permissionsListenable(user.getUserId()));

        channel = lChannel.get();
        userPermissions = lPermissions.get();

        assertNotNull(channel);
        assertTrue(userPermissions.hasPermission("SPORTS"));
        assertNotNull(lUser);
    }


    /**
     * Do all of the above but also time out if we don't get all the results back
     * within 500 milliseconds
     */
    @Test(expected = ExecutionException.class)
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
        ListenableFuture<User> lUser = users.lookupUserListenable("chbatey");
        ListenableFuture<Permissions> lPermissions = transformAsync(lUser, user -> permissions.permissionsListenable(user.getUserId()));
        ListenableFuture<Channel> lChannel = channels.lookupChannelListenable("SkySportsOne");

        ListenableFuture<List<Object>> totalOperation = Futures.allAsList(lChannel, lPermissions);
        ListenableFuture<List<Object>> totalOperationWithTimeout = Futures.withTimeout(totalOperation, 500, TimeUnit.MILLISECONDS, es);
        ListenableFuture<Result> lResult = Futures.transform(totalOperationWithTimeout, this::fromList);

        blockUntilComplete(lResult);

        assertTrue(lResult.isDone());
        result = lResult.get();
        assertNotNull(result.channel);
        assertTrue(result.permissions.hasPermission("SPORTS"));
    }

    private Result fromList(List<Object> list) {
        return new Result((Channel) list.get(0), (Permissions) list.get(1));
    }


    private void blockUntilComplete(ListenableFuture<?> future) {
        try {
            future.get();
        } catch (Exception e) {
            LOG.warn("Future failed", e);
        }
    }

    public ListenableFuture<Result> combine(ListenableFuture<Channel> futureA,
                                            final ListenableFuture<Permissions> futureB) {
        return Futures.transformAsync(futureA, a ->
                Futures.transform(futureB, (Function<Permissions, Result>) b ->
                        new Result(a, b)));
    }
}
