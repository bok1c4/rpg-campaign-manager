package rs.ac.singidunum.rpg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CampaignDtos {

    public record CreateRequest(
            @NotBlank @Size(max = 150) String title,
            String description,
            String setting) {
    }

    public record UpdateRequest(
            @NotBlank @Size(max = 150) String title,
            String description,
            String setting) {
    }

    public record Response(
            Long id,
            String title,
            String description,
            String setting,
            UserDtos.Response gameMaster,
            int characterCount,
            int sessionCount,
            List<UserDtos.Response> players) {
    }
}
