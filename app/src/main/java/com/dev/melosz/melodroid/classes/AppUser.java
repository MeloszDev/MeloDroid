package com.dev.melosz.melodroid.classes;

import java.io.Serializable;

/**
 *
 * Created by marek.kozina on 8/25/2015.
 * Entity class for storing users and their account & limited personal information.
 *
 */
public class AppUser implements Serializable {
    /**
     * Fields
     */
    private int id;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private int score;
    private boolean logged;

    /**
     * Default constructors
     */
    public AppUser() {}
    /**
     * @param userName String
     */
    public AppUser(String userName) {
        this.userName = userName;
    }

    /**
     * Constructor with all fields except ID.  Used when making a new entry
     *
     * @param userName String
     * @param password String
     * @param firstName String
     * @param lastName String
     * @param email String
     * @param phoneNumber String
     * @param address String
     * @param city String
     * @param state String
     * @param zip String
     * @param score int
     * @param logged int saved as bool
     */
    public AppUser(String userName, String password, String firstName, String lastName, String email,
                   String phoneNumber, String address, String city, String state, String zip,
                   int score, int logged) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.score = score;
        this.logged = (logged != 0);
    }
    /**
     * Constructor with all fields with ID. Used for existing entries
     *
     * @param id int (Unique)
     * @param userName String
     * @param password String
     * @param firstName String
     * @param lastName String
     * @param email String
     * @param phoneNumber String
     * @param address String
     * @param city String
     * @param state String
     * @param zip String
     * @param score int
     * @param logged int saved as bool
     */
    public AppUser(int id, String userName, String password, String firstName, String lastName,
                   String email, String phoneNumber, String address, String city, String state,
                   String zip, int score, int logged) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
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
     * Gets the firstName
     * @return String
     */
    public String getFirstName() {

        return firstName;
    }
    /**
     * Sets the firstName
     *
     * @param firstName String
     */
    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }
    /**
     * Gets the lastName
     *
     * @return String
     */
    public String getLastName() {

        return lastName;
    }
    /**
     * Sets the lastName
     *
     * @param lastName String
     */
    public void setLastName(String lastName) {

        this.lastName = lastName;
    }
    /**
     * Gets the email
     *
     * @return String
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the email
     *
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Gets the phoneNumber
     *
     * @return String
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * Sets the phoneNumber
     *
     * @param phoneNumber String
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /**
     * Gets the address
     *
     * @return String
     */
    public String getAddress() {
        return address;
    }
    /**
     * Sets the address
     *
     * @param address String
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * Gets the address2
     *
     * @return String
     */
    public String getAddress2() {
        return address2;
    }
    /**
     * Sets the address2
     *
     * @param address2 String
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Gets the city
     *
     * @return String
     */
    public String getCity() {
        return city;
    }
    /**
     * Sets the city
     *
     * @param city String
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * Gets the state
     *
     * @return String
     */
    public String getState() {
        return state;
    }
    /**
     * Sets the state
     *
     * @param state String
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * Gets the zip
     *
     * @return String
     */
    public String getZip() {
        return zip;
    }
    /**
     * Sets the zip
     *
     * @param zip String
     */
    public void setZip(String zip) {
        this.zip = zip;
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
