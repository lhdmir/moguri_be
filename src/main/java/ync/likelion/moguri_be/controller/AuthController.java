//package ync.likelion.moguri_be.controller;
//
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//import ync.likelion.moguri_be.dto.LoginRequest;
//import ync.likelion.moguri_be.dto.LoginResponse;
//import ync.likelion.moguri_be.dto.ErrorResponse;
//import ync.likelion.moguri_be.model.User;
//import ync.likelion.moguri_be.service.UserService;
//import ync.likelion.moguri_be.util.JwtUtil;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:3000")
//public class AuthController {
//
//    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    @Autowired
//    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
//        this.userService = userService;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtUtil = jwtUtil;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
//        Optional<User> userOptional = userService.findByUsername(loginRequest.getUsername());
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
//                String token = jwtUtil.generateToken(user.getUsername());
//                // 추가적으로 필요한 사용자 정보를 조회하여 반환합니다.
//                LoginResponse loginResponse = userService.buildLoginResponse(user, token);
//                return ResponseEntity.ok(loginResponse);
//            }
//        }
//        return ResponseEntity.status(401).body(new ErrorResponse("ID 또는 PW가 잘못되었습니다"));
//    }
//}
