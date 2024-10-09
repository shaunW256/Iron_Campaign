package root.services;

import org.springframework.stereotype.Service;
import root.entities.User;

@Service("phaseManager")
public class PhaseManagerService implements PhaseManagerInterface {

    public void incrementPhase(User lifter) {
        int phaseNum = lifter.getPhaseCount();

        if (phaseNum < 4) {
            lifter.setPhaseCount(phaseNum + 1);
        }
        else {
            lifter.setPhaseCount(1);
        }
    }

    public void incrementWeek(User lifter) {
        int week = lifter.getWeekNum();

        if (week < 5) {
            lifter.setWeekNum(week + 1);
        }
        else {
            lifter.setWeekNum(1);
            incrementPhase(lifter);
        }
    }

    public void incrementDay (User lifter) {
        int day = lifter.getDay();

        if (day < 4) {
            lifter.setDay(day + 1);
        }
        else {
            lifter.setDay(1);
            incrementWeek(lifter);
        }
    }
}

