package ync.likelion.moguri_be.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodayDinner;

public interface TodayDinnerRepository extends JpaRepository<TodayDinner, Integer> {
    TodayDinner findByUserId(int userId);
}
