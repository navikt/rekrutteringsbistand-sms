package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "rekrutteringsbistand.altinn-varsel")
public class AltinnVarselProperties {

    private URI uri;
    private String systemBruker;
    private String systemPassord;
    private boolean debugLog;

    public AltinnVarselProperties() {
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getSystemBruker() {
        return systemBruker;
    }

    public void setSystemBruker(String systemBruker) {
        this.systemBruker = systemBruker;
    }

    public String getSystemPassord() {
        return systemPassord;
    }

    public void setSystemPassord(String systemPassord) {
        this.systemPassord = systemPassord;
    }

    public boolean isDebugLog() {
        return debugLog;
    }

    public void setDebugLog(boolean debugLog) {
        this.debugLog = debugLog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AltinnVarselProperties that = (AltinnVarselProperties) o;
        return debugLog == that.debugLog &&
                Objects.equals(uri, that.uri) &&
                Objects.equals(systemBruker, that.systemBruker) &&
                Objects.equals(systemPassord, that.systemPassord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, systemBruker, systemPassord, debugLog);
    }

    @Override
    public String toString() {
        return "AltinnVarselProperties{" +
                "uri=" + uri +
                ", systemBruker='" + systemBruker + '\'' +
                ", systemPassord='" + systemPassord + '\'' +
                ", debugLog=" + debugLog +
                '}';
    }
}
