package dev.ivan.reviewverso_back.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;

@Service
public class JpaUserDetailsService implements UserDetailsService {

        private UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

        @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
  
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUserName(identifier))
                .map(SecurityUser::new)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con este email o userName"));
    }
}
