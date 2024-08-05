package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.BackgroundCode;
import ync.likelion.moguri_be.model.AccessoryCode;
import ync.likelion.moguri_be.model.OwnedBackground;
import ync.likelion.moguri_be.model.OwnedAccessories;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.BackgroundCodeRepository;
import ync.likelion.moguri_be.repository.AccessoryCodeRepository;
import ync.likelion.moguri_be.repository.OwnedBackgroundRepository;
import ync.likelion.moguri_be.repository.OwnedAccessoriesRepository;

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
}
