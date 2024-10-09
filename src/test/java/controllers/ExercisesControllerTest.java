package controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import root.StartApplication;
import root.repositories.ExercisesRepo;
import root.entities.Exercise;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/********************************************** # CHAT GIPPITY'S TESTS # **********************************************/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = StartApplication.class)
@AutoConfigureMockMvc
@WithMockUser
public class ExercisesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExercisesRepo exercisesRepo;

    @Test
    public void testCreateExercise() throws Exception {
        Exercise exercise = new Exercise();
        exercise.setName("Push-up");
        exercise.setMuscleGroup("Chest");

        when(exercisesRepo.save(any(Exercise.class))).thenReturn(exercise);

        mockMvc.perform(post("/exercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Push-up\", \"muscleGroup\": \"Chest\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Push-up"))
                .andExpect(jsonPath("$.muscleGroup").value("Chest"));
    }

    @Test
    public void testGetAllExercises() throws Exception {
        List<Exercise> exercises = Arrays.asList(
                new Exercise("Push-up", "Chest"),
                new Exercise("Squat", "Legs")
        );

        when(exercisesRepo.findAll()).thenReturn(exercises);

        mockMvc.perform(get("/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Push-up"))
                .andExpect(jsonPath("$[0].muscleGroup").value("Chest"))
                .andExpect(jsonPath("$[1].name").value("Squat"));
    }

    @Test
    public void testGetExerciseById() throws Exception {
        Exercise exercise = new Exercise("Push-up", "Chest");

        when(exercisesRepo.findById(1L)).thenReturn(Optional.of(exercise));

        mockMvc.perform(get("/exercises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Push-up"))
                .andExpect(jsonPath("$.muscleGroup").value("Chest"));
    }

    @Test
    public void testUpdateExercise() throws Exception {
        Exercise existingExercise = new Exercise("Push-up", "Chest");
        Exercise updatedExercise = new Exercise("Pull-up", "Back");

        when(exercisesRepo.findById(1L)).thenReturn(Optional.of(existingExercise));
        when(exercisesRepo.save(any(Exercise.class))).thenReturn(updatedExercise);

        mockMvc.perform(put("/exercises/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Pull-up\", \"muscleGroup\": \"Back\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pull-up"))
                .andExpect(jsonPath("$.muscleGroup").value("Back"));
    }

    @Test
    public void testDeleteExercise() throws Exception {
        doNothing().when(exercisesRepo).deleteById(1L);

        mockMvc.perform(delete("/exercises/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteExerciseNonExistentId() throws Exception {
        // Simulate the behavior when the ID does not exist
        doThrow(new EmptyResultDataAccessException(1)).when(exercisesRepo).deleteById(999L);

        // Perform DELETE request and expect 404 Not Found
        ResultActions r = mockMvc.perform(delete("/exercises/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Exercise not found"));  // Optional: Check the response body
    }

    @Test
    public void testGetExerciseByNonExistingId() throws Exception {
        when(exercisesRepo.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/exercises/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateExerciseWithInvalidData() throws Exception {
        mockMvc.perform(post("/exercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"muscleGroup\": \"Chest\"}"))
                .andExpect(status().isBadRequest());
    }
}
