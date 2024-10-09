package root.services;

import root.entities.User;

public interface PhaseManagerInterface {
    void incrementPhase(User lifter);
    void incrementWeek(User lifter);
    void incrementDay (User lifter);
}
