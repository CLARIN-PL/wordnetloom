package pl.edu.pwr.wordnetloom.client.service;

public class ForbiddenException extends RuntimeException {

    private String messege;

    public ForbiddenException(){
        super();
        messege = "Your are not authorize to do this operation";
    }

    public String getMessege() {
        return messege;
    }
}
