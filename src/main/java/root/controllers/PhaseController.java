package root.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import root.entities.Phase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import root.repositories.PhaseRepo;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/phase")
public class PhaseController {

    private final PhaseRepo phaseRepo;
    private final String CLASS_NAME = "PhaseController.java";

    @Autowired
    public PhaseController(PhaseRepo phaseRepo) {
        this.phaseRepo = phaseRepo;
    }

    @PostMapping
    public ResponseEntity<Object> createPhase(@Valid @RequestBody Phase phase, Authentication authentication){
        phaseRepo.save(phase);
        return ResponseEntity.status(HttpStatus.CREATED).body(phase);
    }

    @GetMapping
    public ResponseEntity<List<Phase>> getPhases(){
        return ResponseEntity.ok(phaseRepo.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Phase> updatePhase(@PathVariable Long id, @Valid @RequestBody Phase phaseDetails,
                                             Authentication authentication){
        return phaseRepo.findById(id).map(phase -> {
            phase.setPhaseName(phaseDetails.getPhaseName());
            phase.setLowerHypertrophyReps(phaseDetails.getLowerHypertrophyReps());
            phase.setLowerStrReps(phaseDetails.getLowerStrReps());
            phase.setUpperHypertrophyReps(phaseDetails.getUpperHypertrophyReps());
            phase.setUpperStrReps(phaseDetails.getUpperStrReps());
            Phase updatedPhase = phaseRepo.save(phase);
            return ResponseEntity.ok().body(updatedPhase);
        })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePhase(@PathVariable Long id, Authentication authentication){
        try {
            phaseRepo.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (Exception e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " deletePhase");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
