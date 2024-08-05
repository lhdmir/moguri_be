package ync.likelion.moguri_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ync.likelion.moguri_be.model.TodayBreakfast;
import ync.likelion.moguri_be.model.TodayDinner;
import ync.likelion.moguri_be.model.TodayLunch;
import ync.likelion.moguri_be.model.TodaySnack;

@Data
@AllArgsConstructor
public class TodayMeal {
    private TodayBreakfast breakfast;
    private TodayLunch lunch;
    private TodayDinner dinner;
    private TodaySnack snack;
}
