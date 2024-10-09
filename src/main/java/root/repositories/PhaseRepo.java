package root.repositories;

import root.entities.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhaseRepo extends JpaRepository<Phase, Long> {
}
