package ync.likelion.moguri_be.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodayDinner;

import java.util.List;

public interface TodayDinnerRepository extends JpaRepository<TodayDinner, Integer> {
    List<TodayDinner> findByUserId(int userId);
    List<TodayDinner> findAllByUserId(int userId);
}
