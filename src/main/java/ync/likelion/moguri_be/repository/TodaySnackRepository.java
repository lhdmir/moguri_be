package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodaySnack;

import java.util.List;

public interface TodaySnackRepository extends JpaRepository<TodaySnack, Integer> {
    List<TodaySnack> findByUserId(int userId);
    List<TodaySnack> findAllByUserId(int userId);
}
