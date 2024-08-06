package ync.likelion.moguri_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ync.likelion.moguri_be.model.TodaySnack;
import ync.likelion.moguri_be.repository.TodaySnackRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TodaySnackService {

    private final TodaySnackRepository todaySnackRepository;

    @Autowired
    public TodaySnackService(TodaySnackRepository todaySnackRepository) {
        this.todaySnackRepository = todaySnackRepository;
    }

    public List<TodaySnack> getAllSnacks() {
        return todaySnackRepository.findAll();
    }

    public Optional<TodaySnack> getSnackById(Integer id) {
        return todaySnackRepository.findById(id);
    }

    public TodaySnack saveSnack(TodaySnack todaySnack) {
        return todaySnackRepository.save(todaySnack);
    }

    public boolean deleteSnackById(Integer id) {
        if (todaySnackRepository.existsById(id)) {
            todaySnackRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 매일 자정에 모든 간식 목록 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void clearAllSnacks() {
        todaySnackRepository.deleteAll();
    }
}
