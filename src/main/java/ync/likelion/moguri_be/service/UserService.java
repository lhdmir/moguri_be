package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.dto.UserDto;
import ync.likelion.moguri_be.dto.TodayMeal;
import ync.likelion.moguri_be.model.*;
import ync.likelion.moguri_be.repository.*;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TodayBreakfastRepository todayBreakfastRepository;
    private final TodayLunchRepository todayLunchRepository;
    private final TodayDinnerRepository todayDinnerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    private final TodayExerciseRepository todayExerciseRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       TodayBreakfastRepository todayBreakfastRepository,
                       TodayLunchRepository todayLunchRepository,
                       TodayDinnerRepository todayDinnerRepository,
                       BCryptPasswordEncoder passwordEncoder, TodayExerciseRepository todayExerciseRepository) {
        this.userRepository = userRepository;
        this.todayBreakfastRepository = todayBreakfastRepository;
        this.todayLunchRepository = todayLunchRepository;
        this.todayDinnerRepository = todayDinnerRepository;
        this.passwordEncoder = passwordEncoder;
        this.todayExerciseRepository = todayExerciseRepository;
    }

    // 사용자 저장 또는 업데이트
    public User save(UserDto userDto) {
        // 사용자 이름으로 기존 사용자 조회
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElse(new User()); // 사용자가 없으면 새 User 객체 생성

        // 사용자 정보 업데이트
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        // 비밀번호는 필요할 때만 업데이트
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        }
        user.setTargetWeight(userDto.getTargetWeight());

        userRepository.save(user); // 사용자 저장
        return user;
    }

    // 사용자 이름으로 사용자 조회
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 이메일로 사용자 조회
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 비밀번호 확인
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 오늘의 식사 정보 조회
    public TodayMeal getTodayMeal(int userId) {
        TodayBreakfast breakfast = todayBreakfastRepository.findByUserId(userId);
        TodayLunch lunch = todayLunchRepository.findByUserId(userId);
        TodayDinner dinner = todayDinnerRepository.findByUserId(userId);

        return new TodayMeal(breakfast, lunch, dinner);
    }

    // 로그아웃 메서드
    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // 인증 정보를 무효화합니다.
            SecurityContextHolder.clearContext();
        }
    }

    public List<TodayExercise> getTodayExercises(int userId) {
        return todayExerciseRepository.findByUserId(userId);
    }
}
