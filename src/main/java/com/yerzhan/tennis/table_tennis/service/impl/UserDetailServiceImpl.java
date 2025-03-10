package com.yerzhan.tennis.table_tennis.service.impl;

import com.yerzhan.tennis.table_tennis.dto.AdminUpdateUserDTO;
import com.yerzhan.tennis.table_tennis.dto.UserDTO;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.mapper.UserMapper;
import com.yerzhan.tennis.table_tennis.repository.UserRepository;
import com.yerzhan.tennis.table_tennis.repository.GamesRepository;
import com.yerzhan.tennis.table_tennis.repository.GameRoundRepository;
import com.yerzhan.tennis.table_tennis.utils.Role;
import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final GamesRepository gamesRepository;
    private final GameRoundRepository gameRoundRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalStateException("Пользователь с именем '" + userDTO.getUsername() + "' уже существует");
        }

        // Конвертируем DTO в сущность
        Users user = userMapper.toEntity(userDTO);
        
        // Устанавливаем зашифрованный пароль
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        // Устанавливаем роль по умолчанию
        user.setDefaultRole();

        // Сохраняем пользователя
        Users savedUser = userRepository.save(user);

        // Возвращаем DTO сохраненного пользователя
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void updateUser(AdminUpdateUserDTO dto) {
        // Находим пользователя по ID
        Users user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        String oldUsername = user.getUsername();
        String newUsername = dto.getUsername();

        // Проверяем, не существует ли другой пользователь с таким же именем
        userRepository.findByUsername(newUsername)
                .ifPresent(existingUser -> {
                    if (!dto.getId().equals(existingUser.getId())) {
                        throw new IllegalStateException("Пользователь с таким именем уже существует");
                    }
                });

        // Обновляем имя пользователя
        user.setUsername(newUsername);

        // Получаем все игры, где пользователь является игроком или оппонентом
        List<Games> playerGames = gamesRepository.findByPlayerUsernameAndGameStatusIn(oldUsername, List.of(GameStatus.values()));
        List<Games> opponentGames = gamesRepository.findByOpponentUsernameAndGameStatusIn(oldUsername, List.of(GameStatus.values()));

        // Обновляем имя пользователя во всех играх
        for (Games game : playerGames) {
            game.getPlayer().setUsername(newUsername);
        }
        for (Games game : opponentGames) {
            game.getOpponent().setUsername(newUsername);
        }

        // Сохраняем обновленные игры
        gamesRepository.saveAll(playerGames);
        gamesRepository.saveAll(opponentGames);

        // Сохраняем обновленного пользователя
        Users savedUser = userRepository.save(user);

        // Обновляем аутентификацию пользователя
        UserDetails updatedUserDetails = User.withUsername(savedUser.getUsername())
                .password(savedUser.getPassword())
                .authorities("ROLE_" + savedUser.getRole().name())
                .build();

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                updatedUserDetails.getPassword(),
                updatedUserDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateUserRole(Integer userId, String role) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        
        if (role.equals("ADMIN")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        
        // Проверяем, не является ли пользователь последним администратором
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .count();
            if (adminCount <= 1) {
                throw new IllegalStateException("Нельзя удалить последнего администратора");
            }
        }

        // Получаем все игры пользователя
        List<Games> playerGames = gamesRepository.findByPlayerUsernameAndGameStatusIn(user.getUsername(), List.of(GameStatus.values()));
        List<Games> opponentGames = gamesRepository.findByOpponentUsernameAndGameStatusIn(user.getUsername(), List.of(GameStatus.values()));

        // Удаляем раунды для всех игр
        for (Games game : playerGames) {
            gameRoundRepository.deleteByGameId(game.getId());
        }
        for (Games game : opponentGames) {
            gameRoundRepository.deleteByGameId(game.getId());
        }

        // Удаляем игры
        gamesRepository.deleteAll(playerGames);
        gamesRepository.deleteAll(opponentGames);
        
        // Удаляем пользователя
        userRepository.delete(user);
    }
}
