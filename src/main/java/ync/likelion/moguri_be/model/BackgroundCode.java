package ync.likelion.moguri_be.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "background_codes")
public class BackgroundCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
