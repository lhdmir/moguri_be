package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodayLunch;

import java.util.List;

public interface TodayLunchRepository extends JpaRepository<TodayLunch, Integer> {
    List<TodayLunch> findByUserId(int userId);
    List<TodayLunch> findAllByUserId(int userId);
}
