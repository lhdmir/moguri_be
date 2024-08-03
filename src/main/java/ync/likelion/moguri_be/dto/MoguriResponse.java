package ync.likelion.moguri_be.dto;

import lombok.Data;
import ync.likelion.moguri_be.model.Moguri;

@Data
public class MoguriResponse {
    private String message;
    private MoguriDto moguri;
}