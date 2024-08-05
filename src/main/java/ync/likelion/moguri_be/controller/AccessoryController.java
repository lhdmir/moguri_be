package ync.likelion.moguri_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ync.likelion.moguri_be.model.AccessoryCode;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.service.AccessoryService;
import ync.likelion.moguri_be.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class AccessoryController {

    @Autowired
    private UserService userService; // 사용자 서비스
    @Autowired
    private AccessoryService accessoryService; // 악세서리 서비스

    @PostMapping("/accessory")
    public ResponseEntity<Object> drawAccessory(
            @RequestHeader("Authorization") String authorization) {

        // 유효한 토큰 검증
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        int userId = user.getId();

        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // 사용자가 보유한 악세서리 목록 가져오기
        List<AccessoryCode> userAccessories = accessoryService.getUserAccessories(user);

        // 모든 악세서리 목록
        List<AccessoryCode> allAccessories = accessoryService.getAllAccessories();

        // 뽑기 가능 여부 체크
        if (userAccessories.size() >= allAccessories.size()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", "All items owned"));
        }

        // 랜덤한 악세서리 선택 (중복되지 않도록)
        AccessoryCode drawnAccessory = accessoryService.drawRandomAccessory(userAccessories);

        // 선택된 악세서리를 사용자 인벤토리에 추가
        accessoryService.addAccessoryToUser(userId, drawnAccessory);

        // 성공 응답
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Accessory drawn successfully");
        response.put("accessory", Map.of(
                "id", drawnAccessory.getId(),
                "name", drawnAccessory.getName(),
                "imageUrl", drawnAccessory.getImageUrl()
        ));

        return ResponseEntity.ok(response);
    }
}
