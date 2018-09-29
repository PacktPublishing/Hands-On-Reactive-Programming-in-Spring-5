package com.example.service.gitter;


import org.springframework.web.util.UriComponentsBuilder;

public final class GitterUriBuilder {

    private GitterUriBuilder() {
    }

    public static UriComponentsBuilder from(GitterProperties.GenericProperties gitterProperties) {
        return UriComponentsBuilder.fromUri(gitterProperties.getEndpoint())
                .pathSegment(gitterProperties.getVersion(), gitterProperties.getMessagesResource().toASCIIString());
    }
}
