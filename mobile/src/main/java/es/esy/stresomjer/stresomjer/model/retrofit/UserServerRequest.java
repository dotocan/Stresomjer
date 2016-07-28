package es.esy.stresomjer.stresomjer.model.retrofit;

import es.esy.stresomjer.stresomjer.model.User;

/**
 * Created by domin on 6/9/2016.
 */
public class UserServerRequest {
    private String operation;
    private User user;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
