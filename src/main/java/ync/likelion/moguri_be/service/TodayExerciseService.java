package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.TodayExercise;
import ync.likelion.moguri_be.repository.TodayExerciseRepository;

import java.util.List;
@Service
public class TodayExerciseService {

    private final TodayExerciseRepository todayExerciseRepository;

    @Autowired
    public TodayExerciseService(TodayExerciseRepository todayExerciseRepository) {
        this.todayExerciseRepository = todayExerciseRepository;
    }

    public List<TodayExercise> getAllExercises() {
        return todayExerciseRepository.findAll();
    }

    public List<TodayExercise> getExerciseById(int id) {
        return todayExerciseRepository.findByUserId(id);
    }

    public TodayExercise saveExercise(TodayExercise todayExercise) {
        return todayExerciseRepository.save(todayExercise);
    }

    public boolean deleteExerciseById(int id) {
        if (todayExerciseRepository.existsById(id)) {
            todayExerciseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 매일 00시에 모든 운동 리스트 초기화 (삭제)
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetAllExercises() {
        todayExerciseRepository.deleteAll();
    }

    public List<TodayExercise> getExerciseByIdAndUserId(int id, int id1) {
        return todayExerciseRepository.findAllByIdAndUserId(id, id1);
    }
}
