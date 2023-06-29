package dk.bringlarsen.aws;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.constructs.Construct;

import java.util.Collections;

import static java.util.Collections.singletonList;

public class CertificateManager extends Construct {

    private Certificate certificate;
    public CertificateManager(@NotNull Construct scope, @NotNull String id, FullyQualifiedDomainName fullyQualifiedDomainName, IHostedZone hostedZone) {
        super(scope, id);

        certificate = Certificate.Builder.create(this, id)
                .domainName(fullyQualifiedDomainName.domain())
                .validation(CertificateValidation.fromDns(hostedZone))
                .subjectAlternativeNames(singletonList(String.join(".", "*", fullyQualifiedDomainName.domain())))
                .build();
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
