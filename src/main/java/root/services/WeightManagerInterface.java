package root.services;

import root.dto.CompletedExerciseDTO;
import root.dto.CompletedWorkoutDTO;
import root.entities.Phase;
import root.entities.User;
import root.entities.UserExercise;

import java.util.Map;

public interface WeightManagerInterface {
    void setRepModifiers(String phaseName);
    void updateOneRepMax(CompletedWorkoutDTO exercises);
    double calculateOneRepMax(double weightPerformed, int repsPerformed, double increment);
    double roundToNearestWeightFraction(double weight, double increment);
    double calculateWeightChangeByPercent(UserExercise exercise, double percentage);
    void initialiseDeloadWeights(Map<String, UserExercise> lifts);
    void initialiseStartingPhaseWeights(Map<String, UserExercise> lifts);
    void updateWorkingWeight(UserExercise exercise, double weightChangeKG, boolean weightIncrease);
    void checkRepsPerSet(CompletedExerciseDTO exerciseDTO, UserExercise exercise, Phase phase, User lifter);
    void calculateWeightChange(UserExercise exercise, double percentage, boolean weightIncrease);
    void processDifficulty(int completedSets, int setsAtUpperBound, CompletedExerciseDTO exerciseDTO, UserExercise exercise, boolean weightIncrease);
    void increaseOrDecreaseWeight(int completedSets, int setsAtUpperBound, CompletedExerciseDTO exerciseDTO, UserExercise exercise, User lifter);
}
