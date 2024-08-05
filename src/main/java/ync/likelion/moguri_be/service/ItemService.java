package ync.likelion.moguri_be.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.BackgroundCode;
import ync.likelion.moguri_be.model.AccessoryCode;
import ync.likelion.moguri_be.model.OwnedBackground;
import ync.likelion.moguri_be.model.OwnedAccessories;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private AccessoryCodeRepository accessoryCodeRepository;

    @Autowired
    private BackgroundCodeRepository backgroundCodeRepository;

    @Autowired
    private OwnedAccessoriesRepository ownedAccessoriesRepository;

    @Autowired
    private OwnedBackgroundRepository ownedBackgroundRepository;

    @Autowired
    private MoguriRepository moguriRepository;

    // 사용자가 보유한 아이템 목록을 가져오는 메서드
    public List<AccessoryCode> getUserAccessoryItems(User user) {
        List<OwnedAccessories> ownedAccessories = ownedAccessoriesRepository.findByUser(user);
        return ownedAccessories.stream()
                .map(OwnedAccessories::getAccessoryCode)
                .collect(Collectors.toList());
    }

    public List<BackgroundCode> getUserBackgroundItems(User user) {
        List<OwnedBackground> ownedBackgrounds = ownedBackgroundRepository.findByUser(user);
        return ownedBackgrounds.stream()
                .map(OwnedBackground::getBackgroundCode)
                .collect(Collectors.toList());
    }

    // 모든 아이템 목록을 가져오는 메서드
    public List<AccessoryCode> getAllAccessoryItems() {
        return accessoryCodeRepository.findAll();
    }

    public List<BackgroundCode> getAllBackgroundItems() {
        return backgroundCodeRepository.findAll();
    }

    // 랜덤 아이템 선택하는 메서드
    public AccessoryCode drawRandomAccessoryItem(List<AccessoryCode> userItems) {
        List<AccessoryCode> allItems = getAllAccessoryItems();
        return getRandomItem(userItems, allItems);
    }

    public BackgroundCode drawRandomBackgroundItem(List<BackgroundCode> userItems) {
        List<BackgroundCode> allItems = getAllBackgroundItems();
        return getRandomItem(userItems, allItems);
    }

    private <T> T getRandomItem(List<T> userItems, List<T> allItems) {
        List<T> availableItems = allItems.stream()
                .filter(item -> !userItems.contains(item))
                .toList();

        if (availableItems.isEmpty()) {
            throw new RuntimeException("No available items to draw");
        }

        Random random = new Random();
        return availableItems.get(random.nextInt(availableItems.size()));
    }

    // 선택된 아이템을 사용자의 인벤토리에 추가하는 메서드
    public void addAccessoryItemToUser(int userId, AccessoryCode item) {
        OwnedAccessories ownedAccessory = new OwnedAccessories();
        ownedAccessory.setUser(new User(userId));
        ownedAccessory.setAccessoryCode(item);
        ownedAccessoriesRepository.save(ownedAccessory);
    }

    public void addBackgroundItemToUser(int userId, BackgroundCode item) {
        OwnedBackground ownedBackground = new OwnedBackground();
        ownedBackground.setUser(new User(userId));
        ownedBackground.setBackgroundCode(item);
        ownedBackgroundRepository.save(ownedBackground);
    }



    public AccessoryCode getCurrentEquippedAccessory(User user) {
        // 현재 착용 중인 아이템을 반환하는 로직
        return moguriRepository.findEquippedAccessoryByUser(user).orElse(null);
    }

    public AccessoryCode getAccessoryById(int accessoryId) {
        // 액세서리 ID로 액세서리 조회 (AccessoryRepository 필요)
        return accessoryCodeRepository.findById(accessoryId).orElse(null);
    }

    @Transactional
    public void equipAccessory(User user, AccessoryCode accessory) {
        // 아이템 착용 로직
        moguriRepository.equipAccessory(user, accessory);
    }

    @Transactional
    public void unequipAccessory(User user, AccessoryCode accessory) {
        // 아이템 해제 로직
        moguriRepository.unequipAccessory(user, accessory);
    }
    public BackgroundCode getCurrentEquippedBackground(User user) {
        // 현재 착용 중인 배경화면을 반환하는 로직
        return moguriRepository.findEquippedBackgroundByUser(user).orElse(null);
    }

    public BackgroundCode getBackgroundById(int backgroundId) {
        // 배경화면 ID로 배경화면 조회
        return backgroundCodeRepository.findById(backgroundId).orElse(null);
    }

    @Transactional
    public void equipBackground(User user, BackgroundCode background) {
        // 배경화면 착용 로직
        moguriRepository.equipBackground(user, background);
    }

    @Transactional
    public void unequipBackground(User user, BackgroundCode background) {
        // 배경화면 해제 로직
        moguriRepository.unequipBackground(user, background);
    }

    // 배경화면을 설정 또는 해제하는 메서드
    @Transactional
    public BackgroundCode toggleBackground(User user, BackgroundCode backgroundCode) {
        BackgroundCode currentBackground = getCurrentEquippedBackground(user);

        if (currentBackground != null && currentBackground.getId() == backgroundCode.getId()) {
            unequipBackground(user, backgroundCode);
            return new BackgroundCode(0, "", ""); // 해제된 경우 빈 배경화면 반환
        }

        equipBackground(user, backgroundCode);
        return backgroundCode; // 설정된 배경화면 반환
    }
}
