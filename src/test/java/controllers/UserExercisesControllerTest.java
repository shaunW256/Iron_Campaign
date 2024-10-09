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
import root.entities.User;
import root.entities.UserExercise;
import root.repositories.UserExerciseRepo;
import root.repositories.UserRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = StartApplication.class)
@AutoConfigureMockMvc
public class UserExercisesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private UserExerciseRepo userExerciseRepo;

    private final User TEST_USER = new User("Test User", "password");
    private final String BASE_URL = "/userExercises";

    @Test
    @WithMockUser
    public void createUserExerciseTest() throws Exception {
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"muscleGroup\": \"Back\", \"oneRM\": 80.0}";

        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(java.util.Optional.of(TEST_USER));

        mockMvc.perform(post("/userExercises")
                .with(user(TEST_USER.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Barbell Rows\", \"muscleGroup\": \"Back\", \"oneRM\": 80.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.oneRM").value(80.0));
    }

    @Test
    @WithMockUser
    public void createUserExerciseWithoutUserTest() throws Exception {
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"muscleGroup\": \"Back\", \"oneRM\": 80.0}";

        mockMvc.perform(post("/userExercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exerciseJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void createUserExerciseWithInvalidExerciseTest() throws Exception {
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"oneRM\": 80.0}";
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(java.util.Optional.of(TEST_USER));

        mockMvc.perform(post("/userExercises")
                .with(user(TEST_USER.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(exerciseJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void getAllUserExercisesTest() throws Exception {
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(java.util.Optional.of(TEST_USER));
        List<UserExercise> exercises = new ArrayList<>();
        exercises.add(new UserExercise(TEST_USER, "Exercise Name", "Muscle Group", 40.0));
        exercises.add(new UserExercise(TEST_USER, "Exercise 2", "Muscle Group 2", 100.5));
        when(userExerciseRepo.findByUserId(TEST_USER.getId())).thenReturn(exercises);

        mockMvc.perform(get("/userExercises")
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Exercise Name"))
                .andExpect(jsonPath("$[1].muscleGroup").value("Muscle Group 2"));
    }

    @Test
    public void getAllUserExercisesWithoutUserAuthTest() throws Exception {

        mockMvc.perform(get("/userExercises"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void getAllUserExercisesWithNullUserTest() throws Exception {
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.empty());

        mockMvc.perform(get("/userExercises")
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void getAllUserExercisesWithNullExercisesTest() throws Exception {
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(java.util.Optional.of(TEST_USER));
        when(userExerciseRepo.findByUserId(TEST_USER.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/userExercises")
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void getUserExerciseByIdTest() throws Exception {
        long exerciseId = 1L;
        UserExercise exercise = new UserExercise(TEST_USER, "Exercise", "Muscle Group", 50.0);
        exercise.setId(exerciseId);

        when(userExerciseRepo.findById(exerciseId)).thenReturn(java.util.Optional.of(exercise));

        mockMvc.perform(get("/userExercises/" + exerciseId)
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Exercise"));
    }

    @Test
    @WithMockUser
    public void getUserExerciseByIdNullExerciseTest() throws Exception {
        long exerciseId = 1L;

        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/userExercises/" + exerciseId)
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserExerciseByIdWithoutUserTest() throws Exception {
        long exerciseId = 1L;
        UserExercise exercise = new UserExercise(null, "Exercise", "Muscle Group", 50.0);
        exercise.setId(exerciseId);

        when(userExerciseRepo.findById(exerciseId)).thenReturn(java.util.Optional.of(exercise));

        mockMvc.perform(get("/userExercises/" + exerciseId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void updateUserExerciseTest() throws Exception {
        long exerciseId = 1L;
        TEST_USER.setId(5L);
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"muscleGroup\": \"Back\", \"oneRM\": 80.0}";
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        UserExercise exercise = new UserExercise(TEST_USER, "Exercise", "Muscle Group", 50.0);
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.of(exercise));

        mockMvc.perform(put(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(exerciseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Barbell Rows"));
    }

    @Test
    @WithMockUser
    public void updateUserExerciseInvalidUserExerciseJsonTest() throws Exception {
        long exerciseId = 1L;
        TEST_USER.setId(5L);
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"oneRM\": 80.0}";

        mockMvc.perform(put(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(exerciseJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserExerciseWithoutUserTest() throws Exception {
        long exerciseId = 1L;
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"muscleGroup\": \"Back\", \"oneRM\": 80.0}";
        UserExercise exercise = new UserExercise(null, "Exercise", "Muscle Group", 50.0);
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.of(exercise));

        mockMvc.perform(put(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(exerciseJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void updateUserExerciseInvalidExerciseIdTest() throws Exception {
        long exerciseId = 1L;
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"muscleGroup\": \"Back\", \"oneRM\": 80.0}";
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.empty());

        mockMvc.perform(put(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(exerciseJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void updateUserExerciseForbiddenExercise() throws Exception {
        long exerciseId = 1L;
        String exerciseJson = "{\"name\": \"Barbell Rows\", \"muscleGroup\": \"Back\", \"oneRM\": 80.0}";
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        User secondUser = new User("Second User", "password");
        secondUser.setId(2L);
        TEST_USER.setId(4L);
        UserExercise exercise = new UserExercise(secondUser, "Exercise", "Muscle Group", 50.0);
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.of(exercise));

        mockMvc.perform(put(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(exerciseJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void deleteUserExerciseTest() throws Exception {
        long exerciseId = 1L;
        TEST_USER.setId(1L);
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        UserExercise exercise = new UserExercise(TEST_USER, "Exercise", "Muscle Group", 50.0);
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.of(exercise));

        mockMvc.perform(delete(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    public void deleteUserExerciseInvalidExerciseTest() throws Exception {
        long exerciseId = 1L;
        TEST_USER.setId(1L);
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.empty());

        mockMvc.perform(delete(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUserExerciseInvalidUserTest() throws Exception {
        long exerciseId = 1L;

        mockMvc.perform(delete(BASE_URL + "/" + exerciseId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void deleteUserExerciseNullUserTest() throws Exception {
        long exerciseId = 1L;
        TEST_USER.setId(1L);
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.empty());
        UserExercise exercise = new UserExercise(TEST_USER, "Exercise", "Muscle Group", 50.0);
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.of(exercise));

        mockMvc.perform(delete(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void deleteUserExerciseForbiddenExercise() throws Exception {
        long exerciseId = 1L;
        TEST_USER.setId(2L);
        when(userRepo.findByUsername(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        User secondUser = new User("Username", "password");
        UserExercise exercise = new UserExercise(secondUser, "Exercise", "Muscle Group", 50.0);
        when(userExerciseRepo.findById(exerciseId)).thenReturn(Optional.of(exercise));

        mockMvc.perform(delete(BASE_URL + "/" + exerciseId)
                .with(user(TEST_USER.getUsername())))
                .andExpect(status().isForbidden());

    }
}
