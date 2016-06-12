package info.batey.djvm;

import com.google.common.util.concurrent.*;
import info.examples.batey.async.thirdparty.Permissions;
import info.examples.batey.async.thirdparty.PermissionsService;
import info.examples.batey.async.thirdparty.User;
import info.examples.batey.async.thirdparty.UserService;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Futures.transformAsync;

@Path("/")
public class Service {

    private static final Logger LOG = LoggerFactory.getLogger(Service.class);

    private final HttpClient httpClient;
    private final UserService users = UserService.userService();
    private final PermissionsService permissions = PermissionsService.permissionsService();

    private final ScheduledExecutorService se = Executors.newScheduledThreadPool(5);

    public Service(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // Synchronous
    @GET
    @Path("/user")
    public boolean userPermission(String userName, String permission) {
        User chbatey = users.lookupUser(userName);
        Permissions p = permissions.permissions(chbatey.getUserId());
        return p.hasPermission(permission);
    }

    // Asynchronous
    @GET
    @Path("funky-async-stuff")
    public void asyncGet1(@Suspended AsyncResponse asyncResponse) {
        // More business value
        // We need to do this on a different thread
        // Without blocking another thread :-/
        asyncResponse.resume("Hello World");
    }

    @GET
    @Path("funky-async-stuff")
    public void asyncGet2(@Suspended AsyncResponse asyncResponse) {
        LOG.info("Which thread?? Async");
        new Thread() {
            @Override
            public void run() {
                LOG.info("Which thread long running io call?");
                // This counts as blocking!
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                asyncResponse.resume("Hello World");
            }
        }.start();
    }

    @GET
    @Path("/user-async")
    public void userAsync(@Suspended AsyncResponse asyncResponse, String userName, String permission) {
        ListenableFuture<User> lUser = users.lookupUserListenable(userName);

        ListenableFuture<Permissions> lPermissions = transformAsync(lUser, user -> permissions.permissionsListenable(user.getUserId()));

        Futures.addCallback(lPermissions, new FutureCallback<Permissions>() {
            @Override
            public void onSuccess(@Nullable Permissions result) {
                asyncResponse.resume(result.hasPermission(permission));
            }

            @Override
            public void onFailure(Throwable t) {
                LOG.warn("Oh noes", t);
            }
        });
    }

    @Path("/user-async-cf")
    public void userAsyncCf(@Suspended AsyncResponse asyncResponse, String userName, String permission) {
        CompletableFuture<User> cUser = users.lookupUserCompletable("chbatey");
        CompletableFuture<Permissions> cPermissions =
                cUser.thenCompose(user -> permissions.permissionsCompletable(user.getUserId()));

        cPermissions.thenAccept((Permissions p) -> {
            asyncResponse.resume(p.hasPermission("SPORTS"));
        });
    }

    // Other stuff
    @GET
    @Path("/callout")
    public String callout() throws IOException {
        HttpGet httpGet = new HttpGet("http://wiremock:7070/name");
        return EntityUtils.toString(httpClient.execute(httpGet).getEntity());
    }


    @GET
    @Path("/funky-business-stuff")
    public String funky() {
        LOG.info("Which thread??");
        // Do something of great business value
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        return "Hello World";
    }



    @GET
    @Path("user")
    public String user(@QueryParam("user") String userName) {
        User user = users.lookupUser(userName);
        return user.getName();
    }

    @GET
    @Path("/watch-tv")
    public void watch(@Suspended AsyncResponse asyncResponse, @QueryParam("user") String user) {
        CompletableFuture<User> cUser = users.lookupUserCompletable(user);
        cUser.thenAcceptAsync(u -> {
            LOG.info("Now which thread??? {}", u);
            asyncResponse.resume(u.getName());
        }, se);
        LOG.info("Are we done yet??");
    }
}
