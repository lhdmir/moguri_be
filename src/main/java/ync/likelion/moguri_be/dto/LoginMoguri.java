package ync.likelion.moguri_be.dto;

import lombok.Data;
import ync.likelion.moguri_be.model.AccessoryCode;
import ync.likelion.moguri_be.model.BackgroundCode;
@Data
public class LoginMoguri {
    private int id;
    private String imageUrl;
    private String name;
    private CurrentItem currentItem;
    private OwnedItems ownedItem;
    private int targetWeight;
    @Data
    public static class CurrentItem {
        private AccessoryCode accessory;
        private BackgroundCode background;
    }

}
