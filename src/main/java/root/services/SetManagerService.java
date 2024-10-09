package root.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.dto.AllMuscleGroupSetsDTO;
import root.dto.MuscleGroupSetsDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("setManager")
public class SetManagerService implements SetManagerInterface {

    private static final String DEADLIFT = "deadlift";
    private static final int REQUIRED_SETS = 5;

    public Map<String, Integer>getSetsPerMovement(List<String> movements, boolean deload) {
        Map<String, Integer> movementsWithSetRange = new HashMap<>();

        for (String movement : movements) {
            if (movement.equals(DEADLIFT)) {
                movementsWithSetRange.put(movement, 3);
            }
            else {
                movementsWithSetRange.put(movement, 5);
            }
        }

        if (deload) {
            movementsWithSetRange = getSetsForDeloadWeek(movementsWithSetRange);
        }
        return movementsWithSetRange;
    }

    public Map<String, Integer> getSetsForDeloadWeek(Map<String, Integer> movements){
        for (Map.Entry<String, Integer> movement : movements.entrySet()) {
            movement.setValue(movement.getValue() - 1);
        }
        return movements;
    }

    public ResponseEntity<String> validateSets(AllMuscleGroupSetsDTO muscleGroupsPerSet) {
        List<String> insufficientSets = new ArrayList<>();
        for (MuscleGroupSetsDTO muscleGroup : muscleGroupsPerSet.getSetsPerMuscleGroup()) {
            if (muscleGroup.getSets() < REQUIRED_SETS) {
                insufficientSets.add(muscleGroup.getMuscleGroup());
            }
        }
        if (insufficientSets.isEmpty()){
            return ResponseEntity.ok("Sets match required volume for each muscle group.");
        }
        else {
            return ResponseEntity.ok("Insufficient volume for: " + String.join(", ", insufficientSets));
        }
    }
}
