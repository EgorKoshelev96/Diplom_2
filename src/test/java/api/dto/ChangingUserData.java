package api.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ChangingUserData {
    private String email;
    private String password;
    private String name;
}

