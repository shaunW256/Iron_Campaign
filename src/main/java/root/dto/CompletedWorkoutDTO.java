package root.dto;

import java.util.List;

public class CompletedWorkoutDTO {

    private List<CompletedExerciseDTO> exercises;

    public List<CompletedExerciseDTO> getExercises() {
        return exercises;
    }

    public void setExercises(List<CompletedExerciseDTO> exercises) {
        this.exercises = exercises;
    }
}
