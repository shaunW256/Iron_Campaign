package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.dto.CompletedExerciseDTO;
import root.dto.CompletedWorkoutDTO;
import root.entities.Phase;
import root.entities.User;
import root.entities.UserExercise;
import root.repositories.UserExerciseRepo;

import java.util.Map;


@Service("weightManager")
public class WeightManagerService implements WeightManagerInterface {

    private static final String STR_PHASE_NAME_CONSTANT = "Strength";
    private static final String HYPERTROPHY_PHASE_NAME_CONSTANT = "Hypertrophy";
    private static final String COMPOUND_LIFT_TYPE_CONSTANT = "main lift";
    private static final double DELOAD_PERCENTAGE = 0.6;
    private static final double TEN_PERCENT = 0.1;
    private static final double FIVE_PERCENT = 0.05;
    private static final double FOUR_PERCENT = 0.04;
    private static final double THREE_PERCENT = 0.03;
    private static final double TWO_HALF_PERCENT = 0.025;
    private static final int MINIMUM_KG = 60;
    private double strRepModifier;
    private double hypertrophyRepModifier;
    private final UserExerciseRepo userExerciseRepo;
    private final String CLASS_NAME = "WeightManagerService.java";

    @Autowired
    public WeightManagerService(UserExerciseRepo userExerciseRepo) {
        this.userExerciseRepo = userExerciseRepo;
    }

    public void setRepModifiers(String phaseName) {

        if (phaseName.equals(HYPERTROPHY_PHASE_NAME_CONSTANT)) {
            this.strRepModifier = 0.8;
            this.hypertrophyRepModifier = 0.7;
        }
        else if (phaseName.equals(STR_PHASE_NAME_CONSTANT)) {
            this.strRepModifier = 0.9;
            this.hypertrophyRepModifier = 0.75;
        }
    }

    // Updates the one rep max based on the weights/reps lifter did in session
    public void updateOneRepMax(CompletedWorkoutDTO exercises) {
        for (CompletedExerciseDTO exercise : exercises.getExercises()) {
            try {
                int firstSetReps = exercise.getFirstSetReps();
                double weightPerformed = exercise.getWeightPerformed();
                UserExercise userExercise = userExerciseRepo.findByUserIdAndName(exercise.getUserID(), exercise.getExerciseName());
                userExercise.setOneRM(calculateOneRepMax(weightPerformed, firstSetReps, userExercise.getWeightIncrement()));
            }
            catch(Exception e) {
                System.out.println("[ERROR]: In " + CLASS_NAME + " updateOneRepMax");
            }
        }
    }

    // Calculates the estimated one rep max using Brzycki's formula
    public double calculateOneRepMax(double weightPerformed, int repsPerformed, double increment) {
        return roundToNearestWeightFraction(weightPerformed / (1.0278 - (0.0278 * repsPerformed)), increment);
    }

    // Rounds the weight to the nearest increment the exercise can change by
    public double roundToNearestWeightFraction(double weight, double increment) {
        return Math.round(weight / increment) * increment;
    }

    public double calculateWeightChangeByPercent(UserExercise exercise, double percentage) {
        return exercise.getWorkingWeight() * percentage;
    }

    public void initialiseDeloadWeights(Map<String, UserExercise> lifts) {
        for (Map.Entry<String, UserExercise> lift : lifts.entrySet()) {
            double deloadWeight = calculateWeightChangeByPercent(lift.getValue(), DELOAD_PERCENTAGE);
            UserExercise exercise = lift.getValue();
            exercise.setDeloadWeight(roundToNearestWeightFraction(deloadWeight, exercise.getWeightIncrement()));
        }
    }

    // Set the weights for a new training phase
    public void initialiseStartingPhaseWeights(Map<String, UserExercise> lifts) {
        for (Map.Entry<String, UserExercise> lift : lifts.entrySet()) {
            double workingWeight;
            if (lift.getKey().equals(COMPOUND_LIFT_TYPE_CONSTANT)) {
                workingWeight = lift.getValue().getOneRM() * strRepModifier;
            }
            else {
                workingWeight = lift.getValue().getOneRM() * hypertrophyRepModifier;
            }
            UserExercise exercise = lift.getValue();
            exercise.setWorkingWeight(roundToNearestWeightFraction(workingWeight, exercise.getWeightIncrement()));
            userExerciseRepo.save(exercise);
        }
    }

    public void updateWorkingWeight(UserExercise exercise, double weightChangeKG, boolean weightIncrease) {
        double currWorkingWeight = exercise.getWorkingWeight();
        double updatedWeightKG;

        if (weightIncrease) {
            updatedWeightKG = currWorkingWeight + weightChangeKG;
        }
        else {
            updatedWeightKG = currWorkingWeight - weightChangeKG;
        }
        double roundedWeightChangeKG = roundToNearestWeightFraction(updatedWeightKG, exercise.getWeightIncrement());
        exercise.setWorkingWeight(roundedWeightChangeKG);
        userExerciseRepo.save(exercise);
    }

    // TODO: Refactor this into separate methods
    // Counts how many sets met the rep range - also counts how many were equal or more than upper-bound rep range
    public void checkRepsPerSet(CompletedExerciseDTO exerciseDTO, UserExercise exercise, Phase phase, User lifter) {
        int completedSets = 0;
        int setsAtUpperBound = 0;
        int upperBoundReps;
        int lowerBoundReps;

        for (int i = 0; i < exerciseDTO.getRepsPerSet().length; i++) {
            if (exerciseDTO.getLiftType().equals(COMPOUND_LIFT_TYPE_CONSTANT)) {
                upperBoundReps = phase.getUpperStrReps();
                lowerBoundReps = phase.getLowerStrReps();
            } else {
                upperBoundReps = phase.getUpperHypertrophyReps();
                lowerBoundReps = phase.getLowerHypertrophyReps();
            }

            if (exerciseDTO.getRepsPerSet()[i] >= lowerBoundReps) {
                completedSets++;
                if (exerciseDTO.getRepsPerSet()[i] >= upperBoundReps) {
                    setsAtUpperBound++;
                }
            }
        }
        increaseOrDecreaseWeight(completedSets, setsAtUpperBound, exerciseDTO, exercise, lifter);
    }

    public void calculateWeightChange(UserExercise exercise, double percentage, boolean weightIncrease) {
        double weightChangeKG;
        if (exercise.getWorkingWeight() >= MINIMUM_KG || !weightIncrease) {
            weightChangeKG = calculateWeightChangeByPercent(exercise, percentage);
        }
        // For when the percentage increase is too small - increase by smallest increment
        else {
            weightChangeKG = exercise.getWeightIncrement();
        }
        updateWorkingWeight(exercise, weightChangeKG, weightIncrease);
    }

    public void processDifficulty(int completedSets, int setsAtUpperBound, CompletedExerciseDTO exerciseDTO, UserExercise exercise, boolean weightIncrease) {
        double percentage;
        if (exerciseDTO.getDifficulty().equals("Easy")) {
            if (completedSets == setsAtUpperBound) {
                percentage = TEN_PERCENT;
            }
            else {
                percentage = FIVE_PERCENT;
            }
        }
        else if (exerciseDTO.getDifficulty().equals("Medium")){
            percentage = THREE_PERCENT;
        }
        else {
            percentage = TWO_HALF_PERCENT;
        }
        calculateWeightChange(exercise, percentage, weightIncrease);
    }

    public void increaseOrDecreaseWeight(int completedSets, int setsAtUpperBound, CompletedExerciseDTO exerciseDTO, UserExercise exercise, User lifter) {
        if (completedSets == exerciseDTO.getRepsPerSet().length) {
            if (lifter.getDay() != 4) {
                processDifficulty(completedSets, setsAtUpperBound, exerciseDTO, exercise, true);
            }
            else {
                /* This is to set the weight for the next week, day 1 -
                   possibly add a new function instead to deal with the next week's weight in a different algo */
                calculateWeightChange(exercise, FOUR_PERCENT, false);
            }
        }
        else {
            calculateWeightChange(exercise, TEN_PERCENT, false);
        }
    }
}
