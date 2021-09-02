package pl.edu.pwr.wordnetloom.server.business.tracker.users.boundary;

import pl.edu.pwr.wordnetloom.server.business.revisions.control.RevisionsQueryService;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerEntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.tracker.users.control.TrackerUserService;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("tracker/users")
@Produces(MediaType.APPLICATION_JSON)
public class TrackerUsersResource {

    @Inject
    RevisionsQueryService revisionsQueryService;

    @Inject
    TrackerUserService trackerUserService;

    @Inject
    TrackerEntityBuilder trackerEntityBuilder;

    @GET
    public List<String> getAllUsers() {
        return revisionsQueryService.findAllUsersEmails();
    }


    @GET
    @Path("{user}/daily-activity/{date}")
    public JsonObject dailyActivity(@PathParam("date") String date,
                                         @PathParam("user") String user) {
        return trackerEntityBuilder.buildUserDailyStats(trackerUserService.getUserDayStats(user, date));
    }

    @GET
    @Path("{user}/monthly-activity/{date}")
    public JsonObject monthlyActivity(@PathParam("date") String date,
                                    @PathParam("user") String user) {
        return trackerEntityBuilder.buildUserMonthlyStats(trackerUserService.getUserMonthStats(user, date));
    }
}
