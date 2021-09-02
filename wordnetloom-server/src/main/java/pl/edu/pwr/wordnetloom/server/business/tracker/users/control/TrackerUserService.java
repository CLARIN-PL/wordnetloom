package pl.edu.pwr.wordnetloom.server.business.tracker.users.control;

import pl.edu.pwr.wordnetloom.server.business.tracker.statistic.control.TrackerStatisticService;
import pl.edu.pwr.wordnetloom.server.business.tracker.statistic.entity.UserStats;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Singleton
public class TrackerUserService {

    @Inject
    TrackerStatisticService trackerStatisticService;

    public List<UserStats> getUserDayStats(String user, String stringDate) {
        return trackerStatisticService.getUsersStatsByDateString(stringDate)
                .stream().filter(s -> s.getUser().equals(user))
                .collect(Collectors.toList());
    }

    public List<UserStats> getUserMonthStats(String user, String stringDate) {
        return trackerStatisticService.getUsersMonthlyStats(stringDate)
                .stream().filter(s -> s.getUser().equals(user))
                .collect(Collectors.toList());
    }
}
