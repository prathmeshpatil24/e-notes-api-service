package com.enotes.cofig;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareConfig implements AuditorAware<Integer> {
    @Override
    public Optional<Integer> getCurrentAuditor() {

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
//            return Optional.empty(); // IMPORTANT for registration
//        }
//
//        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
//        return Optional.of(user.getId());


        return Optional.of(null); // hardcode admin/user id  as 2  for dev
    }
}
