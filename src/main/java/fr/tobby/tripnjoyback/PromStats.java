package fr.tobby.tripnjoyback;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import lombok.Getter;

@Getter
public class PromStats {
    private final HTTPServer server;
    private final Gauge groupCount;
    private final Gauge ramUsed;

    public PromStats(final HTTPServer server)
    {
        this.server = server;
        groupCount = new Gauge.Builder().name("groupCount").help("yo").create().register();
        ramUsed = new Gauge.Builder().name("ram").help("Memory used").create().register();
    }
}
