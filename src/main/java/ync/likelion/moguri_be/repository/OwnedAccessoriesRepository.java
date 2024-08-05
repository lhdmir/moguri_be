package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.OwnedAccessories;
import ync.likelion.moguri_be.model.User;

import java.util.List;

public interface OwnedAccessoriesRepository extends JpaRepository<OwnedAccessories, Integer> {
    List<OwnedAccessories> findByUser(User user); // 사용자 ID로 소유한 악세서리 찾기
}
