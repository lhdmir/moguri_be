package ync.likelion.moguri_be.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoguriGrowDetailDto {
    private int id;
    private String imageUrl;
    private double targetWeight;
}
