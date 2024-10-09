package root.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import root.dto.AllMuscleGroupSetsDTO;
import root.dto.CompletedWorkoutDTO;
import root.entities.User;
import root.entities.UserExercise;
import root.repositories.UserRepo;
import root.services.WorkoutSessionInterface;
import java.util.List;
import java.util.Map;

import root.services.SetManagerInterface;

@RestController
@RequestMapping("/workoutSession")
public class WorkoutSessionController {

    private final WorkoutSessionInterface workoutSessionInterface;
    private final SetManagerInterface setManagerInterface;
    private final UserRepo userRepo;
    private final String CLASS_NAME = "workoutSessionController.java";

    @Autowired
    public WorkoutSessionController(WorkoutSessionInterface workoutSessionInterface,
                                    SetManagerInterface setManagerInterface, UserRepo userRepo) {
        this.workoutSessionInterface = workoutSessionInterface;
        this.setManagerInterface = setManagerInterface;
        this.userRepo = userRepo;
    }

    // This returns all of the user's accessory exercises back for them to pick which ones they are doing for the session
    @GetMapping("/user-exercises")
    public ResponseEntity<Map<String, List<UserExercise>>> provideUserExercisesOptions(Authentication authenticatedUser) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);

        if (user != null) {
            Map<String, List<UserExercise>> exercises = workoutSessionInterface.returnUsersExerciseOptions(user);
            return ResponseEntity.ok(exercises);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " provideUserExercisesOptions");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Returns the number of sets to perform for each muscle group
    @GetMapping("/get-sets-to-perform")
    public ResponseEntity<Map<String, Integer>> provideSetsToPerform(Authentication authenticatedUser) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);

        if (user != null) {
            Map<String, Integer> setsToPerform = workoutSessionInterface.configureSetsToPerform(user);
            return ResponseEntity.ok(setsToPerform);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " provideSetsToPerform");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // User has selected their exercises, add the compound to it and then calculate the weight they should lift
    @PostMapping("/workout/start")
    public ResponseEntity<Map<String, UserExercise>> configureSession(Authentication authenticatedUser, @RequestBody Map<String, UserExercise> jsonData) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);

        if (user != null) {
            Map<String, UserExercise> sessionLifts = workoutSessionInterface.configureSessionLifts(user, jsonData);

            if (!sessionLifts.containsKey("main lift")) {
                System.out.println("[ERROR]: In " + CLASS_NAME + " configureSession");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok(sessionLifts);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " configureSession");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Ensure the user has selected enough sets
    @PostMapping("/set-validation")
    public ResponseEntity<String> validateSets(@RequestBody AllMuscleGroupSetsDTO allMuscleGroupSetsDTO) {
        return setManagerInterface.validateSets(allMuscleGroupSetsDTO);
    }

    // User has completed a gym session - add a day to their count etc and calculate new weights to lift
    @PostMapping("/finish/session")
    public ResponseEntity<Object> finishSession (Authentication authenticatedUser, @RequestBody CompletedWorkoutDTO completedWorkoutInfo) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);
        boolean sessionEnded = workoutSessionInterface.sessionEnd(completedWorkoutInfo, user);

        if (user != null) {
            if (sessionEnded) {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else {
                System.out.println("[ERROR]: In " + CLASS_NAME + " finishSession");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " finishSession");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
