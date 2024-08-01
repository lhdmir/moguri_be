package ync.likelion.moguri_be.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "moguri")
public class Moguri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User와의 관계

    @ManyToOne
    @JoinColumn(name = "moguri_code_id", nullable = false)
    private MoguriCode moguriCode; // MoguriCode와의 관계


    @Column(name = "name", nullable = false, length = 30)
    private String name;


    @ManyToOne
    @JoinColumn(name = "current_accessory_id")
    private AccessoryCode currentAccessory; // 현재 액세서리

    @ManyToOne
    @JoinColumn(name = "current_background_id")
    private BackgroundCode currentBackground; // 현재 배경
}
