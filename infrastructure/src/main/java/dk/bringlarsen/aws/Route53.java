package dk.bringlarsen.aws;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.constructs.Construct;

public class Route53 extends Construct {

    private final IHostedZone zone;

    public Route53(@NotNull Construct scope, @NotNull String id, FullyQualifiedDomainName fullyQualifiedDomainName) {
        super(scope, id);

        zone = HostedZone.fromLookup(this, id,
                HostedZoneProviderProps.builder().domainName(fullyQualifiedDomainName.domain()).build());
    }

    public IHostedZone getZone() {
        return zone;
    }
}
