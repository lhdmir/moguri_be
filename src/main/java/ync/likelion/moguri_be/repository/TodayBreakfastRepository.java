package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodayBreakfast;

import java.util.List;

public interface TodayBreakfastRepository extends JpaRepository<TodayBreakfast, Integer> {
    List<TodayBreakfast> findByUserId(int userId);
}
