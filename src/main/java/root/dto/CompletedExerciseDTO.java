package root.dto;

public class CompletedExerciseDTO {
    private Long userID;
    private String exerciseName;
    private double weightPerformed;
    private int[] repsPerSet;
    private String difficulty;
    private String liftType;

    public Long getUserID() {
        return userID;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int[] getRepsPerSet() {
        return repsPerSet;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getLiftType() {
        return liftType;
    }

    public double getWeightPerformed() {
        return weightPerformed;
    }

    public int getFirstSetReps(){
        return repsPerSet[0];
    }
}
