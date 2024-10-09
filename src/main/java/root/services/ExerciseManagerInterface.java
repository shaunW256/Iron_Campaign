package root.services;

import root.entities.User;
import root.entities.UserExercise;
import java.util.List;
import java.util.Map;

public interface ExerciseManagerInterface {
    List<String> getAccessoryMuscleGroups(int day);
    String getCompoundExercise(int day);
    Map<String, List<UserExercise>> addAccessoryExercises(User lifter, List<String> muscleGroups);
}
