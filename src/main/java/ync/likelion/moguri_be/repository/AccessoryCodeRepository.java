package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.AccessoryCode;

import java.util.List;
import java.util.Optional;

public interface AccessoryCodeRepository extends JpaRepository<AccessoryCode, Integer> {
    List<AccessoryCode> findAll(); // 모든 악세서리 코드 찾기

    // ID로 액세서리 조회
    Optional<AccessoryCode> findById(int accessoryId);

    // 특정 조건으로 액세서리 존재 여부 확인
    boolean existsById(int accessoryId);
}
