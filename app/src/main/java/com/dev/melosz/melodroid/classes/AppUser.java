package com.dev.melosz.melodroid.classes;

import java.io.Serializable;

/**
 * Class for an AppUser which will eventually be stored in a SQLite DB
 *
 * Created by marek.kozina on 8/25/2015.
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
    private String city;
    private String state;
    private String zip;
    private boolean logged;

    /**
     * Default constructors
     */
    public AppUser() {}
    /**
     * @param userName
     */
    public AppUser(String userName) {
        this.userName = userName;
    }

    /**
     * Constructor with all fields except ID.  Used when making a new entry
     *
     * @param userName
     * @param password
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param address
     * @param city
     * @param state
     * @param zip
     */
    public AppUser(String userName, String password, String firstName, String lastName, String email,
                   String phoneNumber, String address, String city, String state, String zip,
                   int logged) {
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
        this.logged = (logged != 0);
    }
    /**
     * Constructor with all fields with ID. Used for existing entries
     *
     * @param id
     * @param userName
     * @param password
     * @param firstName
     * @param lastName
     * @param email
     * @param phoneNumber
     * @param address
     * @param city
     * @param state
     * @param zip
     */
    public AppUser(int id, String userName, String password, String firstName, String lastName,
                   String email, String phoneNumber, String address, String city, String state,
                   String zip, int logged) {
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
     * @param id
     */
    public void setId(int id) {

        this.id = id;
    }
    /**
     * Gets the userName
     *
     * @return
     */
    public String getUserName() {

        return userName;
    }
    /**
     * Sets the userName
     *
     * @param userName
     */
    public void setUserName(String userName) {

        this.userName = userName;
    }
    /**
     * Gets the password
     *
     * @return
     */
    public String getPassword() {

        return password;
    }
    /**
     * Sets the password
     *
     * @param password
     */
    public void setPassword(String password) {

        this.password = password;
    }
    /**
     * Gets the firstName
     * @return
     */
    public String getFirstName() {

        return firstName;
    }
    /**
     * Sets the firstName
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }
    /**
     * Gets the lastName
     *
     * @return
     */
    public String getLastName() {

        return lastName;
    }
    /**
     * Sets the lastName
     *
     * @param lastName
     */
    public void setLastName(String lastName) {

        this.lastName = lastName;
    }
    /**
     * Gets the email
     *
     * @return
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the email
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Gets the phoneNumber
     *
     * @return
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * Sets the phoneNumber
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /**
     * Gets the address
     *
     * @return
     */
    public String getAddress() {
        return address;
    }
    /**
     * Sets the address
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * Gets the city
     *
     * @return
     */
    public String getCity() {
        return city;
    }
    /**
     * Sets the city
     *
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * Gets the state
     *
     * @return
     */
    public String getState() {
        return state;
    }
    /**
     * Sets the state
     *
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * Gets the zip
     *
     * @return
     */
    public String getZip() {
        return zip;
    }
    /**
     * Sets the zip
     *
     * @param zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * Gets the user's logged state
     *
     * @return
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * Sets the flag for the user to be logged in or not
     *
     * @param logged
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }
}
