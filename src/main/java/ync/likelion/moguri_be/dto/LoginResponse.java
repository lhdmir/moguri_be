package ync.likelion.moguri_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ync.likelion.moguri_be.model.TodayExercise;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token; // JWT 토큰
    private Date cookieExpirationTime; // 쿠키 만료 시간
    private LoginMoguri moguri; // Moguri 객체
    private TodayMeal todayMeal; // 오늘의 식사 정보
    private List<TodayExercise> todayExercise; // 오늘의 운동 정보
}
