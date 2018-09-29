package com.example.service.gitter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "gitter")
public class GitterProperties {

    private Auth auth = new Auth();

    private GenericProperties api;
    private GenericProperties stream;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public GenericProperties getApi() {
        return api;
    }

    public void setApi(GenericProperties api) {
        this.api = api;
    }

    public GenericProperties getStream() {
        return stream;
    }

    public void setStream(GenericProperties stream) {
        this.stream = stream;
    }

    public static class GenericProperties {
        private URI endpoint;
        private URI messagesResource;
        private String version = "v1";

        public URI getMessagesResource() {
            return messagesResource;
        }

        public void setMessagesResource(URI messagesResource) {
            this.messagesResource = messagesResource;
        }

        public URI getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(URI endpoint) {
            this.endpoint = endpoint;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static class Auth {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
