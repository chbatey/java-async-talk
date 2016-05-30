package info.examples.batey.async.thirdparty;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Uninterruptibles;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChannelService {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private final Map<String, Channel> channels;

    public static ChannelService channelService() {
        return new ChannelService(ImmutableMap.of(
                "SkyOne", new Channel("SkyOne"),
                "SkySportsOne", new Channel("SkySportsOne")
        ));
    }

    private ChannelService(Map<String, Channel> channels) {
        this.channels = channels;
    }

    public Channel lookupChannel(String name) {
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        return channels.get(name);
    }

    public Future<Channel> lookupChannelAsync(String name) {
        return executor.submit(() -> channels.get(name));
    }
}
