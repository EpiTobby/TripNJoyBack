package fr.tobby.tripnjoyback;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import lombok.Getter;

@Getter
public class PromStats {
    private final HTTPServer server;
    private final Gauge groupCount;
    private final Gauge userCount;
    private final Gauge profileCount;
    private final Gauge reportCount;
    private final Gauge ramUsed;

    public PromStats(final HTTPServer server)
    {
        this.server = server;
        groupCount = new Gauge.Builder().name("groupCount").help("group count").create().register();
        userCount = new Gauge.Builder().name("userCount").help("user count").create().register();
        profileCount = new Gauge.Builder().name("profileCount").help("profile count").create().register();
        reportCount = new Gauge.Builder().name("reportCount").help("report count").create().register();
        ramUsed = new Gauge.Builder().name("ram").help("Memory used").create().register();
    }
}
