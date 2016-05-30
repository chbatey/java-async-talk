package info.examples.batey.async.thirdparty;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Uninterruptibles;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PermissionsService {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

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

    public Permissions permissions(String userName) {
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        return permissions.get(userName);
    }

    public Future<Permissions> permissionsAsync(String userName) {
        return executor.schedule(() -> permissions.get(userName), 100, TimeUnit.MILLISECONDS);
    }
}
