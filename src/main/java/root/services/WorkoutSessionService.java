package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.dto.CompletedExerciseDTO;
import root.dto.CompletedWorkoutDTO;
import root.entities.User;
import root.entities.Phase;
import root.entities.UserExercise;
import root.repositories.UserExerciseRepo;
import java.util.Map;
import java.util.List;


@Service("workoutSession")
public class WorkoutSessionService implements WorkoutSessionInterface {

    private final UserExerciseRepo userExerciseRepo;
    private final PhaseManagerInterface phaseManagerInterface;
    private final WeightManagerInterface weightManagerInterface;
    private final ExerciseManagerService exerciseManagerInterface;
    private final SetManagerInterface setManagerInterface;
    private final String CLASS_NAME = "workoutSessionService.java";

    @Autowired
    public WorkoutSessionService(UserExerciseRepo userExerciseRepo, PhaseManagerInterface phaseManagerInterface,
                                 WeightManagerService weightManagerInterface,
                                 ExerciseManagerService exerciseManagerInterface, SetManagerInterface setManagerInterface) {
        this.userExerciseRepo = userExerciseRepo;
        this.phaseManagerInterface = phaseManagerInterface;
        this.weightManagerInterface = weightManagerInterface;
        this.exerciseManagerInterface = exerciseManagerInterface;
        this.setManagerInterface = setManagerInterface;
    }

    // This is to get all of the accessory exercises the user can choose from for their session
    public Map<String, List<UserExercise>> returnUsersExerciseOptions(User lifter) {
        List<String> muscleGroupsToTrain = exerciseManagerInterface.getAccessoryMuscleGroups(lifter.getDay());
        return exerciseManagerInterface.addAccessoryExercises(lifter, muscleGroupsToTrain);
    }

    public Map<String, Integer> configureSetsToPerform(User lifter) {
        boolean deload = false;
        String compoundExercise = exerciseManagerInterface.getCompoundExercise(lifter.getDay());
        List<String> movements = exerciseManagerInterface.getAccessoryMuscleGroups(lifter.getDay());
        movements.add(compoundExercise);
        if (lifter.getWeekNum() == 5) {
            deload = true;
        }
        return setManagerInterface.getSetsPerMovement(movements, deload);
    }

    public Map<String, UserExercise> configureSessionLifts(User lifter, Map<String, UserExercise> sessionLifts) {
        String compoundLiftStr = exerciseManagerInterface.getCompoundExercise(lifter.getDay());
        UserExercise compoundLift = userExerciseRepo.findByUserIdAndName(lifter.getId(), compoundLiftStr);
        sessionLifts.put("main lift", compoundLift);
        // Weights for next session are updated at the end of a session, so no need to consider weeks 2 - 4
        if (lifter.getWeekNum() == 1) {
            setSessionWeights(sessionLifts, lifter.getPhase(), false);
        }
        else if (lifter.getWeekNum() == 5) {
            setSessionWeights(sessionLifts, lifter.getPhase(), true);
        }
        return sessionLifts;
    }

    public void setSessionWeights(Map<String, UserExercise> lifts, Phase phase, boolean deloadWeek) {
        if (!deloadWeek) {
            weightManagerInterface.setRepModifiers(phase.getPhaseName());
            weightManagerInterface.initialiseStartingPhaseWeights(lifts);
        }
        else {
            weightManagerInterface.initialiseDeloadWeights(lifts);
        }
    }

    // Begins to update the User's exercises based on the performance of the last session
    public boolean updateUserExercisesFromDTO(CompletedWorkoutDTO completedWorkoutInfo, User lifter) {
        List<CompletedExerciseDTO> exerciseDTOS = completedWorkoutInfo.getExercises();
        try {
            Phase phase = lifter.getPhase();
            for (CompletedExerciseDTO exerciseDTO : exerciseDTOS) {
                UserExercise userExercise = userExerciseRepo.findByUserIdAndName(lifter.getId(),
                        exerciseDTO.getExerciseName());
                weightManagerInterface.checkRepsPerSet(exerciseDTO, userExercise, phase, lifter);
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " updateUserExercisesFromDTO");
            return false;
        }
    }

    public boolean sessionEnd(CompletedWorkoutDTO completedWorkoutInfo, User user) {
        weightManagerInterface.updateOneRepMax(completedWorkoutInfo);
        boolean exercisesUpdated = updateUserExercisesFromDTO(completedWorkoutInfo, user);

        if (exercisesUpdated) {
            phaseManagerInterface.incrementDay(user);
            return true;
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " sessionEnd");
            return false;
        }
    }
}
