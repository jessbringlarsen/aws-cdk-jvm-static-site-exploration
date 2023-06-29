package dk.bringlarsen.aws;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class Application {
    public static void main(final String[] args) {
        App app = new App();

        FullyQualifiedDomainName domain = new FullyQualifiedDomainName((String) app.getNode().tryGetContext("subdomain"), (String) app.getNode().tryGetContext("domain"));

        StackProps props = StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region("us-east-1").build())
                .build();

        new ApplicationStack(app, "jla-cdk-exploration", props, domain);
        app.synth();
    }
}

