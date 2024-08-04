package ync.likelion.moguri_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import ync.likelion.moguri_be.repository.MoguriCodeRepository;
import ync.likelion.moguri_be.repository.MoguriRepository;
import ync.likelion.moguri_be.repository.UserRepository;
import ync.likelion.moguri_be.service.UserService;
import ync.likelion.moguri_be.util.JwtUtil;
import ync.likelion.moguri_be.model.MoguriCode;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final MoguriRepository moguriRepository;
    private final UserRepository userRepository;
    @Autowired
    private MoguriCodeRepository moguriCodeRepository;

    private static final List<String> MOGURI_CODES = List.of(
            "http://158.180.71.193/image/moguri_1-1.png",
            "http://158.180.71.193/image/moguri_2-1.png",
            "http://158.180.71.193/image/moguri_3-1.png",
            "http://158.180.71.193/image/moguri_4-1.png",
            "http://158.180.71.193/image/moguri_5-1.png",
            "http://158.180.71.193/image/moguri_6-1.png"
    );
    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, MoguriRepository moguriRepository, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.moguriRepository = moguriRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "사용할 수 없는 ID 또는 Email")
    })
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDto userDto) {
        // 사용자 이름 중복 체크
        if (userService.findByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("사용할 수 없는 ID 입니다."));
        }

        // 이메일 중복 체크
        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("사용할 수 없는 Email 입니다."));
        }

        // 사용자 저장
        User user = userService.save(userDto);
        // 랜덤 모구리 코드 부여
        String randomMoguriCode = getRandomMoguriCode();
        Moguri moguri = new Moguri();
        moguri.setUser(user); // 사용자 ID 설정
        moguri.setMoguriCode(getMoguriCodeId(randomMoguriCode)); // 랜덤 모구리 코드 설정

        moguriRepository.save(moguri); // 모구리 저장
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("회원가입에 성공했습니다."));

    }
    private String getRandomMoguriCode() {
        Random random = new Random();
        int index = random.nextInt(MOGURI_CODES.size()); // 0부터 MOGURI_CODES 크기까지 랜덤 인덱스 생성
        return MOGURI_CODES.get(index); // 랜덤으로 선택된 모구리 코드 반환
    }

    private MoguriCode getMoguriCodeId(String imageUrl) {
        return moguriCodeRepository.findByImageUrl(imageUrl)
                .orElse(null);
    }


    @Operation(summary = "사용자 로그인", description = "사용자가 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "존재하지 않는 사용자 또는 잘못된 비밀번호")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginDto loginDto) {
        Optional<User> userOpt = userService.findByUsername(loginDto.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("존재하지 않는 사용자입니다."));
        }

        User user = userOpt.get();
        if (userService.checkPassword(loginDto.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            Date cookieExpirationTime = new Date(System.currentTimeMillis() + 3600000); // 1시간

            TodayMeal todayMeal = userService.getTodayMeal(user.getId());
            List<TodayExercise> todayExercise = userService.getTodayExercises(user.getId());

            Moguri moguri = moguriRepository.findByUser(user).orElse(new Moguri());

            LoginResponse loginResponse = new LoginResponse(token, cookieExpirationTime, moguri, todayMeal, todayExercise);
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ID 또는 PW가 잘못되었습니다."));
        }
    }

    @Operation(summary = "사용자 로그아웃", description = "사용자가 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "잘못된 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            userService.logout();
            return ResponseEntity.ok(new ResponseMessage("Logout successful"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid token"));
        }
    }
}
