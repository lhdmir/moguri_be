package ync.likelion.moguri_be.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserDto {

    @NotBlank(message = "Email은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "Username은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,13}$", message = "Username은 영문 및 숫자 조합으로 13자 이내여야 합니다.")
    private String username;

    @NotBlank(message = "PW는 필수입니다.")
    @Size(min = 8, max = 13, message = "PW는 최소 8자에서 최대 13자 이내여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "PW는 영문과 숫자를 포함해야 합니다.")
    private String password;
}
