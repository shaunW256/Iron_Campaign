package root.repositories;

import root.entities.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserExerciseRepo extends JpaRepository<UserExercise, Long> {
     List<UserExercise> findByUserId(Long id);
     UserExercise findByUserIdAndName(Long id, String name);
     List<UserExercise> findByUserIdAndMuscleGroup(Long id, String muscleGroup);
}
