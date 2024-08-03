package ync.likelion.moguri_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ync.likelion.moguri_be.dto.ErrorResponse;
import ync.likelion.moguri_be.dto.LoginDto;
import ync.likelion.moguri_be.dto.LoginResponse;
import ync.likelion.moguri_be.dto.ResponseMessage;
import ync.likelion.moguri_be.dto.TodayMeal;
import ync.likelion.moguri_be.dto.UserDto;
import ync.likelion.moguri_be.model.Moguri;
import ync.likelion.moguri_be.model.TodayExercise;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.service.UserService;
import ync.likelion.moguri_be.util.JwtUtil;

import java.util.*;

@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDto userDto) {
        // 사용자 이름 중복 체크
        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("사용할 수 없는 ID 입니다."));
        }

        // 이메일 중복 체크
        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(new ErrorResponse("사용할 수 없는 Email 입니다."));
        }

        // 사용자 저장
        userService.save(userDto);

        // 회원가입 성공 응답
        return ResponseEntity.status(201).body(new ResponseMessage("회원가입에 성공했습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginDto loginDto) {
        Optional<User> userOpt = userService.findByUsername(loginDto.getUsername());

        if (userOpt.isPresent() && userService.checkPassword(loginDto.getPassword(), userOpt.get().getPassword())) {
            User user = userOpt.get();
            // JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getUsername());
            Date cookieExpirationTime = new Date(System.currentTimeMillis() + 3600000); // 1시간

            // 오늘의 식사 정보 조회
            TodayMeal todayMeal = userService.getTodayMeal(user.getId());
            List<TodayExercise> todayExercise = new ArrayList<>(); // Exercise 리스트 생성

            // Moguri 객체 초기화 및 설정
            Moguri moguri = new Moguri(); // 필요에 따라 moguri 설정 추가

            // 로그인 성공 응답 구성
            LoginResponse loginResponse = new LoginResponse(token, cookieExpirationTime, moguri, todayMeal, todayExercise);

            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ID 또는 PW가 잘못되었습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // 토큰 유효성 검사 (여기서는 단순히 인증 정보가 있는지만 체크)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            userService.logout();
            return ResponseEntity.ok().body("{\"message\": \"Logout successful\"}");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid token\"}");
        }
    }


}
