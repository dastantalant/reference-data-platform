package com.platform.common.config;

import com.platform.common.entity.User;
import com.platform.common.repository.UserRepository;
import org.jspecify.annotations.NullMarked;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component("jpaAuditorAware")
public class JpaAuditorAware implements AuditorAware<User> {

    private final UserRepository userRepository;

    public JpaAuditorAware(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NullMarked
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return userRepository.findByUsername("system");
        }

        return userRepository.findByUsername(auth.getName())
                .or(() -> userRepository.findByUsername("system"));
    }
}