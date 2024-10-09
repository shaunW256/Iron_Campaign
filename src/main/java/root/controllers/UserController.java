package root.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import root.entities.Phase;
import root.entities.User;
import root.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import root.security.AuthRequest;
import root.services.JwtService;
import root.services.UserInfoService;
import root.utilities.UtilityHelpers;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepo userRepo;
    private final UserInfoService userInfoService;
    private final UtilityHelpers utilityHelpers;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final String CLASS_NAME = "UserController.java";

    @Autowired
    public UserController(UserRepo userRepo, UserInfoService userInfoService, UtilityHelpers utilityHelpers,
                          JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.userInfoService = userInfoService;
        this.utilityHelpers = utilityHelpers;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        try {
            String registrationConfirmation = userInfoService.userRegistration(user);
            return ResponseEntity.ok(registrationConfirmation);
        }
        catch (Exception e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " createUser");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public String loginAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            return ResponseEntity.ok(userRepo.findAll());
        }
        catch (Exception e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " getAllUsers");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return utilityHelpers.validateUserFromOptional(id);
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<User> updateUser(@RequestBody User userDetails) {
        User user = userRepo.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            userRepo.save(user);
            return ResponseEntity.ok(user);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " updateUser");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(@RequestBody User userDetails) {
        User user = userRepo.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            user.setPassword(userDetails.getPassword());
            return ResponseEntity.ok(user);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " updatePassword");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@RequestBody User userDetails) {
        User user = userRepo.findByUsername(userDetails.getUsername()).orElse(null);
        try {
            userRepo.deleteById(userDetails.getId());
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (NullPointerException e) {
            System.out.println("[ERROR]: In " + CLASS_NAME + " deleteUser");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/initialisePhase")
    public ResponseEntity<User> initialisePhase(@PathVariable Long userId, @RequestParam("phaseId") Long phaseId){
        User user = utilityHelpers.validateUserFromOptional(userId).getBody();
        Phase phase = utilityHelpers.retrieveOptionalPhase(phaseId).getBody();
        if (user != null && phase != null) {
            user.setPhase(phase);
            user.setWeekNum(1);
            user.setDay(1);
            user.setPhaseCount(1);
            userRepo.save(user);
            return ResponseEntity.ok(user);
        }
        else {
            System.out.println("[ERROR]: In " + CLASS_NAME + " initialisePhase");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
