package ync.likelion.moguri_be.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "owned_accessories")
public class OwnedAccessories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "accessory_id", nullable = false)
    private AccessoryCode accessoryCode;
}
