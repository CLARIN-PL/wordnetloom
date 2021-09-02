package pl.edu.pwr.wordnetloom.server.business.tracker.statistic.control;

import pl.edu.pwr.wordnetloom.server.business.revisions.control.RevisionsQueryService;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerDateService;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.control.SenseHistoryQueryService;
import pl.edu.pwr.wordnetloom.server.business.tracker.statistic.entity.UserStats;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.control.SynsetHistoryQueryService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;

@Singleton
@Transactional
public class TrackerStatisticService {
    public static final Logger LOG = Logger.getLogger(TrackerStatisticService.class.getName());

    @Inject
    SenseHistoryQueryService senseHistoryQueryService;

    @Inject
    SynsetHistoryQueryService synsetHistoryQueryService;

    @Inject
    RevisionsQueryService revisionsQueryService;

    @Inject
    TrackerDateService dateService;

    public List<UserStats> getUsersStatsByTimestampsGroupByHours(long begin, long end) {
        List<RevisionsInfo> users = revisionsQueryService.findAllUsersEmailsByTimestamps(begin, end);
        Map<String, UserStats> userStatsMap = new HashMap<>();
        users.forEach(u -> userStatsMap.put(u.getUserWithDateHour(), new UserStats(u)));

        senseHistoryQueryService.findSenseHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateHour()).addSenseHistory(s));
        senseHistoryQueryService.findSenseRelationHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateHour()).addSenseRelationsHistory(s));
        synsetHistoryQueryService.findSynsetHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateHour()).addSynsetHistory(s));
        synsetHistoryQueryService.findSynsetRelationHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateHour()).addSynsetRelationsHistory(s));

        return new ArrayList<>(userStatsMap.values());
    }

    public List<UserStats> getUsersStatsByTimestampsGroupByDays(long begin, long end) {
        List<RevisionsInfo> users = revisionsQueryService.findAllUsersEmailsByTimestamps(begin, end);
        Map<String, UserStats> userStatsMap = new HashMap<>();
        users.forEach(u -> userStatsMap.put(u.getUserWithDateDay(), new UserStats(u)));

        senseHistoryQueryService.findSenseHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateDay()).addSenseHistory(s));
        senseHistoryQueryService.findSenseRelationHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateDay()).addSenseRelationsHistory(s));
        synsetHistoryQueryService.findSynsetHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateDay()).addSynsetHistory(s));
        synsetHistoryQueryService.findSynsetRelationHistoryByTimestamps(begin, end)
                .forEach(s -> userStatsMap.get(s.getRevisionsInfo().getUserWithDateDay()).addSynsetRelationsHistory(s));

        return new ArrayList<>(userStatsMap.values());
    }

    public List<UserStats> getUsersStatsToday() {
        return getUsersStatsByTimestampsGroupByHours(
                dateService.getBeginOfDate(dateService.getTodayDateTime()),
                dateService.getEndOfDate(dateService.getTodayDateTime())
        );
    }

    public List<UserStats> getUsersStatsByDateString(String dateString) {
        return getUsersStatsBetweenDates(dateString, dateString);
    }

    public List<UserStats> getUsersStatsBetweenDates(String dateStringBegin, String dateStringEnd) {
        try {
            Date dateBegin = dateService.parseStringDateToDate(dateStringBegin);
            Date dateEnd = dateService.parseStringDateToDate(dateStringEnd);
            long begin = dateService.getBeginOfDate(dateService.getLocalDateTime(dateBegin));
            long end = dateService.getEndOfDate(dateService.getLocalDateTime(dateEnd));

            return getUsersStatsByTimestampsGroupByDays(begin, end);
        } catch (Exception e) {
            LOG.warning("Wrong date format! date = " + dateStringBegin + "  " + dateStringEnd);
        }

        return new LinkedList<>();
    }

    public List<UserStats> getUsersMonthlyStats(String dateString) {
        try {
            Date date = dateService.parseStringDateToDate(dateString);
            long begin = dateService.getBeginOfMonth(date);
            long end = dateService.getEndOfMonth(date);

            return getUsersStatsByTimestampsGroupByDays(begin, end);
        } catch (Exception e) {
            LOG.warning("Wrong date format! date = " + dateString);
        }

        return new LinkedList<>();
    }
}
