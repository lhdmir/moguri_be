package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.BackgroundCode;

import java.util.List;

public interface BackgroundCodeRepository extends JpaRepository<BackgroundCode, Integer> {
    List<BackgroundCode> findAll(); // 모든 악세서리 코드 찾기
}
