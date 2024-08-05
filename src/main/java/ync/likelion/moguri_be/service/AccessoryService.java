package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.AccessoryCode;
import ync.likelion.moguri_be.model.OwnedAccessories;
import ync.likelion.moguri_be.model.User;
import ync.likelion.moguri_be.repository.AccessoryCodeRepository;
import ync.likelion.moguri_be.repository.OwnedAccessoriesRepository;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AccessoryService {

    @Autowired
    private AccessoryCodeRepository accessoryCodeRepository; // 악세서리 코드 레포지토리
    @Autowired
    private OwnedAccessoriesRepository ownedAccessoriesRepository; // 소유한 악세서리 레포지토리

    // 사용자가 보유한 악세서리 목록을 가져오는 메서드
    public List<AccessoryCode> getUserAccessories(User user) {
        List<OwnedAccessories> ownedAccessories = ownedAccessoriesRepository.findByUser(user);
        return ownedAccessories.stream()
                .map(OwnedAccessories::getAccessoryCode)
                .collect(Collectors.toList());
    }

    // 모든 악세서리 목록을 가져오는 메서드
    public List<AccessoryCode> getAllAccessories() {
        return accessoryCodeRepository.findAll(); // 모든 악세서리 반환
    }

    // 사용자가 보유하지 않은 랜덤한 악세서리를 선택하는 메서드
    public AccessoryCode drawRandomAccessory(List<AccessoryCode> userAccessories) {
        List<AccessoryCode> allAccessories = getAllAccessories();

        // 사용자가 보유하지 않은 악세서리 목록 필터링
        List<AccessoryCode> availableAccessories = allAccessories.stream()
                .filter(accessory -> !userAccessories.contains(accessory))
                .toList();

        // 랜덤하게 선택
        if (availableAccessories.isEmpty()) {
            throw new RuntimeException("No available accessories to draw");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(availableAccessories.size());
        return availableAccessories.get(randomIndex);
    }

    // 선택된 악세서리를 사용자의 인벤토리에 추가하는 메서드
    public void addAccessoryToUser(int userId, AccessoryCode accessoryCode) {
        OwnedAccessories ownedAccessory = new OwnedAccessories();
        ownedAccessory.setUser(new User(userId)); // 사용자 설정
        ownedAccessory.setAccessoryCode(accessoryCode); // 악세서리 설정
        ownedAccessoriesRepository.save(ownedAccessory); // 소유한 악세서리 저장
    }
}
