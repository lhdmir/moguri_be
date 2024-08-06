package ync.likelion.moguri_be.dto;

import lombok.Data;

@Data
public class TodayMealResponse {
    private int id;
    private String menu;
    private int calorie;
}
