package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.TodayLunch;
import ync.likelion.moguri_be.repository.TodayLunchRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TodayLunchService {

    private final TodayLunchRepository todayLunchRepository;

    @Autowired
    public TodayLunchService(TodayLunchRepository todayLunchRepository) {
        this.todayLunchRepository = todayLunchRepository;
    }

    public List<TodayLunch> getAllLunches() {
        return todayLunchRepository.findAll();
    }

    public Optional<TodayLunch> getLunchById(Integer id) {
        return todayLunchRepository.findById(id);
    }

    public TodayLunch saveLunch(TodayLunch todayLunch) {
        return todayLunchRepository.save(todayLunch);
    }

    public boolean deleteLunchById(Integer id) {
        if (todayLunchRepository.existsById(id)) {
            todayLunchRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 매일 자정에 모든 점심 식단 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void clearAllLunches() {
        todayLunchRepository.deleteAll();
    }
}
