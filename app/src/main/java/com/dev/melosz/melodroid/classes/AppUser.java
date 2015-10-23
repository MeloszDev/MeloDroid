package com.dev.melosz.melodroid.classes;

import java.io.Serializable;

/**
 *
 * Created by marek.kozina on 8/25/2015.
 * Entity class for storing users and their account & limited personal information.
 *
 */
public class AppUser extends User implements Serializable {
    /**
     * Fields
     */
    private int id;
    private String userName;
    private String password;
    private int score;
    private boolean logged;

    /**
     * Default constructors
     */
    public AppUser() {}
    /**
     * Generic required fields constructor
     * @param userName String
     * @param email String the super User email
     * @param phoneNumber String the super User phoneNumber
     * @param zip String the super User zip
     */
    public AppUser(String userName, String email, String phoneNumber, String zip) {

        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.zip = zip;
    }

    /**
     * Constructor with all fields except ID.  Used when making a new entry
     *
     * @param userName String userName
     * @param password String password
     * @param email String email (super)
     * @param phoneNumber String phoneNumber (super)
     * @param zip String zip (super)
     * @param score int score
     * @param logged int saved as bool
     */
    public AppUser(String userName, String password, String email, String phoneNumber, String zip,
                   int score, int logged) {

        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.zip = zip;
        this.score = score;
        this.logged = (logged != 0);
    }
    /**
     * Constructor with all fields with ID. Used for existing entries
     *
     * @param id int (Unique)
     * @param userName String userName
     * @param password String password
     * @param email String email (super)
     * @param phoneNumber String phoneNumber (super)
     * @param zip String zip (super)
     * @param score int score
     * @param logged int saved as bool
     */
    public AppUser(int id, String userName, String password, String email, String phoneNumber,
                   String zip, int score, int logged) {

        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.zip = zip;
        this.score = score;
        this.logged = (logged != 0);
    }

    /**
     * Gets the id
     *
     * @return id
     */
    public int getId() {

        return id;
    }
    /**
     * Sets the id
     *
     * @param id int
     */
    public void setId(int id) {

        this.id = id;
    }
    /**
     * Gets the userName
     *
     * @return String
     */
    public String getUserName() {

        return userName;
    }
    /**
     * Sets the userName
     *
     * @param userName String
     */
    public void setUserName(String userName) {

        this.userName = userName;
    }
    /**
     * Gets the password
     *
     * @return String
     */
    public String getPassword() {

        return password;
    }
    /**
     * Sets the password
     *
     * @param password String
     */
    public void setPassword(String password) {

        this.password = password;
    }
    /**
     * Gets the Memory Game high score for the user
     * @return int
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the Memory Game high score for the user
     * @param score int
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the user's logged state
     *
     * @return boolean
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * Sets the flag for the user to be logged in or not
     *
     * @param logged boolean
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }
}
