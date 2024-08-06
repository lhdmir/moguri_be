package ync.likelion.moguri_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OwnedItems {
    private List<AccessoryCode> accessory; // 액세서리 리스트
    private List<BackgroundCode> background; // 배경 리스트

    @Getter
    @Setter
    public static class AccessoryCode {
        private int id;
        private String name;
        private String imageUrl;
    }

    @Getter
    @Setter
    public static class BackgroundCode {
        private int id;
        private String name;
        private String imageUrl;
    }
}
