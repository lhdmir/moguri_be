package ync.likelion.moguri_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ync.likelion.moguri_be.model.TodayBreakfast;
import ync.likelion.moguri_be.model.TodayDinner;
import ync.likelion.moguri_be.model.TodayLunch;
import ync.likelion.moguri_be.model.TodaySnack;

import java.util.List;

@Data
@AllArgsConstructor
public class TodayMeal {
    private List<TodayBreakfast> breakfast;
    private List<TodayLunch> lunch;
    private List<TodayDinner> dinner;
    private List<TodaySnack> snack;
}
