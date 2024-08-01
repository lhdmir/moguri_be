package ync.likelion.moguri_be.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "moguri_codes")
public class MoguriCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
