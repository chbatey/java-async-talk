package info.examples.batey.async.thirdparty;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class PermissionsService {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionsService.class);

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("permissions-service-%d").build());
    private final ListeningScheduledExecutorService ls = MoreExecutors.listeningDecorator(executor);

    public static PermissionsService permissionsService() {
        Map<Integer, Permissions> of = ImmutableMap.of(
                1, Permissions.permissions("SPORTS", "ENTS"),
                2, Permissions.permissions()
        );
        return new PermissionsService(of);
    }

    private final Map<Integer, Permissions> permissions;

    private PermissionsService(Map<Integer, Permissions> permissions) {
        this.permissions = permissions;
    }

    public Permissions permissions(int userId) {
        Uninterruptibles.sleepUninterruptibly(Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
        LOG.info("Permission lookup complete");
        return permissions.get(userId);
    }

    public Future<Permissions> permissionsAsync(int userId) {
        return executor.schedule(() -> {
            LOG.info("Permission lookup complete");
            return permissions.get(userId);
        }, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
    }

    public ListenableFuture<Permissions> permissionsListenable(int userId) {
        return ls.schedule(() -> {
            LOG.info("Permission lookup complete");
            return permissions.get(userId);
        }, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Permissions> permissionsCompletable(int userId) {
        CompletableFuture<Permissions> result = new CompletableFuture<>();
        executor.schedule(() -> {
            LOG.info("Permissions look up complete");
            result.complete(permissions.get(userId));
        }, Config.PERMISSION_DELAY, TimeUnit.MILLISECONDS);
        return result;
    }
}
