package ync.likelion.moguri_be.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
