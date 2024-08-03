package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.Moguri;

import java.util.Optional;

public interface MoguriRepository extends JpaRepository<Moguri, Integer> {
    boolean existsByNameAndUserId(String name, int userId);

    Optional<Moguri> findByUserId(int userId);
}
