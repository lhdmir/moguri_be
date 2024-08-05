package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.AccessoryCode;

import java.util.List;

public interface AccessoryCodeRepository extends JpaRepository<AccessoryCode, Integer> {
    List<AccessoryCode> findAll(); // 모든 악세서리 코드 찾기
}
