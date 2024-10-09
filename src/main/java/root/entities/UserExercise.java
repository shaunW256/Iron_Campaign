package root.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_exercises")
public class UserExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_exercise_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @NotBlank(message = "Please provide an exercise name")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Please provide the muscle group of the exercise")
    @Column(name = "muscle_group")
    private String muscleGroup;

    @NotNull(message = "Please provide a valid one rep max value")
    @Column(name = "one_rm")
    private double oneRM;

    @Column(name = "working_weight")
    private double workingWeight;

    @Column(name = "deload_weight")
    private double deloadWeight;

    @Column(name = "weight_increment")
    private double weightIncrement;

    public UserExercise(){
        // Default constructor - required by JPA
    }

    public UserExercise(User user, String name, String muscleGroup, double oneRM){
        this.user = user;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.oneRM = oneRM;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public double getOneRM() {
        return oneRM;
    }

    public void setOneRM(double oneRM) {
        this.oneRM = oneRM;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public double getWorkingWeight() {
        return workingWeight;
    }

    public void setWorkingWeight(double workingWeight) {
        this.workingWeight = workingWeight;
    }

    public double getWeightIncrement() {
        return weightIncrement;
    }

    public void setWeightIncrement(double increaseWeight) {
        this.weightIncrement = increaseWeight;
    }

    public double getDeloadWeight() {
        return deloadWeight;
    }

    public void setDeloadWeight(double deloadWeight) {
        this.deloadWeight = deloadWeight;
    }
}

