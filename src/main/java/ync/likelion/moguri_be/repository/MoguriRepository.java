package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.Moguri;
import ync.likelion.moguri_be.model.User;

import java.util.Optional;

public interface MoguriRepository extends JpaRepository<Moguri, Integer> {
    boolean existsByNameAndUser(String name, User user);

    Optional<Moguri> findByUser(User user);

    boolean existsByName(String name);
}
