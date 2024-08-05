package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.OwnedBackground;
import ync.likelion.moguri_be.model.User;

import java.util.List;

public interface OwnedBackgroundRepository extends JpaRepository<OwnedBackground, Integer> {
    List<OwnedBackground> findByUser(User user); // 사용자 ID로 소유한 악세서리 찾기
}
