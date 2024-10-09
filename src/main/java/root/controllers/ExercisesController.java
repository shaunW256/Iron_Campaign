package root.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import root.entities.Exercise;
import root.repositories.ExercisesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/exercises")
public class ExercisesController {

    private final String CLASS_NAME = "ExercisesController.java";

    @Autowired
    private ExercisesRepo exercisesRepo;

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@Valid @RequestBody Exercise exercise) {
        try {
            exercisesRepo.save(exercise);
            // Return 201 created rather than 200 successful
            return ResponseEntity.status(HttpStatus.CREATED).body(exercise);
        }
        catch (Exception e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " createExercise");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        try {
            return ResponseEntity.ok(exercisesRepo.findAll());
        }
        catch (Exception e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " getAllExercises");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        return exercisesRepo.findById(id).map(exercise -> ResponseEntity.ok().body(exercise))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise exerciseDetails) {
        return exercisesRepo.findById(id).map(exercise -> {
            exercise.setName(exerciseDetails.getName());
            exercise.setMuscleGroup(exerciseDetails.getMuscleGroup());
            Exercise updatedExercise = exercisesRepo.save(exercise);
            return ResponseEntity.ok().body(updatedExercise);
        })
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteExercise(@PathVariable Long id) {
        try {
            exercisesRepo.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (Exception e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " deleteExercise");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Exercise not found");
        }
    }

}
