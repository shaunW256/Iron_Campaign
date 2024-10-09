package root.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import root.entities.Phase;
import root.entities.User;
import root.entities.UserExercise;
import root.repositories.PhaseRepo;
import root.repositories.UserExerciseRepo;
import root.repositories.UserRepo;

import java.util.Optional;

@Component
public class UtilityHelpers {

    private final UserRepo userRepo;
    private final UserExerciseRepo userExerciseRepo;
    private final PhaseRepo phaseRepo;
    private final String CLASS_NAME = "UtilityHelpers.java";

    @Autowired
    public UtilityHelpers(UserRepo userRepo, UserExerciseRepo userExerciseRepo, PhaseRepo phaseRepo) {
        this.userRepo = userRepo;
        this.userExerciseRepo = userExerciseRepo;
        this.phaseRepo = phaseRepo;
    }

    public UserExercise validateOptionalUserExercise(Long id) {
        Optional<UserExercise> userExercise = userExerciseRepo.findById(id);
        return userExercise.orElse(null);
    }

    public boolean doesUsernameExist(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    // This version might no longer be needed
    public ResponseEntity<User> validateUserFromOptional(Authentication authenticatedUser) {
        String username = authenticatedUser.getName();
        Optional<User> authenticatedOptionalUser = userRepo.findByUsername(username);
        User user;

        if (authenticatedOptionalUser.isPresent()) {
            user = authenticatedOptionalUser.get();
            return ResponseEntity.ok(user);
        }

        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " validateUserFromOptional");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<User> validateUserFromOptional(Long userId) {
        Optional<User> userOptional = userRepo.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(user);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " validateUserFromOptional");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public boolean doesExerciseBelongToUser(UserExercise userExercise, User user) {
        if (user.getId().equals(userExercise.getUser().getId())) {
            return true;
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " doesExerciseBelongToUser");
            return false;
        }
    }

    public ResponseEntity<Phase> retrieveOptionalPhase(Long phaseId) {
        Optional<Phase> phaseOptional = phaseRepo.findById(phaseId);

        if (phaseOptional.isPresent()) {
            Phase phase = phaseOptional.get();
            return ResponseEntity.ok(phase);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " retrieveOptionalPhase");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
