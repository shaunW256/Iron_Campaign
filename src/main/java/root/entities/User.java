package root.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Please provide a username")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Please provide a password")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phase_count")
    private int phaseCount;

    @Column(name = "week_num")
    private int weekNum;

    @Column(name = "day")
    private int day;

    @ManyToOne
    @JoinColumn(name = "phase_id")
    private Phase phase;

    public User(){

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase){
        this.phase = phase;
    }

    public int getPhaseCount() {
        return phaseCount;
    }

    public void setPhaseCount(int phaseCount) {
        this.phaseCount = phaseCount;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
