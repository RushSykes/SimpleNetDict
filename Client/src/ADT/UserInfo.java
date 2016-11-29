package ADT;

import java.io.Serializable;

public class UserInfo implements Serializable {
    // 0 for logIn
    // 1 for signUp
    // 2 for query
    // 3 for thumb up
    // To be added more for int mode
    private int mode;
    private String userName;
    private String password;
    private String confirm;

    private int queryType;
    private String word;
    private Youdao resultYoudao;

    public UserInfo(String userName, String password, int mode) {
        this.userName = userName;
        this.password = password;
        this.mode = mode;
    }

    public UserInfo(String word, int mode, int type) {
        this.word = word;
        this.mode = mode;
        this.queryType = type;
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

    public int getQueryType() {
        return queryType;
    }

    public String getWord() {
        return word;
    }

    public Object getResult(int type) {
        if(queryType == 0)
            return resultYoudao;
        else
            return null;
    }

    public void setResult(Object result) {
        if(getQueryType() == 0)
            resultYoudao = (Youdao)result;
    }
}
