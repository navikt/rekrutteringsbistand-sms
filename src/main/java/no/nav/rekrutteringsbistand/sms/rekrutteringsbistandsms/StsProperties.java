package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
@ConfigurationProperties(prefix = "rekrutteringsbistand.sts")
public class StsProperties {

    private URI wsUri;
    private String username;
    private String password;

    public StsProperties() {
    }

    public URI getWsUri() {
        return wsUri;
    }

    public void setWsUri(URI wsUri) {
        this.wsUri = wsUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StsProperties that = (StsProperties) o;
        return Objects.equals(wsUri, that.wsUri) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wsUri, username, password);
    }

    @Override
    public String toString() {
        return "StsProperties{" +
                "wsUri=" + wsUri +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
