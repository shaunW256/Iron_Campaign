package root.services;

import root.dto.CompletedWorkoutDTO;
import root.entities.Phase;
import root.entities.User;
import root.entities.UserExercise;

import java.util.List;
import java.util.Map;

public interface WorkoutSessionInterface {
    Map<String, List<UserExercise>> returnUsersExerciseOptions(User lifter);
    Map<String, Integer> configureSetsToPerform(User lifter);
    Map<String, UserExercise> configureSessionLifts(User lifter, Map<String, UserExercise> sessionLifts);
    void setSessionWeights(Map<String, UserExercise> lifts, Phase phase, boolean deloadWeek);
    boolean updateUserExercisesFromDTO(CompletedWorkoutDTO completedWorkoutInfo, User lifter);
    boolean sessionEnd(CompletedWorkoutDTO completedWorkoutInfo, User user);
}
