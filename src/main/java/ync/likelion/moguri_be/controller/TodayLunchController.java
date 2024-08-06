package ync.likelion.moguri_be.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ync.likelion.moguri_be.dto.TodayMealDto;
import ync.likelion.moguri_be.dto.TodayMealResponse;
import ync.likelion.moguri_be.model.TodayLunch;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.TodayLunchRepository;
import ync.likelion.moguri_be.service.TodayLunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ync.likelion.moguri_be.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/lunch")
public class TodayLunchController {

    private final TodayLunchService lunchService;
    private final UserService userService;
    private final TodayLunchRepository todayLunchRepository;

    @Autowired
    public TodayLunchController(TodayLunchService lunchService, UserService userService, TodayLunchRepository todayLunchRepository) {
        this.lunchService = lunchService;
        this.userService = userService;
        this.todayLunchRepository = todayLunchRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLunches(@RequestHeader("Authorization") String authorization) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<TodayLunch> lunches = todayLunchRepository.findAllByUserId(user.getId());
        List<TodayMealResponse> lunchResponse = lunches.stream().map(lunch -> {
            TodayMealResponse response = new TodayMealResponse();
            response.setId(lunch.getId());
            response.setMenu(lunch.getMenu());
            response.setCalorie(lunch.getCalorie());
            return response;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "식단 점심 리스트 조회를 성공했습니다.");
        response.put("lunches", lunchResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createLunch(@RequestHeader("Authorization") String authorization,
                                                           @RequestBody TodayMealDto todayLunch) {
        TodayLunch createdLunch = new TodayLunch();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        createdLunch.setUser(user);
        createdLunch.setMenu(todayLunch.getMenu());
        createdLunch.setCalorie(todayLunch.getCalorie());

        lunchService.saveLunch(createdLunch);

        Map<String, Object> response = new HashMap<>();
        response.put("id", createdLunch.getId());
        response.put("menu", createdLunch.getMenu());
        response.put("calorie", createdLunch.getCalorie());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLunch(@PathVariable Integer id,
                                                           @RequestBody TodayLunch updatedLunch) {
        Optional<TodayLunch> existingLunchOpt = lunchService.getLunchById(id);

        if (existingLunchOpt.isPresent()) {
            TodayLunch existingLunch = existingLunchOpt.get();

            // Check for input validation
            if (updatedLunch.getMenu() == null || updatedLunch.getMenu().isEmpty() ||
                    updatedLunch.getMenu().length() > 20 ||
                    updatedLunch.getCalorie() < 0 || updatedLunch.getCalorie() > 2000) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid input");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Check if the content is actually updated
            if (existingLunch.getMenu().equals(updatedLunch.getMenu()) &&
                    existingLunch.getCalorie() == updatedLunch.getCalorie()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No changes detected");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Update the existing lunch
            existingLunch.setMenu(updatedLunch.getMenu());
            existingLunch.setCalorie(updatedLunch.getCalorie());
            TodayLunch updated = lunchService.saveLunch(existingLunch);

            TodayMealResponse  updatedResponse= new TodayMealResponse();
            updatedResponse.setId(updated.getId());
            updatedResponse.setMenu(updated.getMenu());
            updatedResponse.setCalorie(updated.getCalorie());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "식단 점심 리스트가 수정되었습니다.");
            response.put("lunch", updatedResponse);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lunch list not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')") // 인증된 사용자만 접근 가능
    public ResponseEntity<Map<String, Object>> deleteLunchById(@PathVariable Integer id) {
        if (lunchService.getLunchById(id).isPresent()) {
            lunchService.deleteLunchById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "식단 점심 리스트가 삭제되었습니다");
            response.put("deleted_id", id); // Include deleted ID for confirmation

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lunch list not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // 유효성 검사 실패 시 오류 메시지 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        errors.put("error", "Invalid input");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
