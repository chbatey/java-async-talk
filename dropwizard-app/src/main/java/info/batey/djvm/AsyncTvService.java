package info.batey.djvm;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import info.examples.batey.async.Result;
import info.examples.batey.async.thirdparty.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Path("/async")
public class AsyncTvService {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncTvService.class);

    private final UserService users = UserService.userService();
    private final PermissionsService permissions = PermissionsService.permissionsService();
    private final ChannelService channels = ChannelService.channelService();

    private final ScheduledExecutorService se = Executors.newScheduledThreadPool(5);

    @GET
    @Path("/user/{user}")
    @Produces("text/plain")
    public void user(@Suspended AsyncResponse asyncResponse,
                     @PathParam("user") String userName) {
        users.lookupUserCompletable(userName).thenAccept(user -> asyncResponse.resume(user.getName()));
    }

    @GET
    @Path("/user/{user}/{permission}")
    public void userPermission(@Suspended AsyncResponse asyncResponse,
                                  @PathParam("user") String userName,
                                  @PathParam("permission") String permission) {
        users.lookupUserCompletable(userName)
                .thenCompose(user -> permissions.permissionsCompletable(user.getUserId()))
                .thenAccept(p -> asyncResponse.resume(p.hasPermission(permission)));
    }

    @GET
    @Path("/watch-channel/{user}/{permission}/{channel}")
    public void watchChannel(@Suspended AsyncResponse asyncResponse,
                                @PathParam("user") String userName,
                                @PathParam("permission") String permission,
                                @PathParam("channel") String channel) {
        CompletableFuture<Permissions> cPermission = users.lookupUserCompletable(userName)
                .thenCompose(user -> permissions.permissionsCompletable(user.getUserId()));

        CompletableFuture<Channel> cChannel = channels.lookupChannelCompletable(channel);

        CompletableFuture<Result> cResult = cPermission.thenCombine(cChannel, (p, c) -> new Result(c, p));

        cResult.thenAccept(result -> asyncResponse.resume(
                result.getChannel() != null && result.getPermissions().hasPermission(permission)
        ));
    }

    @GET
    @Path("/watch-channel-fast/{user}/{permission}/{channel}")
    public void watchChannelFast(@PathParam("user") String userName,
                                    @PathParam("permission") String permission,
                                    @PathParam("channel") String channel) {
        // no need to implement, it just happened
    }

    @GET
    @Path("/watch-channel-timeout/{user}/{permission}/{channel}")
    public void watchChannelTimeout(@Suspended AsyncResponse asyncResponse,
                                    @PathParam("user") String userName,
                                    @PathParam("permission") String permission,
                                    @PathParam("channel") String channel) {

         CompletableFuture<Permissions> cPermission = users.lookupUserCompletable(userName)
                .thenCompose(user -> permissions.permissionsCompletable(user.getUserId()));

        CompletableFuture<Channel> cChannel = channels.lookupChannelCompletable(channel);

        CompletableFuture<Result> cResult = cPermission.thenCombine(cChannel, (p, c) -> new Result(c, p));

        cResult.thenAccept(result -> asyncResponse.resume(
                result.getChannel() != null && result.getPermissions().hasPermission(permission)
        ));

        asyncResponse.setTimeout(500, TimeUnit.MILLISECONDS);
    }
}
