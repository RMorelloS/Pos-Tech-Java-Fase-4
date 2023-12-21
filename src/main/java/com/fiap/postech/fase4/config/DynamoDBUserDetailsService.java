package com.fiap.postech.fase4.config;

import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Primary
@NoArgsConstructor
@AllArgsConstructor
@Service
public class DynamoDBUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.getUserByLogin(Mono.just(username))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usuário não encontrado com o nome: " + username)))
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUserLogin())
                        .password(new BCryptPasswordEncoder().encode(user.getUserKey()))
                        .roles(user.getRole())
                        .build());
    }
}
