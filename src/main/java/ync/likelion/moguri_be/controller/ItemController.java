package ync.likelion.moguri_be.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ync.likelion.moguri_be.model.*;
import ync.likelion.moguri_be.service.ItemService;
import ync.likelion.moguri_be.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class ItemController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @PostMapping("/accessory")
    @Operation(summary = "액세서리 뽑기", description = "사용자가 액세서리를 뽑습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세서리가 성공적으로 뽑혔습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "409", description = "모든 아이템을 소유하고 있음")
    })
    public ResponseEntity<Object> drawAccessory(@RequestHeader("Authorization") String authorization) {
        return drawItem(authorization, true, "Accessory drawn successfully");
    }

    @PostMapping("/background")
    @Operation(summary = "배경화면 뽑기", description = "사용자가 배경화면 뽑습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배경화면이 성공적으로 뽑혔습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "409", description = "모든 아이템을 소유하고 있음")
    })
    public ResponseEntity<Object> drawBackground(@RequestHeader("Authorization") String authorization) {
        return drawItem(authorization, false, "Background drawn successfully");
    }
    private ResponseEntity<Object> drawItem(String authorization, boolean isAccessory, String successMessage) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        int userId = user.getId();
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        List<AccessoryCode> userAccessoryItems = itemService.getUserAccessoryItems(user);
        List<BackgroundCode> userBackgroundItems = itemService.getUserBackgroundItems(user);

        List<AccessoryCode> allAccessoryItems = itemService.getAllAccessoryItems();
        List<BackgroundCode> allBackgroundItems = itemService.getAllBackgroundItems();

        if (isAccessory) {
            if (userAccessoryItems.size() >= allAccessoryItems.size()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("error", "모든 악세서리를 보유중입니다 !"));
            }

            AccessoryCode drawnItem = itemService.drawRandomAccessoryItem(userAccessoryItems);
            itemService.addAccessoryItemToUser(userId, drawnItem);

            Map<String, Object> response = new HashMap<>();
            response.put("message", successMessage);
            response.put("item", Map.of(
                    "id", drawnItem.getId(),
                    "name", drawnItem.getName(),
                    "imageUrl", drawnItem.getImageUrl()
            ));

            return ResponseEntity.ok(response);
        } else {
            if (userBackgroundItems.size() >= allBackgroundItems.size()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("error", "모든 배경화면을 보유 중입니다 !"));
            }

            BackgroundCode drawnItem = itemService.drawRandomBackgroundItem(userBackgroundItems);
            itemService.addBackgroundItemToUser(userId, drawnItem);

            Map<String, Object> response = new HashMap<>();
            response.put("message", successMessage);
            response.put("item", Map.of(
                    "id", drawnItem.getId(),
                    "name", drawnItem.getName(),
                    "imageUrl", drawnItem.getImageUrl()
            ));

            return ResponseEntity.ok(response);
        }
    }

}
