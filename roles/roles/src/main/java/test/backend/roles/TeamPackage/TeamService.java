package test.backend.roles.TeamPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TeamService {
    private static final String TEAMS_ENDPOINT = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/teams";
    private static final String TEAM_ENDPOINT_TEMPLATE = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/teams/%s";

    private final RestTemplate restTemplate;
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(RestTemplate restTemplate, TeamRepository teamRepository) {
        this.restTemplate = restTemplate;
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        try {
            ResponseEntity<List<Team>> response = restTemplate.exchange(
                    TEAMS_ENDPOINT,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Team>>() {
                    }
            );

            int statusCodeValue = response.getStatusCode().value();
            HttpStatus statusCode = HttpStatus.valueOf(statusCodeValue);
            if (statusCode == HttpStatus.OK) {
                List<Team> teams = response.getBody();

                if (teams == null || teams.isEmpty()) {
                    throw new RuntimeException("No teams found in the response.");
                }

                teamRepository.saveAll(teams);
                return teams;
            } else if (statusCode == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Teams not found.");
            } else {
                throw new RuntimeException("Failed to retrieve teams. Status code: " + statusCode.value());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to retrieve teams due to network error: " + e.getMessage());
        }
    }

    public Team getTeamById(String id) {
        String teamEndpoint = String.format(TEAM_ENDPOINT_TEMPLATE, id);

        try {
            ResponseEntity<Team> response = restTemplate.getForEntity(teamEndpoint, Team.class);

            int statusCodeValue = response.getStatusCode().value();
            HttpStatus statusCode = HttpStatus.valueOf(statusCodeValue);
            if (statusCode == HttpStatus.OK) {
                Team team = response.getBody();

                if (team == null) {
                    throw new RuntimeException("Empty response received for team with id: " + id);
                }

                teamRepository.save(team);
                return team;
            } else if (statusCode == HttpStatus.NOT_FOUND) {
                throw new NoSuchElementException("Team not found with id: " + id);
            } else {
                throw new RuntimeException("Failed to retrieve team with id: " + id);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to retrieve team with id: " + id + " due to network error: " + e.getMessage());
        }
    }
}
