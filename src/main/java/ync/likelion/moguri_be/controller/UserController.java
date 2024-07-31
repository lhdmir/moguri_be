package ync.likelion.moguri_be.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ync.likelion.moguri_be.dto.ErrorResponse;
import ync.likelion.moguri_be.dto.ResponseMessage;
import ync.likelion.moguri_be.dto.UserDto;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        User savedUser = userService.save(userDto);

        // 회원가입 성공 응답
        return ResponseEntity.status(201).body(new ResponseMessage("회원가입에 성공했습니다."));
    }

}
