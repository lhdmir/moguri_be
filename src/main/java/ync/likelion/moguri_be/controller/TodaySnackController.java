package ync.likelion.moguri_be.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ync.likelion.moguri_be.dto.TodayMealDto;
import ync.likelion.moguri_be.dto.TodayMealResponse;
import ync.likelion.moguri_be.model.TodaySnack;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.TodaySnackRepository;
import ync.likelion.moguri_be.service.TodaySnackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ync.likelion.moguri_be.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/snack")
public class TodaySnackController {

    private final TodaySnackService snackService;
    private final UserService userService;
    private final TodaySnackRepository todaySnackRepository;

    @Autowired
    public TodaySnackController(TodaySnackService snackService, UserService userService, TodaySnackRepository todaySnackRepository) {
        this.snackService = snackService;
        this.userService = userService;
        this.todaySnackRepository = todaySnackRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSnacks(@RequestHeader("Authorization") String authorization) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<TodaySnack> snacks = todaySnackRepository.findAllByUserId(user.getId());

        List<TodayMealResponse> lunchResponse = snacks.stream().map(snack -> {
            TodayMealResponse response = new TodayMealResponse();
            response.setId(snack.getId());
            response.setMenu(snack.getMenu());
            response.setCalorie(snack.getCalorie());
            return response;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "식단 간식 리스트 조회를 성공했습니다.");
        response.put("snacks", lunchResponse);

        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<Map<String, Object>> createSnack(@RequestHeader("Authorization") String authorization,
                                                           @RequestBody TodayMealDto todaySnack) {
        TodaySnack createdSnack = new TodaySnack();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        createdSnack.setUser(user);
        createdSnack.setMenu(todaySnack.getMenu());
        createdSnack.setCalorie(todaySnack.getCalorie());

        snackService.saveSnack(createdSnack);

        // Check for input validation
        if (todaySnack.getMenu() == null || todaySnack.getMenu().isEmpty() ||
                todaySnack.getMenu().length() > 20 ||
                todaySnack.getCalorie() < 0 || todaySnack.getCalorie() > 2000) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid input");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }


        Map<String, Object> response = new HashMap<>();
        response.put("id", createdSnack.getId());
        response.put("menu", createdSnack.getMenu());
        response.put("calorie", createdSnack.getCalorie());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSnack(@PathVariable Integer id,
                                                           @RequestBody TodaySnack updatedSnack) {
        Optional<TodaySnack> existingSnackOpt = snackService.getSnackById(id);

        if (existingSnackOpt.isPresent()) {
            TodaySnack existingSnack = existingSnackOpt.get();

            // Check for input validation
            if (updatedSnack.getMenu() == null || updatedSnack.getMenu().isEmpty() ||
                    updatedSnack.getMenu().length() > 20 ||
                    updatedSnack.getCalorie() < 0 || updatedSnack.getCalorie() > 2000) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid input");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Check if the content is actually updated
            if (existingSnack.getMenu().equals(updatedSnack.getMenu()) &&
                    existingSnack.getCalorie() == updatedSnack.getCalorie()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No changes detected");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Update the existing snack
            existingSnack.setMenu(updatedSnack.getMenu());
            existingSnack.setCalorie(updatedSnack.getCalorie());
            TodaySnack updated = snackService.saveSnack(existingSnack);

            TodayMealResponse  updatedResponse= new TodayMealResponse();
            updatedResponse.setId(updated.getId());
            updatedResponse.setMenu(updated.getMenu());
            updatedResponse.setCalorie(updated.getCalorie());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "식단 간식 리스트가 수정되었습니다.");
            response.put("snack", updatedResponse);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Snack list not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSnackById(@PathVariable Integer id) {
        if (snackService.getSnackById(id).isPresent()) {
            snackService.deleteSnackById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "식단 간식 리스트가 삭제되었습니다");
            response.put("deleted_id", id); // Include deleted ID for confirmation

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Snack list not found");
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
