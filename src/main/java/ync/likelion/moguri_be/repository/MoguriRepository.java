package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ync.likelion.moguri_be.model.AccessoryCode;
import ync.likelion.moguri_be.model.BackgroundCode;
import ync.likelion.moguri_be.model.Moguri;
import ync.likelion.moguri_be.model.User;

import java.util.Optional;

public interface MoguriRepository extends JpaRepository<Moguri, Integer> {
    boolean existsByNameAndUser(String name, User user);

    Optional<Moguri> findByUser(User user);

    boolean existsByName(String name);

    // 현재 착용 중인 액세서리 조회
    @Query("SELECT m.currentAccessory FROM Moguri m WHERE m.user = ?1")
    Optional<AccessoryCode> findEquippedAccessoryByUser(User user);

    // 현재 배경화면 조회
    @Query("SELECT m.currentBackground FROM Moguri m WHERE m.user = ?1")
    Optional<BackgroundCode> findEquippedBackgroundByUser(User user);

    // 액세서리 착용
    @Modifying
    @Query("UPDATE Moguri m SET m.currentAccessory = ?2 WHERE m.user = ?1")
    void equipAccessory(User user, AccessoryCode accessory);

    // 배경화면 착용
    @Modifying
    @Query("UPDATE Moguri m SET m.currentBackground = ?2 WHERE m.user = ?1")
    void equipBackground(User user, BackgroundCode background);

    // 액세서리 해제
    @Modifying
    @Query("UPDATE Moguri m SET m.currentAccessory.id = 0 WHERE m.user = ?1 AND m.currentAccessory = ?2")
    void unequipAccessory(User user, AccessoryCode accessory);

    // 배경화면 해제
    @Modifying
    @Query("UPDATE Moguri m SET m.currentBackground.id = 0 WHERE m.user = ?1 AND m.currentBackground = ?2")
    void unequipBackground(User user, BackgroundCode background);
}
