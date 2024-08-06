package ync.likelion.moguri_be.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ync.likelion.moguri_be.dto.TodayMealDto;
import ync.likelion.moguri_be.dto.TodayMealResponse;
import ync.likelion.moguri_be.model.TodayBreakfast;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.TodayBreakfastRepository;
import ync.likelion.moguri_be.service.TodayBreakfastService;
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
@RequestMapping("/api/breakfast")
public class TodayBreakfastController {

    private final TodayBreakfastService breakfastService;
    private final TodayBreakfastRepository todayBreakfastRepository;
    private final UserService userService;

    @Autowired
    public TodayBreakfastController(TodayBreakfastService breakfastService, TodayBreakfastRepository todayBreakfastRepository, UserService userService) {
        this.breakfastService = breakfastService;
        this.todayBreakfastRepository = todayBreakfastRepository;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBreakfasts(@RequestHeader("Authorization") String authorization) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

//        List<TodayExerciseResponse> exerciseResponses = exercises.stream().map(exercise -> {
//            TodayExerciseResponse response = new TodayExerciseResponse();
//            response.setId(exercise.getId());
//            response.setExerciseContent(exercise.getExerciseContent());
//            return response;
//        }).collect(Collectors.toList());

        List<TodayBreakfast> breakfasts = todayBreakfastRepository.findAllByUserId(user.getId());
        List<TodayMealResponse> breakfastResponse = breakfasts.stream().map(breakfast -> {
            TodayMealResponse response = new TodayMealResponse();
            response.setId(breakfast.getId());
            response.setMenu(breakfast.getMenu());
            response.setCalorie(breakfast.getCalorie());
            return response;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "아침 식단 리스트 조회를 성공했습니다.");
        response.put("breakfasts", breakfastResponse);

        return ResponseEntity.ok(response);
    }



    @PostMapping
    public ResponseEntity<Map<String, Object>> createBreakfast(@RequestHeader("Authorization") String authorization,  @RequestBody TodayMealDto todayBreakfast) {
        TodayBreakfast createdBreakfast = new TodayBreakfast();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        createdBreakfast.setUser(user);
        createdBreakfast.setMenu(todayBreakfast.getMenu());
        createdBreakfast.setCalorie(todayBreakfast.getCalorie());

        breakfastService.saveBreakfast(createdBreakfast);

        Map<String, Object> response = new HashMap<>();

        response.put("id", createdBreakfast.getId());
        response.put("menu", createdBreakfast.getMenu());
        response.put("calorie", createdBreakfast.getCalorie());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBreakfast(@PathVariable Integer id, @RequestBody TodayMealDto updatedBreakfast) {
        Optional<TodayBreakfast> existingBreakfastOpt = breakfastService.getBreakfastById(id);

        if (existingBreakfastOpt.isPresent()) {
            TodayBreakfast existingBreakfast = existingBreakfastOpt.get();

            // Check for input validation
            if (updatedBreakfast.getMenu() == null || updatedBreakfast.getMenu().isEmpty() ||
                    updatedBreakfast.getMenu().length() > 20 ||
                    updatedBreakfast.getCalorie() < 0 || updatedBreakfast.getCalorie() > 2000) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid input");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Check if the content is actually updated
            if (existingBreakfast.getMenu().equals(updatedBreakfast.getMenu()) &&
                    existingBreakfast.getCalorie() == updatedBreakfast.getCalorie()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No changes detected");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Update the existing breakfast
            existingBreakfast.setMenu(updatedBreakfast.getMenu());
            existingBreakfast.setCalorie(updatedBreakfast.getCalorie());
            TodayBreakfast updated = breakfastService.saveBreakfast(existingBreakfast);

            TodayMealResponse  updatedResponse= new TodayMealResponse();
//            updatedResponse.setId(updated.getId());
//            updatedResponse.setMenu(updated.getMenu());
//            updatedResponse.setCalorie(updated.getCalorie());

            Map<String, Object> response = new HashMap<>();
            response.put("id", updated.getId());
            response.put("menu", updated.getMenu());
            response.put("calorie", updated.getCalorie());

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Breakfast list not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBreakfastById(@PathVariable Integer id) {
        if (breakfastService.getBreakfastById(id).isPresent()) {
            breakfastService.deleteBreakfastById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "식단 아침 리스트가 삭제되었습니다");
            response.put("deleted_id", id); // Include deleted ID for confirmation

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Breakfast list not found");
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
