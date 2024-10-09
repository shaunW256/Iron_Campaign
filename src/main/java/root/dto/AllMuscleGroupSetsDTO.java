package root.dto;

import java.util.List;

public class AllMuscleGroupSetsDTO {

    private List<MuscleGroupSetsDTO> setsPerMuscleGroup;

    public List<MuscleGroupSetsDTO> getSetsPerMuscleGroup() {
        return setsPerMuscleGroup;
    }

    public void setSetsPerMuscleGroup(List<MuscleGroupSetsDTO> setsPerMuscleGroup) {
        this.setsPerMuscleGroup = setsPerMuscleGroup;
    }
}
