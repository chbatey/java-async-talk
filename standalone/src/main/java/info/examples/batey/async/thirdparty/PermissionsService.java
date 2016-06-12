package info.examples.batey.async.thirdparty;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class PermissionsService {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionsService.class);

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private final ListeningScheduledExecutorService ls = MoreExecutors.listeningDecorator(executor);

    public static PermissionsService permissionsService() {
        Map<String, Permissions> of = ImmutableMap.of(
                "chbatey", Permissions.permissions("SPORTS", "ENTS"),
                "trevor", Permissions.permissions()
        );
        return new PermissionsService(of);
    }

    private final Map<String, Permissions> permissions;

    private PermissionsService(Map<String, Permissions> permissions) {
        this.permissions = permissions;
    }

    public Permissions permissions(String userId) {
        Uninterruptibles.sleepUninterruptibly(Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
        return permissions.get(userId);
    }

    public Future<Permissions> permissionsAsync(String userId) {
        return executor.schedule(() -> permissions.get(userId), Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
    }

    public ListenableFuture<Permissions> permissionsListenable(String userId) {
        return ls.schedule(() -> {
            LOG.info("Getting permissions");
            return permissions.get(userId);
        }, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Permissions> permissionsCompletable(String userId) {
        CompletableFuture<Permissions> result = new CompletableFuture<>();
        executor.schedule(() -> {
            LOG.info("Permissions look up complete");
            result.complete(permissions.get(userId));
        }, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
        return result;
    }
}
