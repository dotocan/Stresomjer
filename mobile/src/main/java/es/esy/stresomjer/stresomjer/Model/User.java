package es.esy.stresomjer.stresomjer.model;

/**
 * Created by domin on 6/9/2016.
 */
public class User {
    private String first_name;
    private String last_name;
    private int age_id;
    private String email;
    private String password;
    private String unique_id;

    // this is saved unique_id
    private String user_id;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getAgeId() {
        return age_id;
    }

    public void setAgeId(int age_id) {
        this.age_id = age_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
