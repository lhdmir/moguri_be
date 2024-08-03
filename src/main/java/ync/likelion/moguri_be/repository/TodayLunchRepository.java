package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodayLunch;

public interface TodayLunchRepository extends JpaRepository<TodayLunch, Integer> {
    TodayLunch findByUserId(int userId);
}
