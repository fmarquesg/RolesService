package test.backend.roles.TeamPackage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    private static final String TEAMS_ENDPOINT = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/teams";
    private static final String TEAM_ENDPOINT_TEMPLATE = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/teams/%s";

    private TeamService teamService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TeamRepository teamRepository;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServer = MockRestServiceServer.createServer(restTemplate);
        teamService = new TeamService(restTemplate, teamRepository);
    }

    @Test
    public void testGetAllTeams_Success() {
        List<Team> expectedTeams = Arrays.asList(
                new Team("1", "Team 1"),
                new Team("2", "Team 2")
        );

        ResponseEntity<List<Team>> responseEntity = new ResponseEntity<>(expectedTeams, HttpStatus.OK);

        when(restTemplate.exchange(eq(TEAMS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<Team> actualTeams = teamService.getAllTeams();

        assertEquals(expectedTeams.size(), actualTeams.size());
        assertTrue(actualTeams.containsAll(expectedTeams));
        verify(teamRepository, times(1)).saveAll(expectedTeams);
    }

    @Test
    public void testGetAllTeams_NoTeamsFound() {
        ResponseEntity<List<Team>> response = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(eq(TEAMS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teamService.getAllTeams();
        });

        assertEquals("No teams found in the response.", exception.getMessage());
        verify(teamRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetAllTeams_TeamsNotFound() {
        ResponseEntity<List<Team>> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(eq(TEAMS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teamService.getAllTeams();
        });

        assertEquals("Teams not found.", exception.getMessage());
        verify(teamRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetAllTeams_FailedToRetrieveTeams() {
        ResponseEntity<List<Team>> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(eq(TEAMS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teamService.getAllTeams();
        });

        assertEquals("Failed to retrieve teams. Status code: 500", exception.getMessage());
        verify(teamRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetAllTeams_NetworkError() {
        when(restTemplate.exchange(eq(TEAMS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("Server Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teamService.getAllTeams();
        });

        assertEquals("Failed to retrieve teams due to network error: Server Error", exception.getMessage());
        verify(teamRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetTeamById_Success() {
        String teamId = "1";
        Team expectedTeam = new Team(teamId, "Team1");

        String teamEndpoint = String.format(TEAM_ENDPOINT_TEMPLATE, teamId);
        ResponseEntity<Team> responseEntity = new ResponseEntity<>(expectedTeam, HttpStatus.OK);

        when(restTemplate.getForEntity(eq(teamEndpoint), eq(Team.class)))
                .thenReturn(responseEntity);

        Team actualTeam = teamService.getTeamById(teamId);

        assertEquals(expectedTeam.getId(), actualTeam.getId());
        assertEquals(expectedTeam.getName(), actualTeam.getName());
        verify(teamRepository, times(1)).save(expectedTeam);
    }

    @Test
    public void testGetTeamById_EmptyResponse() {
        String teamId = "1";
        String teamEndpoint = String.format(TEAM_ENDPOINT_TEMPLATE, teamId);
        ResponseEntity<Team> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.getForEntity(eq(teamEndpoint), eq(Team.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teamService.getTeamById(teamId);
        });

        assertEquals("Empty response received for team with id: " + teamId, exception.getMessage());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    public void testGetTeamById_TeamNotFound() {
        String teamId = "1";
        String teamEndpoint = String.format(TEAM_ENDPOINT_TEMPLATE, teamId);
        ResponseEntity<Team> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.getForEntity(eq(teamEndpoint), eq(Team.class)))
                .thenReturn(responseEntity);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            teamService.getTeamById(teamId);
        });

        assertEquals("Team not found with id: " + teamId, exception.getMessage());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    public void testGetTeamById_FailedToRetrieveTeam() {
        String teamId = "1";
        String teamEndpoint = String.format(TEAM_ENDPOINT_TEMPLATE, teamId);
        ResponseEntity<Team> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.getForEntity(eq(teamEndpoint), eq(Team.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teamService.getTeamById(teamId);
        });

        assertEquals("Failed to retrieve team with id: " + teamId, exception.getMessage());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    public void testGetTeamById_NetworkError() {
        String teamId = "1";
        String teamEndpoint = String.format(TEAM_ENDPOINT_TEMPLATE, teamId);

        when(restTemplate.getForEntity(eq(teamEndpoint), eq(Team.class)))
                .thenThrow(new RestClientException("Server Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teamService.getTeamById(teamId);
        });

        assertEquals("Failed to retrieve team with id: " + teamId + " due to network error: Server Error", exception.getMessage());
        verify(teamRepository, never()).save(any(Team.class));
    }

}
