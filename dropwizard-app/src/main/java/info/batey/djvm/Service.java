package info.batey.djvm;

import com.google.common.util.concurrent.Uninterruptibles;
import info.examples.batey.async.thirdparty.PermissionsService;
import info.examples.batey.async.thirdparty.User;
import info.examples.batey.async.thirdparty.UserService;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Path("/")
public class Service {

    private static final Logger LOG = LoggerFactory.getLogger(Service.class);

    private final HttpClient httpClient;
    private final UserService userService = UserService.userService();
    private final PermissionsService permissions = PermissionsService.permissionsService();

    private final ScheduledExecutorService se = Executors.newScheduledThreadPool(5);

    public Service(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

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
    @Path("funky-async-stuff")
    public void asyncGet1(@Suspended AsyncResponse asyncResponse) {
       // More business value
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
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                asyncResponse.resume("Hello World");
            }
        }.start();
    }

    @GET
    @Path("user")
    public String user(@QueryParam("user") String userName) {
        User user = userService.lookupUser(userName);
        return user.getName();
    }

    @GET
    @Path("/watch-tv")
    public void watch(@Suspended AsyncResponse asyncResponse, @QueryParam("user") String user) {
        CompletableFuture<User> cUser = userService.lookupUserCompletable(user);
        cUser.thenAcceptAsync(u -> {
            LOG.info("Now which thread??? {}", u);
            asyncResponse.resume(u.getName());
        }, se);
        LOG.info("Are we done yet??");
    }
}
