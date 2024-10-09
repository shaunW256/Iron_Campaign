package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.entities.User;
import root.entities.UserExercise;
import root.repositories.UserExerciseRepo;

import java.util.*;

@Service("exerciseManager")
public class ExerciseManagerService implements ExerciseManagerInterface {

    private final UserExerciseRepo userExerciseRepo;

    @Autowired
    public ExerciseManagerService(UserExerciseRepo userExerciseRepo) {
        this.userExerciseRepo = userExerciseRepo;
    }

    public List<String> getAccessoryMuscleGroups(int day) {
        List<String> lifts;

        switch (day) {
            case 1 -> lifts = Arrays.asList("hamstrings", "biceps", "abs");
            case 2, 4 -> lifts = Arrays.asList("front delts", "side delts", "back", "triceps");
            case 3 -> lifts = Arrays.asList("quads", "biceps", "abs");
            default -> throw new IllegalStateException("Unexpected value: " + day);
        }
        return lifts;
    }

    // TODO: In future - add the chance to have day 4 start with OHP as its compound
    public String getCompoundExercise(int day) {
        String exerciseName;
        switch (day) {
            case 1 -> exerciseName = "squat";
            case 2, 4 -> exerciseName = "bench";
            case 3 -> exerciseName = "deadlift";
            default -> throw new IllegalStateException("Unexpected value: " + day);
        }
        return exerciseName;
    }

    public Map<String, List<UserExercise>> addAccessoryExercises(User lifter, List<String> muscleGroups) {
        Map<String, List<UserExercise>> allExercisesPerMuscleGroups = new HashMap<>();

        for (String muscle : muscleGroups) {
            List<UserExercise> exercisesPerMuscleGroup = userExerciseRepo.findByUserIdAndMuscleGroup(lifter.getId(), muscle);

            // Maybe refactor this elsewhere?
            if(exercisesPerMuscleGroup == null) {
                exercisesPerMuscleGroup = Collections.emptyList();
            }

            allExercisesPerMuscleGroups.put(muscle.toLowerCase(), exercisesPerMuscleGroup);
        }

        return allExercisesPerMuscleGroups;
    }
}
