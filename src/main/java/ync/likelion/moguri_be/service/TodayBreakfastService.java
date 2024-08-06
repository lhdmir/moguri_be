package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.TodayBreakfast;
import ync.likelion.moguri_be.repository.TodayBreakfastRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TodayBreakfastService {

    private final TodayBreakfastRepository todayBreakfastRepository;

    @Autowired
    public TodayBreakfastService(TodayBreakfastRepository todayBreakfastRepository) {
        this.todayBreakfastRepository = todayBreakfastRepository;
    }

    public List<TodayBreakfast> getAllBreakfasts() {
        return todayBreakfastRepository.findAll();
    }

    public Optional<TodayBreakfast> getBreakfastById(Integer id) {
        return todayBreakfastRepository.findById(id);
    }

    public TodayBreakfast saveBreakfast(TodayBreakfast todayBreakfast) {
        return todayBreakfastRepository.save(todayBreakfast);
    }

    public boolean deleteBreakfastById(Integer id) {
        if (todayBreakfastRepository.existsById(id)) {
            todayBreakfastRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 매일 자정에 모든 식단 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void clearAllBreakfasts() {
        todayBreakfastRepository.deleteAll();
    }
}
