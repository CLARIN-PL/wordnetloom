package pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.inject.Inject;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ChangePasswordDialogViewModel implements ViewModel {

    @Inject
    RemoteService service;

    private Command saveCommand;

    private StringProperty newPassword = new SimpleStringProperty();
    private StringProperty confirmPassword = new SimpleStringProperty();

    private Validator newPasswordValidator;
    private Validator confirmPasswordValidator;

    private final CompositeValidator formValidator = new CompositeValidator();

    private StringProperty title = new SimpleStringProperty("Change password");

    public void initialize() {
        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                save();
            }
        });
    }

    public ChangePasswordDialogViewModel(){
        newPasswordValidator = new FunctionBasedValidator<>(
                newPasswordProperty(),
                np -> np != null && !np.trim().isEmpty() && np.length() > 7,
                ValidationMessage.error("Password may not be empty and must have min 8 character length"));

        confirmPasswordValidator= new FunctionBasedValidator<>(
                confirmPasswordProperty(),
                cp -> cp != null && !cp.trim().isEmpty() && newPassword.get().equals(confirmPassword.get()),
                ValidationMessage.error("Both passwords has to be same"));

        formValidator.addValidators(
                newPasswordValidator,
                confirmPasswordValidator);
    }

    public void save(){
        if(formValidator.getValidationStatus().isValid()){
            String ps = encryptPassword(confirmPassword.get());
            try {
                service.changePassword(ps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String encryptPassword(final String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        md.update(password.getBytes());
        return Base64.getMimeEncoder().encodeToString(md.digest());
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

    public ValidationStatus getNewPasswordValidator() {
        return newPasswordValidator.getValidationStatus();
    }

    public ValidationStatus getConfirmPasswordValidator() {
        return confirmPasswordValidator.getValidationStatus();
    }

    public String getNewPassword() {
        return newPassword.get();
    }

    public StringProperty newPasswordProperty() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword.set(newPassword);
    }

    public String getConfirmPassword() {
        return confirmPassword.get();
    }

    public StringProperty confirmPasswordProperty() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword.set(confirmPassword);
    }
}

