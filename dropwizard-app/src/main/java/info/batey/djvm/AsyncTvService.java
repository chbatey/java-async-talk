package info.batey.djvm;

import info.examples.batey.async.Result;
import info.examples.batey.async.thirdparty.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
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
    }

    @GET
    @Path("/user/{user}/{permission}")
    public void userPermission(@Suspended AsyncResponse asyncResponse,
                                  @PathParam("user") String userName,
                                  @PathParam("permission") String permission) {
    }

    @GET
    @Path("/watch-channel/{user}/{permission}/{channel}")
    public void watchChannel(@Suspended AsyncResponse asyncResponse,
                                @PathParam("user") String userName,
                                @PathParam("permission") String permission,
                                @PathParam("channel") String channel) {
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


    }
}
