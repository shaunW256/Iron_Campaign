package root.services;

import org.springframework.http.ResponseEntity;
import root.dto.AllMuscleGroupSetsDTO;

import java.util.List;
import java.util.Map;

public interface SetManagerInterface {
    Map<String, Integer> getSetsPerMovement(List<String> movements, boolean deload);
    Map<String, Integer> getSetsForDeloadWeek(Map<String, Integer> movements);
    ResponseEntity<String> validateSets(AllMuscleGroupSetsDTO muscleGroupsPerSet);
}
