package pl.edu.pwr.wordnetloom.server.business.tracker.statistic.boundary;

import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerEntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.tracker.statistic.control.TrackerStatisticService;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("tracker")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrackerStatisticResource {

    @Inject
    TrackerStatisticService trackerStatisticService;

    @Inject
    TrackerEntityBuilder trackerEntityBuilder;

    @GET
    @Path("stats/today")
    public JsonObject createdToday() {
        return trackerEntityBuilder.buildUsersDailyStats(trackerStatisticService.getUsersStatsToday());
    }

    @GET
    @Path("stats/from/{begin}/to/{end}")
    public JsonObject statsByTimestamps(@PathParam("begin") long begin, @PathParam("end") long end) {
        return trackerEntityBuilder.buildUsersMonthlyStats(
                trackerStatisticService.getUsersStatsByTimestampsGroupByHours(begin, end)
        );
    }

    @GET
    @Path("stats/dates/from/{begin}/to/{end}")
    public JsonObject statsByDates(@PathParam("begin") String dateStringBegin, @PathParam("end") String dateStringEnd) {
        return trackerEntityBuilder.buildUsersMonthlyStats(
                trackerStatisticService.getUsersStatsBetweenDates(dateStringBegin, dateStringEnd)
        );
    }

    @GET
    @Path("stats/date/{date_string}")
    public JsonObject statsByDate(@PathParam("date_string") String dateString) {
        return trackerEntityBuilder.buildUsersDailyStats(trackerStatisticService.getUsersStatsByDateString(dateString));
    }
}
