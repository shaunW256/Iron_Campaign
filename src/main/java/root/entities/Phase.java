package root.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "phases")
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary key
    @Column(name = "phase_id")
    private Long id;

    @NotBlank(message = "Phase name group is required")
    @Column(name = "phase_name")
    private String phaseName;

    @Min(value = 1, message = "Please provide the lower-bound of the strength rep range")
    @Column(name = "lower_strength_reps")
    private int lowerStrReps;

    @Min(value = 2, message = "Please provide the upper-bound of the strength rep range")
    @Column(name = "upper_strength_reps")
    private int upperStrReps;

    @Min(value = 1, message = "Please provide the lower-bound of the hypertrophy rep range")
    @Column(name = "lower_hypertrophy_reps")
    private int lowerHypertrophyReps;

    @Min(value = 2, message = "Please provide the upper-bound of the hypertrophy rep range")
    @Column(name = "upper_hypertrophy_reps")
    private int upperHypertrophyReps;

    public Phase() {

    }

    public Phase(String phaseName, int lowerStrReps, int upperStrReps, int lowerHypertrophyReps, int upperHypertrophyReps) {
        this.phaseName = phaseName;
        this.lowerStrReps = lowerStrReps;
        this.upperStrReps = upperStrReps;
        this.lowerHypertrophyReps = lowerHypertrophyReps;
        this.upperHypertrophyReps = upperHypertrophyReps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public int getLowerStrReps() {
        return lowerStrReps;
    }

    public void setLowerStrReps(int lowerStrReps) {
        this.lowerStrReps = lowerStrReps;
    }

    public int getUpperStrReps() {
        return upperStrReps;
    }

    public void setUpperStrReps(int upperStrReps) {
        this.upperStrReps = upperStrReps;
    }

    public int getLowerHypertrophyReps() {
        return lowerHypertrophyReps;
    }

    public void setLowerHypertrophyReps(int lowerHypertrophyReps) {
        this.lowerHypertrophyReps = lowerHypertrophyReps;
    }

    public int getUpperHypertrophyReps() {
        return upperHypertrophyReps;
    }

    public void setUpperHypertrophyReps(int upperHypertrophyReps) {
        this.upperHypertrophyReps = upperHypertrophyReps;
    }
}
