package dk.bringlarsen.aws;

import static java.lang.String.join;

public record FullyQualifiedDomainName(String subdomain, String domain) {

    public String get() {
        return join(".", subdomain, domain);
    }
}
