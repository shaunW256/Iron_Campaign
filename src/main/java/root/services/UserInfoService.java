package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import root.entities.User;
import root.repositories.UserRepo;
import root.utilities.UtilityHelpers;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    // https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UtilityHelpers utilityHelpers;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = userRepo.findByUsername(username);

        // Converting UserInfo to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Modified to not register if username exists currently
    public String userRegistration(User user) {
        if (!utilityHelpers.doesUsernameExist(user.getUsername())){
            // Encode password before saving the user
            user.setPassword(encoder.encode(user.getPassword()));
            userRepo.save(user);
            return "User: " + user.getUsername() + " successfully registered.";
        }
        else {
            throw new UsernameNotFoundException("User already exists with username: " + user.getUsername());
        }
    }
}