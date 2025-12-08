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
//            return Optional.of(0); // or Optional.empty();
//        }
//
//        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
//        return Optional.of(user.getId());


        return Optional.of(0); // hardcode admin/user id  as 2/0  for dev
    }
}
