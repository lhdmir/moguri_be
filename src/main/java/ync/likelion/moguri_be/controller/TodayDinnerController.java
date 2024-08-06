package ync.likelion.moguri_be.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ync.likelion.moguri_be.dto.TodayMealDto;
import ync.likelion.moguri_be.dto.TodayMealResponse;
import ync.likelion.moguri_be.model.TodayDinner;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.TodayDinnerRepository;
import ync.likelion.moguri_be.repository.UserRepository;
import ync.likelion.moguri_be.service.TodayDinnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dinner")
public class TodayDinnerController {

    private final TodayDinnerService dinnerService;
    private final UserRepository userService;
    private final TodayDinnerRepository todayDinnerRepository;

    @Autowired
    public TodayDinnerController(TodayDinnerService dinnerService, UserRepository userService, TodayDinnerRepository todayDinnerRepository) {
        this.dinnerService = dinnerService;
        this.userService = userService;
        this.todayDinnerRepository = todayDinnerRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDinners(@RequestHeader("Authorization") String authorization) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<TodayDinner> dinners = todayDinnerRepository.findAllByUserId(user.getId());
        List<TodayMealResponse> dinnerResponse = dinners.stream().map(dinner -> {
            TodayMealResponse response = new TodayMealResponse();
            response.setId(dinner.getId());
            response.setMenu(dinner.getMenu());
            response.setCalorie(dinner.getCalorie());
            return response;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "식단 저녁 리스트 조회를 성공했습니다.");
        response.put("dinners", dinnerResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDinner(@RequestHeader("Authorization") String authorization,
                                                            @RequestBody TodayMealDto todayDinner) {
        TodayDinner createDinner = new TodayDinner();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        createDinner.setUser(user);
        createDinner.setMenu(todayDinner.getMenu());
        createDinner.setCalorie(todayDinner.getCalorie());

        dinnerService.saveDinner(createDinner);

        // Check for input validation
        if (todayDinner.getMenu() == null || todayDinner.getMenu().isEmpty() ||
                todayDinner.getMenu().length() > 20 ||
                todayDinner.getCalorie() < 0 || todayDinner.getCalorie() > 2000) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid input");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", createDinner.getId());
        response.put("menu", createDinner.getMenu());
        response.put("calorie", createDinner.getCalorie());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDinner(@PathVariable Integer id, @RequestBody TodayDinner updatedDinner) {
        Optional<TodayDinner> existingDinnerOpt = dinnerService.getDinnerById(id);

        if (existingDinnerOpt.isPresent()) {
            TodayDinner existingDinner = existingDinnerOpt.get();

            // Check for input validation
            if (updatedDinner.getMenu() == null || updatedDinner.getMenu().isEmpty() ||
                    updatedDinner.getMenu().length() > 20 ||
                    updatedDinner.getCalorie() < 0 || updatedDinner.getCalorie() > 2000) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid input");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Check if the content is actually updated
            if (existingDinner.getMenu().equals(updatedDinner.getMenu()) &&
                    existingDinner.getCalorie() == updatedDinner.getCalorie()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No changes detected");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Update the existing dinner
            existingDinner.setMenu(updatedDinner.getMenu());
            existingDinner.setCalorie(updatedDinner.getCalorie());
            TodayDinner updated = dinnerService.saveDinner(existingDinner);
            TodayMealResponse  updatedResponse= new TodayMealResponse();
            updatedResponse.setId(updated.getId());
            updatedResponse.setMenu(updated.getMenu());
            updatedResponse.setCalorie(updated.getCalorie());

            Map<String, Object> response = new HashMap<>();
            response.put("id", updated.getId());
            response.put("menu", updated.getMenu());
            response.put("calorie", updated.getCalorie());

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Dinner list not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDinnerById(@PathVariable Integer id) {
        if (dinnerService.getDinnerById(id).isPresent()) {
            dinnerService.deleteDinnerById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "식단 저녁 리스트가 삭제되었습니다");
            response.put("deleted_id", id); // Include deleted ID for confirmation

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Dinner list not found");
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
