package ync.likelion.moguri_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackgroundRequest {
    private Background background;

    @Getter
    @Setter
    public static class Background {
        private int id;
        private String name;
        private String imageUrl;
    }
}
