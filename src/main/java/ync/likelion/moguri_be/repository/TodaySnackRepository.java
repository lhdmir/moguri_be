package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodaySnack;

public interface TodaySnackRepository extends JpaRepository<TodaySnack, Integer> {
    TodaySnack findByUserId(int userId);
}
