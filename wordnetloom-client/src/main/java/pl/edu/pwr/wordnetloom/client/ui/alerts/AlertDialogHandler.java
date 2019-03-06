package pl.edu.pwr.wordnetloom.client.ui.alerts;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.service.ForbiddenException;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;

import javax.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;

@Singleton
public class AlertDialogHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AlertDialogHandler.class);

    public void handleErrors(Exception e){
        if(e instanceof ValidationException){
            onShowValidationErrorMsg((ValidationException) e);
            LOG.error("Validation:", e);
        }else if(e instanceof ForbiddenException){
            onShowSecurityError((ForbiddenException) e);
            LOG.error("Security:", e);
        }else{
            onShowErrorMsg(e);
            LOG.error("Error:", e);
        }
    }

    private void onShowErrorMsg(Exception ex){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("System error occurred");
        alert.setContentText("We are sorry but something went wrong");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        TextArea moreDetails = new TextArea(sw.toString());
        alert.getDialogPane().setExpandableContent(moreDetails);
        alert.showAndWait();
    }
    private void onShowSecurityError(ForbiddenException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Security warning");
        alert.setHeaderText(null);
        alert.setContentText(ex.getMessege());
        alert.showAndWait();
    }
    private void onShowValidationErrorMsg(ValidationException ex){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation failed");
        alert.setHeaderText(null);
        alert.setContentText("Operation failed because of validation errors");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.getErrors().entrySet().forEach((e -> pw.println(e.getValue())));

        TextArea moreDetails = new TextArea(sw.toString());
        moreDetails.setPrefRowCount(5);
        alert.getDialogPane().setExpandableContent(moreDetails);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }

    public void onShowSuccessNotification(String text){
        Notifications.create()
                .title("Operation successful")
                .darkStyle()
                .position(Pos.CENTER)
                .text(text).showInformation();
    }
}
