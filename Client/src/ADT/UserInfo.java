package Dict.ADT;

import java.io.Serializable;

public class UserInfo implements Serializable {
    // 0 for logIn
    // 1 for signUp
    // To be added more for int mode
    private int mode;
    private String userName;
    private String password;
    private String confirm;

    public UserInfo(String userName, String password, int mode) {
        this.userName = userName;
        this.password = password;
        this.mode = mode;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public int getMode() {
        return mode;
    }
}
