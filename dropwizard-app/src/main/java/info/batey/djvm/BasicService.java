package info.batey.djvm;

import com.google.common.util.concurrent.Uninterruptibles;
import info.examples.batey.async.thirdparty.Permissions;
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
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Path("/")
public class BasicService {

    private static final Logger LOG = LoggerFactory.getLogger(BasicService.class);

    private final HttpClient httpClient;

    public BasicService(HttpClient httpClient) {
        this.httpClient = httpClient;
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
    @Produces("text/plain")
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

}
