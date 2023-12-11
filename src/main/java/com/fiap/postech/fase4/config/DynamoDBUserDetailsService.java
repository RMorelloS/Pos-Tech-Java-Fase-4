package com.fiap.postech.fase4.config;

import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class DynamoDBUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.getUserByLogin(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o nome: " + username);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserLogin())
                .password( new BCryptPasswordEncoder().encode(user.getUserKey()))
                .roles(user.getRole())
                .build();
    }
}
