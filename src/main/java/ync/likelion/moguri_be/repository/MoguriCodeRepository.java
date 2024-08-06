package ync.likelion.moguri_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ync.likelion.moguri_be.model.MoguriCode;

import java.util.Optional;

public interface MoguriCodeRepository extends JpaRepository<MoguriCode, Integer> {
    Optional<MoguriCode> findByImageUrl(String imageUrl);
    Optional<MoguriCode> findById(int id);
}
