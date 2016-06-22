package info.batey.djvm;

import info.examples.batey.async.Result;
import info.examples.batey.async.thirdparty.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Path("/sync")
public class SyncTvService {

    private static final Logger LOG = LoggerFactory.getLogger(SyncTvService.class);

    private final UserService users = UserService.userService();
    private final PermissionsService permissions = PermissionsService.permissionsService();
    private final ChannelService channels = ChannelService.channelService();

    @GET
    @Path("/user/{user}")
    public String user(@PathParam("user") String userName) {
        User user = users.lookupUser(userName);
        return user.getName();
    }

    @GET
    @Path("/user/{user}/{permission}")
    public boolean userPermission(@PathParam("user") String userName,
                                  @PathParam("permission") String permission) {
        User user = users.lookupUser(userName);
        Permissions p = permissions.permissions(user.getUserId());
        return p.hasPermission(permission);
    }

    @GET
    @Path("/watch-channel/{user}/{permission}/{channel}")
    public boolean watchChannel(@PathParam("user") String userName,
                                @PathParam("permission") String permission,
                                @PathParam("channel") String channel) {
        User user = users.lookupUser(userName);
        Permissions p = permissions.permissions(user.getUserId());
        Channel c = channels.lookupChannel(channel);
        return c != null && p.hasPermission(permission);
    }

    @GET
    @Path("/watch-channel-fast/{user}/{permission}/{channel}")
    public boolean watchChannelFast(@PathParam("user") String userName,
                                    @PathParam("permission") String permission,
                                    @PathParam("channel") String channel) throws Exception {
        Future<Channel> fChannel = se.submit(() -> channels.lookupChannel(channel));
        User user = users.lookupUser(userName);
        Permissions p = permissions.permissions(user.getUserId());
        Channel c = fChannel.get();
        return c != null && p.hasPermission(permission);
    }

    @GET
    @Path("/watch-channel-timeout/{user}/{permission}/{channel}")
    public boolean watchChannelTimeout(@PathParam("user") String userName,
                                       @PathParam("permission") String permission,
                                       @PathParam("channel") String channel) throws Exception {

        Future<Result> fResult = se.submit(() -> {
            Future<Channel> fChannel = se.submit(() -> channels.lookupChannel(channel));
            User user = users.lookupUser(userName);
            Permissions p = permissions.permissions(user.getUserId());
            Channel c = fChannel.get();
            return new Result(c, p);
        });

        Result result = fResult.get(500, TimeUnit.MILLISECONDS);
        return result.getChannel() != null && result.getPermissions().hasPermission(permission);
    }
    private final ScheduledExecutorService se = Executors.newScheduledThreadPool(5);
}
