package pl.edu.pwr.wordnetloom.client.ui.alerts;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.controlsfx.control.Notifications;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;

import javax.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;

@Singleton
public class AlertDialogHandler {

    public void onShowErrorMsg(Exception ex){
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

    public void onShowValidationErrorMsg(ValidationException ex){
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
