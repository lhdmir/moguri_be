package ync.likelion.moguri_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ync.likelion.moguri_be.dto.ErrorResponse;
import ync.likelion.moguri_be.dto.MoguriDto;
import ync.likelion.moguri_be.dto.MoguriResponse;
import ync.likelion.moguri_be.dto.UserDto;
import ync.likelion.moguri_be.model.Moguri;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.MoguriRepository;
import ync.likelion.moguri_be.service.UserService;

@RestController
@RequestMapping("/api/moguri")
public class MoguriController {

    private final UserService userService;
    private final MoguriRepository moguriRepository;

    @Autowired
    public MoguriController(UserService userService, MoguriRepository moguriRepository) {
        this.userService = userService;
        this.moguriRepository = moguriRepository;
    }

    @PostMapping
    public ResponseEntity<Object> createMoguri(@RequestBody MoguriDto moguriDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // 토큰에서 사용자 이름 추출
        System.out.println(username);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 모구리 이름 중복 체크
        if (moguriRepository.existsByNameAndUserId(moguriDto.getName(), user.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("이미 존재하는 모구리 이름입니다."));
        }

        // UserDto 설정
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(user.getEmail()); // 기존 이메일 설정
        userDto.setTargetWeight(moguriDto.getTargetWeight());

        // 모구리 생성 로직
        Moguri newMoguri = new Moguri();
        newMoguri.setName(moguriDto.getName());
        newMoguri.setUser(user); // 사용자와 연결


        userService.save(userDto); // 사용자 정보를 저장

        moguriRepository.save(newMoguri); // 모구리 저장

        // MoguriResponse 생성
        MoguriResponse response = new MoguriResponse();
        response.setMessage("Create moguri successful");
        response.setMoguri(moguriDto); // 필요시 생성된 모구리의 정보를 포함하도록 수정 가능

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 추가적인 메서드 (모구리 조회, 업데이트 등)도 여기에 추가할 수 있습니다.
}
