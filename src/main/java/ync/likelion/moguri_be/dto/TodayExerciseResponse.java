package ync.likelion.moguri_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayExerciseResponse {
    private int id;
    private String content;
}

