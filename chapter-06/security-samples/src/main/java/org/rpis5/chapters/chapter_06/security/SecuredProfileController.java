package org.rpis5.chapters.chapter_06.security;

import reactor.core.publisher.Mono;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SecuredProfileController {

    private final ProfileService profileService;

    public SecuredProfileController(ProfileService service) {
        profileService = service;
    }

    @GetMapping("/profiles")
    public Mono<Profile> getProfile() {
        return ReactiveSecurityContextHolder
            .getContext()
            .map(SecurityContext::getAuthentication)
            .flatMap(auth -> profileService.getByUser(auth.getName()));
    }
}
