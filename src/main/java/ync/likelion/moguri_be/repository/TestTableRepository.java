package ync.likelion.moguri_be.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ync.likelion.moguri_be.model.TestTable;

@Repository
public interface TestTableRepository extends JpaRepository<TestTable, Integer> {
}
