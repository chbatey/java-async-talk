package info.examples.batey.async.thirdparty;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private final ListeningScheduledExecutorService ls = MoreExecutors.listeningDecorator(executor);

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

    public Future<User> lookupUserAsync(String userName) {
        return executor.schedule(() -> users.get(userName), 100, TimeUnit.MILLISECONDS);
    }

    public ListenableFuture<User> lookupUserListenable(String userName) {
        return ls.schedule(() -> {
            LOG.info("Looking up user");
            return users.get(userName);
        }, 100, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<User> lookupUserCompletable(String userName) {
        CompletableFuture<User> cUser = new CompletableFuture<>();
        // How you can very easily wrap existing APIs with an API that returns
        // completable futures.
        executor.schedule(() -> {
                    LOG.info("Look up user now finished");
                    cUser.complete(users.get(userName));
                },
                100, TimeUnit.MILLISECONDS);
        return cUser;
    }
}
