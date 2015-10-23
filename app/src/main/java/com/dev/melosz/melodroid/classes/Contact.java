package com.dev.melosz.melodroid.classes;

import java.io.Serializable;

/**
 * Created by marek.kozina on 10/19/2015.
 * Entity class for storing Contacts and their related AppUser.
 */
public class Contact extends User implements Serializable {

    private int contact_id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String description;
    private String type;

    // UUID for checking if Contact should be created or updated
    private String uuID;

    // foreign key linking the contact to it's AppUser
    private int user_id;

    /**
     * Default empty constructor
     */
    public Contact() {}

    /**
     * Constructor with all fields except contact_id. Used for new entries
     * @param firstName String firstName
     * @param middleName String middleName
     * @param lastName String lastName
     * @param email String  email (super)
     * @param phoneNumber String phoneNumber (super)
     * @param address String address (super)
     * @param address2 String address2 (super)
     * @param city String city (super)
     * @param state String state (super)
     * @param zip String zip (super)
     * @param description String description
     * @param type String type
     * @param user_id int the id of the associated user
     */
    public Contact(String firstName, String middleName, String lastName, String email,
                   String phoneNumber, String address, String address2, String city, String state,
                   String zip, String description, String type, String uuID, int user_id) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.description = description;
        this.type = type;
        this.uuID = uuID;
        this.user_id = user_id;
    }

    /**
     * Constructor with all fields with contact_id. Used for existing entries
     * @param contact_id int the id (Unique)
     * @param firstName String firstName
     * @param middleName String middleName
     * @param lastName String lastName
     * @param email String  email (super)
     * @param phoneNumber String phoneNumber (super)
     * @param address String address (super)
     * @param address2 String address2 (super)
     * @param city String city (super)
     * @param state String state (super)
     * @param zip String zip (super)
     * @param description String description
     * @param type String type
     * @param user_id int the id of the associated user
     */
    public Contact(int contact_id, String firstName, String middleName, String lastName, String email,
                   String phoneNumber, String address, String address2, String city, String state,
                   String zip, String description, String type, String uuID, int user_id) {
        this.contact_id = contact_id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.description = description;
        this.type = type;
        this.uuID = uuID;
        this.user_id = user_id;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUuID() {
        return uuID;
    }

    public void setUuID(String uuID) {
        this.uuID = uuID;
    }
}
