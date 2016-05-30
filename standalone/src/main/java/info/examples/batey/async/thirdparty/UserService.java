package info.examples.batey.async.thirdparty;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Uninterruptibles;

import java.util.Map;
import java.util.concurrent.*;

public class UserService {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    public static UserService userService() {
        return new UserService(ImmutableMap.of(
                "chbatey", new User("Christopher Batey", "chbatey"),
                "trevor", new User("Trevor Sinclair", "trevor")
        ));
    }

    private final Map<String, User> users;

    private UserService(Map<String, User> users) {
        this.users = users;
    }

    public User lookupUser(String userName) {
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        return users.get(userName);
    }

    public Future<User> lookupUserAsync(final String userName) {
        return executor.schedule(() -> users.get(userName), 100, TimeUnit.MILLISECONDS);
    }
}
