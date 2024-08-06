package ync.likelion.moguri_be.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ync.likelion.moguri_be.dto.TodayExerciseResponse;
import ync.likelion.moguri_be.dto.TodayExercisedto;
import ync.likelion.moguri_be.model.TodayExercise;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.TodayExerciseRepository;
import ync.likelion.moguri_be.service.TodayExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ync.likelion.moguri_be.service.UserService;

@RestController
@RequestMapping("/api/exercise")
public class TodayExerciseController {

    private final TodayExerciseService exerciseService;
    private final UserService userService;
    private final TodayExerciseRepository todayExerciseRepository;

    @Autowired
    public TodayExerciseController(TodayExerciseService exerciseService, UserService userService, TodayExerciseRepository todayExerciseRepository) {
        this.exerciseService = exerciseService;
        this.userService = userService;
        this.todayExerciseRepository = todayExerciseRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExercises(
            @Parameter(description = "Authorization 헤더", required = true)
            @RequestHeader("Authorization") String authorization) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // 토큰에서 사용자 이름 추출
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<TodayExercise> exercises = todayExerciseRepository.findAllByUserId(user.getId());
        List<TodayExerciseResponse> exerciseResponses = exercises.stream().map(exercise -> {
            TodayExerciseResponse response = new TodayExerciseResponse();
            response.setId(exercise.getId());
            response.setContent(exercise.getExerciseContent());
            return response;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "운동 리스트 조회를 성공했습니다.");
        response.put("todayExercise", exerciseResponses);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TodayExerciseResponse> createExercise(@Valid @RequestHeader("Authorization") String authorization, @RequestBody TodayExercisedto todayExerciseDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // 토큰에서 사용자 이름 추출
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        TodayExercise todayExercise = new TodayExercise();
        todayExercise.setExerciseContent(todayExerciseDTO.getContent());
        todayExercise.setUser(user); // 사용자 이름 설정

        TodayExercise createdExercise = exerciseService.saveExercise(todayExercise);
        TodayExerciseResponse todayExerciseResponse = new TodayExerciseResponse();
        todayExerciseResponse.setId(createdExercise.getId());
        todayExerciseResponse.setContent(todayExerciseDTO.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(todayExerciseResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateExercise(
            @PathVariable int id,
            @RequestBody TodayExercisedto updatedExerciseDto) {

        // ID로 기존 운동 항목을 찾습니다.
        List<TodayExercise> existingExerciseList = todayExerciseRepository.findById(id);

        System.out.println(updatedExerciseDto);
        System.out.println(updatedExerciseDto.getContent());
        System.out.println(existingExerciseList);

        if (existingExerciseList == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "해당 운동 리스트를 찾을 수 없습니다.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // 리스트가 비어 있지 않으므로 첫 번째 요소를 가져옵니다.
        TodayExercise existingExercise = existingExerciseList.get(0);

        // DTO의 content 값을 가져옵니다.
        String updatedContent = updatedExerciseDto.getContent();

        // 유효성 검사
        if (updatedContent == null || updatedContent.isEmpty() || updatedContent.length() > 50) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "유효하지 않은 입력입니다.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // 변경 사항이 있는지 확인
        if (existingExercise.getExerciseContent().equals(updatedContent)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "No changes detected");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // 내용 업데이트
        existingExercise.setExerciseContent(updatedContent);
        TodayExercise updated = exerciseService.saveExercise(existingExercise);

        // DTO로 변환
        TodayExerciseResponse responseDto = new TodayExerciseResponse();
        responseDto.setId(updated.getId());
        responseDto.setContent(updated.getExerciseContent());

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("id", updated.getId());
        response.put("content", updated.getExerciseContent());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteExerciseById(@PathVariable Integer id) {
        if (todayExerciseRepository.findById(id).isPresent()) {
            exerciseService.deleteExerciseById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "운동 리스트가 삭제되었습니다.");
            response.put("deleted_id", id);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "해당 리스트를 찾을 수 없습니다.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

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
