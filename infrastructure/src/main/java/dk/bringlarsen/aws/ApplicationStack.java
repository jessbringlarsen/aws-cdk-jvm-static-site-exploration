package dk.bringlarsen.aws;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class ApplicationStack extends Stack {
    public ApplicationStack(final Construct scope, final String id, final StackProps props, FullyQualifiedDomainName domain) {
        super(scope, id, props);

        Route53 hostedZone = new Route53(this, "applicationzostedzone", domain);
        CertificateManager certificateManager = new CertificateManager(this, "applicationcertificate", domain, hostedZone.getZone());

        new S3Bucket(this, "webbucket", domain, hostedZone, certificateManager);
    }
}
