package ync.likelion.moguri_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessoryRequest {
    private Accessory accessory;

    @Getter
    @Setter
    public static class Accessory {
        private int id;
        private String name;
        private String imageUrl;
    }
}
