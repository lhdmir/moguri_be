package ync.likelion.moguri_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ync.likelion.moguri_be.model.Moguri;
import ync.likelion.moguri_be.model.TodayExercise;
import ync.likelion.moguri_be.dto.TodayMeal;
import ync.likelion.moguri_be.model.User;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token; // JWT 토큰
    private Date cookieExpirationTime; // 쿠키 만료 시간
    private String username; // 사용자 이름
    private String email; // 사용자 이메일
    private Moguri moguri; // Moguri 객체
    private TodayMeal todayMeal; // 오늘의 식사 정보
    private List<TodayExercise> todayExercise; // 오늘의 운동 정보

    public LoginResponse(String token, Date cookieExpirationTime, Moguri moguri, TodayMeal todayMeal, List<TodayExercise> todayExercise) {
        this.token = token;
        this.cookieExpirationTime = cookieExpirationTime;
        this.moguri = moguri;
        this.todayMeal = todayMeal;
        this.todayExercise = todayExercise;
    }
}
