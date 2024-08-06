package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.TodayDinner;
import ync.likelion.moguri_be.repository.TodayDinnerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TodayDinnerService {

    private final TodayDinnerRepository todayDinnerRepository;

    @Autowired
    public TodayDinnerService(TodayDinnerRepository todayDinnerRepository) {
        this.todayDinnerRepository = todayDinnerRepository;
    }

    public List<TodayDinner> getAllDinners() {
        return todayDinnerRepository.findAll();
    }

    public Optional<TodayDinner> getDinnerById(Integer id) {
        return todayDinnerRepository.findById(id);
    }

    public TodayDinner saveDinner(TodayDinner todayDinner) {
        return todayDinnerRepository.save(todayDinner);
    }

    public boolean deleteDinnerById(Integer id) {
        if (todayDinnerRepository.existsById(id)) {
            todayDinnerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 매일 자정에 모든 저녁 식단 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void clearAllDinners() {
        todayDinnerRepository.deleteAll();
    }
}
