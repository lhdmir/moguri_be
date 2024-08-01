package ync.likelion.moguri_be.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodayMeal {
    private List<Meal> breakfast; // 아침 식사
    private List<Meal> lunch; // 점심 식사
    private List<Meal> dinner; // 저녁 식사
    private List<Meal> snack; // 간식

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meal {
        private int id; // 식사 ID
        private String menu; // 메뉴 이름
        private int calorie; // 칼로리
    }
}
