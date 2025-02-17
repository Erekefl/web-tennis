package com.yerzhan.tennis.table_tennis.service.impl;

import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.repository.GamesRepository;
import com.yerzhan.tennis.table_tennis.repository.UserRepository;
import com.yerzhan.tennis.table_tennis.utils.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final GamesRepository gamesRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();
    }

    public Users getAuthenticatedUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()){
            throw new UsernameNotFoundException("User not authenticated");
        }
        Users users = userRepository.findByUsername(authentication.getName());
        if (users == null){
            throw new UsernameNotFoundException("User not found");
        }else {
            return users;
        }

    }

    public List<Games> getGamesForUsers(Users users){
        return gamesRepository.findByPlayer2(users);
    }

    @Transactional
    public void createUser(String username, String password) {
        // Проверяем, существует ли пользователь с таким username
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalStateException("Пользователь с таким именем уже существует!");
        }

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(passwordEncoder().encode(password));
        user.setDefaultRole(); // Устанавливаем роль по умолчанию

        userRepository.save(user);
    }
    public Users findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void updateUser(int id, String username, String password, String role) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        user.setUsername(username);

        // Если пароль передан, хешируем его и обновляем
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder().encode(password));
        }

        user.setRole(Role.valueOf(role));
        userRepository.save(user);
    }

    public Users getUsersById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User with ID " + id + " not found"));
    }


    public void  deleteByUserId(int id){
        if (!userRepository.existsById(id)){
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public void  deleteByUserName(String username){
        Users users = userRepository.findByUsername(username);
        if (users == null){
            throw new IllegalArgumentException("User with username " + username + " not found");
        }
        userRepository.delete(users);
    }

    public List<Users> getAllUsers(){
        return userRepository.findAll();
    }

    public List<Users> getAllUsersByName(String username){
        return userRepository.findAll();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
