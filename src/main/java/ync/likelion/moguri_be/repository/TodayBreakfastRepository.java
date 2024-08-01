package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodayBreakfast;

public interface TodayBreakfastRepository extends JpaRepository<TodayBreakfast, Integer> {
    TodayBreakfast findByUserId(int userId);
}
