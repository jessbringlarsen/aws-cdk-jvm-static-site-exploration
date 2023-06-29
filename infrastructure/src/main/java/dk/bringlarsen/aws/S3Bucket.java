package dk.bringlarsen.aws;


import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.cloudfront.BehaviorOptions;
import software.amazon.awscdk.services.cloudfront.Distribution;
import software.amazon.awscdk.services.cloudfront.DistributionProps;
import software.amazon.awscdk.services.cloudfront.ViewerProtocolPolicy;
import software.amazon.awscdk.services.cloudfront.origins.S3Origin;
import software.amazon.awscdk.services.route53.*;
import software.amazon.awscdk.services.route53.targets.CloudFrontTarget;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.BucketProps;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.BucketDeploymentProps;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.constructs.Construct;

import java.util.UUID;

import static java.lang.String.join;
import static java.util.Collections.singletonList;

public class S3Bucket extends Construct {

    public S3Bucket(@NotNull Construct parent, @NotNull String id, FullyQualifiedDomainName fullyQualifiedDomainName, Route53 route53, CertificateManager certificateManager) {
        super(parent, id);

        Bucket bucket = new Bucket(this, id, BucketProps.builder()
                .publicReadAccess(true)
                .autoDeleteObjects(true)
                .websiteIndexDocument("index.html")
                .websiteErrorDocument("index.html")
                .removalPolicy(RemovalPolicy.DESTROY)
                .bucketName("s3bucket-" + UUID.randomUUID())
                .blockPublicAccess(BlockPublicAccess.BLOCK_ACLS)
                .accessControl(BucketAccessControl.BUCKET_OWNER_FULL_CONTROL)
                .build());

        Distribution distribution = new Distribution(this, "Frontend-Distribution", DistributionProps.builder()
                .certificate(certificateManager.getCertificate())
                .domainNames(singletonList(fullyQualifiedDomainName.get()))
                .defaultRootObject("index.html")
                .defaultBehavior(BehaviorOptions.builder().origin(new S3Origin(bucket)).viewerProtocolPolicy(ViewerProtocolPolicy.REDIRECT_TO_HTTPS).build())
                .build());

        new BucketDeployment(this, "DeployWithInvalidation", BucketDeploymentProps.builder()
                .sources(singletonList(Source.asset("../web")))
                .destinationBucket(bucket)
                .distribution(distribution)
                .distributionPaths(singletonList("/*"))
                .build());

        new ARecord(this, "FrontendAliasRecord", ARecordProps.builder()
                .recordName(fullyQualifiedDomainName.get())
                .target(RecordTarget.fromAlias(new CloudFrontTarget(distribution)))
                .zone(route53.getZone())
                .build());

        CfnOutput.Builder.create(this, "Site")
                .description("Site domain url")
                .value(bucket.getBucketWebsiteUrl()).build();

        CfnOutput.Builder.create(this, "FQDN")
                .description("Site domain url")
                .value(fullyQualifiedDomainName.get()).build();
    }
}
