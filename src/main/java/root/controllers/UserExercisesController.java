package root.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import root.entities.User;
import root.entities.UserExercise;
import root.repositories.UserExerciseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import root.repositories.UserRepo;
import root.utilities.UtilityHelpers;

import java.util.List;

@RestController
@RequestMapping("/userExercises")
public class UserExercisesController {

    private final UserExerciseRepo userExerciseRepo;
    private final UtilityHelpers utilityHelpers;
    private final UserRepo userRepo;
    private final String CLASS_NAME = "UserExerciseController.java";

    @Autowired
    public UserExercisesController(UserExerciseRepo userExerciseRepo, UtilityHelpers utilityHelpers, UserRepo userRepo) {
        this.userExerciseRepo = userExerciseRepo;
        this.utilityHelpers = utilityHelpers;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<UserExercise> createUserExercise(@Valid @RequestBody UserExercise userExercise,
                                                           Authentication authenticatedUser) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);
        if (user != null) {
            userExercise.setUser(user);
            userExerciseRepo.save(userExercise);
            return ResponseEntity.status(HttpStatus.CREATED).body(userExercise);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " createUserExercise");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    @GetMapping
    public ResponseEntity<List<UserExercise>> getAllUserExercises(Authentication authenticatedUser) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);

        if (user != null) {
            return ResponseEntity.ok(userExerciseRepo.findByUserId(user.getId()));
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " getAllUserExercises");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserExercise> getUserExerciseById(@PathVariable Long id, Authentication authenticatedUser) {
        return userExerciseRepo.findById(id)
                .map(userExercise -> ResponseEntity.ok().body(userExercise))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserExercise> updateUserExercise(@PathVariable Long id, @Valid @RequestBody UserExercise userExerciseDetails,
                                                           Authentication authenticatedUser) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);
        UserExercise userExercise = utilityHelpers.validateOptionalUserExercise(id);
        boolean exerciseBelongsToUser;

        if (user != null && userExercise != null) {
            exerciseBelongsToUser = utilityHelpers.doesExerciseBelongToUser(userExercise, user);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " updateUserExercise");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (exerciseBelongsToUser) {
            userExercise.setName(userExerciseDetails.getName());
            userExercise.setMuscleGroup(userExerciseDetails.getMuscleGroup());
            userExercise.setOneRM(userExerciseDetails.getOneRM());
            userExerciseRepo.save(userExercise);
            return ResponseEntity.ok().body(userExercise);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " updateUserExercise");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Delete an exercise with authentication
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserExercise(Authentication authenticatedUser, @PathVariable Long id) {
        User user = userRepo.findByUsername(authenticatedUser.getName()).orElse(null);
        UserExercise exercise = utilityHelpers.validateOptionalUserExercise(id);

        boolean exerciseValidated;
        if (user != null && exercise != null) {
            exerciseValidated = utilityHelpers.doesExerciseBelongToUser(exercise, user);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " deleteUserExercise");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (exerciseValidated) {
            userExerciseRepo.deleteById(exercise.getId());
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " deleteUserExercise");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
