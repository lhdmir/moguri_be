package ync.likelion.moguri_be.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "today_dinner")
public class TodayDinner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "menu", length = 30)
    private String menu;

    @Column(name = "calorie", nullable = false)
    private Integer calorie;
}
