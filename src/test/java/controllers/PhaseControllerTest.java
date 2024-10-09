package controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import root.StartApplication;
import root.entities.Phase;
import root.entities.User;
import root.repositories.PhaseRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = StartApplication.class)
@AutoConfigureMockMvc
public class PhaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhaseRepo phaseRepo;

    private final String BASE_URL = "/phase";
    private final User TEST_USER = new User("Test User", "password");

    @Test
    @WithMockUser
    public void createPhaseTest() throws Exception {
        String phaseJson = "{\"phaseName\": \"Test Phase\", \"lowerStrReps\": 1, \"upperStrReps\": 5," +
                "\"lowerHypertrophyReps\": 5, \"upperHypertrophyReps\": 10}";

        mockMvc.perform(post(BASE_URL)
        .with(user(TEST_USER.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(phaseJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void createPhaseInvalidPhase() throws Exception {
        String phaseJson = "{\"phaseName\": \"Test Phase\", \"upperStrReps\": 5," +
                "\"lowerHypertrophyReps\": 5, \"upperHypertrophyReps\": 10}";

        mockMvc.perform(post(BASE_URL)
        .with(user(TEST_USER.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(phaseJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createPhaseWithoutUser() throws Exception {
        String phaseJson = "{\"phaseName\": \"Test Phase\", \"lowerStrReps\": 1, \"upperStrReps\": 5," +
                "\"lowerHypertrophyReps\": 5, \"upperHypertrophyReps\": 10}";

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(phaseJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void getPhasesTest() throws Exception {
        Phase phase1 = new Phase("Test Phase", 1, 5, 5, 10);
        Phase phase2 = new Phase("Second Phase", 2, 10, 10, 20);
        List<Phase> phases = new ArrayList<>();
        phases.add(phase1);
        phases.add(phase2);

        when(phaseRepo.findAll()).thenReturn(phases);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phaseName").value("Test Phase"));
    }

    @Test
    @WithMockUser
    public void getPhasesEmptyListTest() throws Exception {
        when(phaseRepo.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void updatePhaseTest() throws Exception {
        long phaseId = 1L;
        String phaseJson = "{\"phaseName\": \"New Phase\", \"lowerStrReps\": 5, \"upperStrReps\": 10," +
                "\"lowerHypertrophyReps\": 10, \"upperHypertrophyReps\": 20}";
        Phase phase = new Phase("Test Phase", 1, 5, 5, 10);

        when(phaseRepo.findById(phaseId)).thenReturn(Optional.of(phase));

        mockMvc.perform(put(BASE_URL + "/" + phaseId)
        .with(user(TEST_USER.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(phaseJson))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.phaseName").value("New Phase"));
    }

    @Test
    @WithMockUser
    public void updatePhaseInvalidPhaseTest() throws Exception {
        long phaseId = 1L;
        String phaseJson = "{\"lowerStrReps\": 5, \"upperStrReps\": 10," +
                "\"lowerHypertrophyReps\": 10, \"upperHypertrophyReps\": 20}";

        mockMvc.perform(put(BASE_URL + "/" + phaseId)
        .with(user(TEST_USER.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(phaseJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void updatePhaseCantFindPhase() throws Exception {
        long phaseId = 1L;
        String phaseJson = "{\"phaseName\": \"New Phase\", \"lowerStrReps\": 5, \"upperStrReps\": 10," +
                "\"lowerHypertrophyReps\": 10, \"upperHypertrophyReps\": 20}";

        when(phaseRepo.findById(phaseId)).thenReturn(Optional.empty());

        mockMvc.perform(put(BASE_URL + "/" + phaseId)
        .with(user(TEST_USER.getUsername()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(phaseJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePhaseInvalidUserTest() throws Exception {
        long phaseId = 1L;
        String phaseJson = "{\"phaseName\": \"New Phase\", \"lowerStrReps\": 5, \"upperStrReps\": 10," +
                "\"lowerHypertrophyReps\": 10, \"upperHypertrophyReps\": 20}";
        Phase phase = new Phase("Test Phase", 1, 5, 5, 10);

        when(phaseRepo.findById(phaseId)).thenReturn(Optional.of(phase));

        mockMvc.perform(put(BASE_URL + "/" + phaseId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(phaseJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void deletePhaseTest() throws Exception {
        long phaseId = 1L;
        Phase phase = new Phase("Test Phase", 1, 5, 5, 10);
        doNothing().when(phaseRepo).deleteById(phaseId);

        mockMvc.perform(delete(BASE_URL + "/" + phaseId)
        .with(user(TEST_USER.getUsername())))
                .andExpect(status().isOk());

        verify(phaseRepo, times(1)).deleteById(phaseId);
    }
}
