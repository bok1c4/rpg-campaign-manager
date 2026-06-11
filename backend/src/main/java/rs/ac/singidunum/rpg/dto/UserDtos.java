package rs.ac.singidunum.rpg.dto;

public class UserDtos {

    public record Response(
            Long id,
            String username,
            String email,
            String role) {
    }
}
