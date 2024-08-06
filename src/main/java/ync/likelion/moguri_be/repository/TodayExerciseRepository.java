package ync.likelion.moguri_be.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.TodayExercise;

import java.util.List;

public interface TodayExerciseRepository extends JpaRepository<TodayExercise, Integer> {
    List<TodayExercise> findByUserId(int userId);
    List<TodayExercise> findById(int id);

    List<TodayExercise> findAllByIdAndUserId(int id, int id1);
}