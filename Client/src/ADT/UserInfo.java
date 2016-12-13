package ADT;

import java.io.Serializable;

public class UserInfo implements Serializable {
    // 0 for logIn
    // 1 for signUp
    // 2 for query
    // 3 for thumb up
    // 4 for read real time info
    // 5 for read real time info complete
    // 6 and 7 for logout(exit the program)
    // 8 and 9 for sending/receiving picture data
    // To be added more for int mode
    private int mode;
    private String userName;
    private String password;
    private String confirm;

    private int queryType;
    private int likeType;
    private boolean liked;
    private String word;
    private String result;
    private int dictScore;

    private byte[] picData;

    // For signup and login
    public UserInfo(String userName, String password, int mode) {
        this.userName = userName;
        this.password = password;
        this.mode = mode;
    }

    // For query a word
    public UserInfo(String word, int mode, int type) {
        this.word = word;
        this.mode = mode;
        this.queryType = type;
    }

    // For thumb up or query
    public UserInfo(String word, String user, int mode, int type) {
        this.word = word;
        this.userName = user;
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

    public void setQueryType(int type) {
        this.queryType = type;
    }

    public String getWord() {
        return word;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikeType() {
        return likeType;
    }

    public void setLikeType(int option) {
        likeType = option;
    }

    public int getDictScore() {
        return dictScore;
    }

    public void setDictScore(int score) {
        this.dictScore = score;
    }

    public byte[] getPicData() {
        return picData;
    }

    public void setPicData(byte[] picData) {
        this.picData = picData;
    }
}
