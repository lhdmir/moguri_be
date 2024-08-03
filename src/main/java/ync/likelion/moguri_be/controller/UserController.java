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
import ync.likelion.moguri_be.repository.MoguriRepository;
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
    private MoguriRepository moguriRepository;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDto userDto) {
        // 사용자 이름 중복 체크
        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            // 변경: HTTP 상태 코드 상수 사용
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("사용할 수 없는 ID 입니다."));
        }

        // 이메일 중복 체크
        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            // 변경: HTTP 상태 코드 상수 사용
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("사용할 수 없는 Email 입니다."));
        }

        // 사용자 저장
        userService.save(userDto);

        // 회원가입 성공 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("회원가입에 성공했습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginDto loginDto) {
        Optional<User> userOpt = userService.findByUsername(loginDto.getUsername());

        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("존재하지 않는 사용자입니다."));
        }

        User user = userOpt.get();
        if (userService.checkPassword(loginDto.getPassword(), user.getPassword())) {
            // JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getUsername());

            Date cookieExpirationTime = new Date(System.currentTimeMillis() + 3600000); // 1시간

            // 오늘의 식사 정보 조회
            TodayMeal todayMeal = userService.getTodayMeal(user.getId());
            List<TodayExercise> todayExercise = userService.getTodayExercises(user.getId()); // Exercise 리스트 조회

            // Moguri 객체 초기화
            Moguri moguri = moguriRepository.findByUserId(user.getId())
                    .orElse(new Moguri()); // 사용자에 해당하는 모구리 조회, 없으면 기본 객체 생성

            // 로그인 성공 응답 구성
            LoginResponse loginResponse = new LoginResponse(token, cookieExpirationTime, moguri, todayMeal, todayExercise);

            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ID 또는 PW가 잘못되었습니다."));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // 변경: 로그아웃 처리 로직 설명 추가 (예: 블랙리스트에 토큰 추가)
            userService.logout();
            // 변경: 응답을 JSON 형식의 문자열 대신 ResponseMessage DTO 사용
            return ResponseEntity.ok(new ResponseMessage("Logout successful"));
        } else {
            // 변경: 응답을 JSON 형식의 문자열 대신 ErrorResponse DTO 사용
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid token"));
        }
    }
}
