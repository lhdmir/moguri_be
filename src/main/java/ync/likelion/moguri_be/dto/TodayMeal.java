package ync.likelion.moguri_be.dto;

import lombok.Data;
import ync.likelion.moguri_be.model.TodayBreakfast;
import ync.likelion.moguri_be.model.TodayDinner;
import ync.likelion.moguri_be.model.TodayLunch;

@Data
public class TodayMeal {
    private TodayBreakfast breakfast;
    private TodayLunch lunch;
    private TodayDinner dinner;

    public TodayMeal(TodayBreakfast breakfast, TodayLunch lunch, TodayDinner dinner) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }
}
