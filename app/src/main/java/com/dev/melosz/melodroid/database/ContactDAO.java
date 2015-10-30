package com.dev.melosz.melodroid.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;

import com.dev.melosz.melodroid.classes.Contact;
import com.dev.melosz.melodroid.utils.AppUtil;
import com.dev.melosz.melodroid.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by marek.kozina on 10/19/2015.
 * Subclass of the AbstractDAO to handle all Contact related database operations.
 *
 */
public class ContactDAO extends AbstractDAO {
    // Logging controls
    private LogUtil log = new LogUtil();
    private static final String TAG = ContactDAO.class.getSimpleName();
    private String METHOD;
    // Set to false to suppress logging
    private static final boolean DEBUG = false;

    // The Application Context
    private Context mCTX;

    // Table names from Contract Classes
    private final String CONTACT_TABLE_NAME = ContactContract.ContactEntry.TABLE_NAME;
    private final String USER_TABLE_NAME = AppUserContract.AppUserEntry.TABLE_NAME;

    // Key String constants for Contact Table
    private final String KEY_ID = ContactContract.ContactEntry.COL_CONTACT_ID;
    private final String KEY_USER_ID = ContactContract.ContactEntry.COL_USER_ID;
    private final String KEY_FIRSTNAME = ContactContract.ContactEntry.COL_FIRSTNAME;
    private final String KEY_MIDDLENAME = ContactContract.ContactEntry.COL_LASTNAME;
    private final String KEY_LASTNAME = ContactContract.ContactEntry.COL_LASTNAME;
    private final String KEY_EMAIL = ContactContract.ContactEntry.COL_EMAIL;
    private final String KEY_PHONENUMBER = ContactContract.ContactEntry.COL_PHONENUMBER;
    private final String KEY_ADDRESS = ContactContract.ContactEntry.COL_ADDRESS;
    private final String KEY_ADDRESS2 = ContactContract.ContactEntry.COL_ADDRESS2;
    private final String KEY_CITY = ContactContract.ContactEntry.COL_CITY;
    private final String KEY_STATE = ContactContract.ContactEntry.COL_STATE;
    private final String KEY_ZIP = ContactContract.ContactEntry.COL_ZIP;
    private final String KEY_DESCRIPTION = ContactContract.ContactEntry.COL_DESCRIPTION;
    private final String KEY_TYPE = ContactContract.ContactEntry.COL_TYPE;
    private final String KEY_UUID = ContactContract.ContactEntry.COL_UUID;

    /**
     * Assign the Singleton DatabaseHelper for this DAO
     * @param context the Activity Context
     */
    public ContactDAO(Context context) {
        super(context);
        mCTX = context;
    }

    /**
     * Method: saveNewContact
     * Description: Makes a new Contact from the syncContactsWithPhone method and saves it to the DB
     *
     * @param contact Contact the new Contact to save
     * @return the newly saved contact
     */
    public Contact saveNewContact(Contact contact){
        ContentValues cvs = makeContactCVs(contact);
        cvs.put(KEY_UUID, AppUtil.assignUUID());

        // Call the generic put method which will save a new Contact based on the cvs
        super.put(cvs, CONTACT_TABLE_NAME);

        return contact;
    }

    /**
     * Updates an existing contact
     *
     * @param contact the Contact to update
     */
    public void updateContact(Contact contact) {
        METHOD = "updateContact()";
        // make cvs for the DB to read/write
        ContentValues cvs = makeContactCVs(contact);

        // Attempt to update the user based on id
        try {
            database.update(CONTACT_TABLE_NAME, cvs, KEY_ID + "=" + contact.getContact_id(), null);
        }
        catch (SQLiteException e) {
            if(DEBUG) log.e(TAG, METHOD, e.getMessage());
        }
        finally {
            if(DEBUG) log.i(TAG, METHOD, contact.getFirstName()
                    + " account updated. Fields below: " + AppUtil.prettyPrintObject(contact));
        }
    }

    public List<Contact> getContactsByUserID(int userID){
        METHOD = "getContactsByUserID()";
        List<Contact> contactList = new ArrayList<>();

        String[] where = new String[]{String.valueOf(userID)};

        Cursor c = database.query(CONTACT_TABLE_NAME,
                null,
                KEY_USER_ID + "=?",
                where,
                null, null, null);

        while (c.moveToNext()){
            Contact newContact = makeContactByCursor(c);
            contactList.add(newContact);

            if(DEBUG) log.i(TAG, METHOD, "Adding Contact: [" +
                    c.getString(c.getColumnIndex(KEY_FIRSTNAME)) + "]");
        }
        return contactList;
    }

    public List<Contact> getAllContacts(){
        METHOD = "getAllContacts()";
        List<Contact> contactList = new ArrayList<>();
        String sql = "SELECT * FROM " + CONTACT_TABLE_NAME;
        Cursor c = database.rawQuery(sql, null);

        // If the query was successful, execute for each entry
        if (c != null && c.moveToFirst()) {
            while (!c.isAfterLast()) {
                // For each entity, make a new Contact and add it to the contactList
                Contact contact = makeContactByCursor(c);
                contactList.add(contact);
                c.moveToNext();
            }
        }
        if(c != null) c.close();

        return contactList;
    }
    /**
     * Gets an Contact entity from the db by the first name
     *
     * @param name the Contact Name String
     * @return the retrieved Contact or null
     */
    public Contact getContactByName(String name, int id) {
        METHOD = "getContactByName()";

        Contact setContact = null;
        if(name != null) {
            // Build SQL String
            String sql = "SELECT * FROM " + CONTACT_TABLE_NAME
                    + " WHERE " + KEY_FIRSTNAME + " = '" + name
                    + "' AND " + KEY_USER_ID + " = '"+id+"';";
            if(DEBUG) log.i(TAG, METHOD, "SQL QUERY: '" + sql + ".");

            Cursor c = database.rawQuery(sql, null);

            // Assign Contact if name exists
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                // Build existing user by passing the ID
                setContact = makeContactByCursor(c);

                // If no UUID exists for a particular name, return null
                if(setContact.getUuID() == null){
                    return null;
                }

                if(DEBUG) log.i(TAG, METHOD, "Contact [" + setContact.getFirstName() +
                        "] Obtained. Fields below:\n." + AppUtil.prettyPrintObject(setContact));

                c.close();
            }
            else if(DEBUG) log.i(TAG,  METHOD, "Contact [" + name + "] Not found.");
        }
        return setContact;
    }

    /**
     * Deletes a specified Contact from the database
     * @param contact Contact to be deleted
     * @return boolean whether or not the delete was successful
     */
    public boolean deleteContact(Contact contact) {
        METHOD = "deleteContact()";
        try
        {
            if(DEBUG) log.i(TAG, METHOD, " Attempting to delete user: [" + contact.getFirstName()
                    + "] from table [" + USER_TABLE_NAME + "].");
            return database.delete(
                    CONTACT_TABLE_NAME,
                    KEY_ID + "=?",
                    new String[]{Integer.toString(contact.getContact_id())}) > 0;
        }
        catch(SQLiteException e)
        {
            if(DEBUG) log.e(TAG, METHOD, e.getLocalizedMessage());
            return false;
        }
    }
    /**
     * Checks if a Contact exists for the AppUser and inserts or updates accordinly
     * @param contact String the Contact
     * @param id the Contact foreign key user_id
     */
    private void saveOrUpdateContact(Contact contact, int id) {
        METHOD = "saveOrUpdateContact()";

        // Get Contact by firstName and the foreign key user_id
        Contact newContact = getContactByName(contact.getFirstName(), id);
        boolean exists = (newContact != null);

        // Update if the Contact exists for the user
        if(exists) {
            updateContact(newContact);
            if(DEBUG) log.i(USER_TABLE_NAME, METHOD, "Found Contact: [" + contact + "].");
        }
        // Insert if the Contact is new for the user
        else {
            saveNewContact(contact);
            if(DEBUG) log.i(USER_TABLE_NAME, METHOD, "Saving New Contact: [" + contact + "].");
        }
    }
    /**
     * Builds the necessary ContentValues for saving/updating a Contact
     * @param contact the Contact
     * @return the ContentValues for a Contact entity
     */
    private ContentValues makeContactCVs(Contact contact) {
        ContentValues cvs = new ContentValues();
        // KEY_ID is generated so it is not populated as a ContentValue
        cvs.put(KEY_USER_ID, contact.getUser_id());
        cvs.put(KEY_FIRSTNAME, contact.getFirstName());
        cvs.put(KEY_MIDDLENAME, contact.getMiddleName());
        cvs.put(KEY_LASTNAME, contact.getLastName());
        cvs.put(KEY_EMAIL, contact.getEmail());
        cvs.put(KEY_PHONENUMBER, contact.getPhoneNumber());
        cvs.put(KEY_ADDRESS, contact.getAddress());
        cvs.put(KEY_ADDRESS2, contact.getAddress2());
        cvs.put(KEY_CITY, contact.getCity());
        cvs.put(KEY_STATE, contact.getState());
        cvs.put(KEY_ZIP, contact.getZip());
        cvs.put(KEY_DESCRIPTION, contact.getDescription());
        cvs.put(KEY_TYPE, contact.getType());
        cvs.put(KEY_UUID, contact.getUuID());
        return cvs;
    }

    /**
     * Builds an existing Contact
     * @param c the query Cursor
     * @return Contact the existing Contact
     */
    private Contact makeContactByCursor(Cursor c){
        return new Contact(
                c.getInt(c.getColumnIndex(KEY_ID)),
                c.getString(c.getColumnIndex(KEY_FIRSTNAME)),
                c.getString(c.getColumnIndex(KEY_MIDDLENAME)),
                c.getString(c.getColumnIndex(KEY_LASTNAME)),
                c.getString(c.getColumnIndex(KEY_EMAIL)),
                c.getString(c.getColumnIndex(KEY_PHONENUMBER)),
                c.getString(c.getColumnIndex(KEY_ADDRESS)),
                c.getString(c.getColumnIndex(KEY_ADDRESS2)),
                c.getString(c.getColumnIndex(KEY_CITY)),
                c.getString(c.getColumnIndex(KEY_STATE)),
                c.getString(c.getColumnIndex(KEY_ZIP)),
                c.getString(c.getColumnIndex(KEY_DESCRIPTION)),
                c.getString(c.getColumnIndex(KEY_TYPE)),
                c.getString(c.getColumnIndex(KEY_UUID)),
                c.getInt(c.getColumnIndex(KEY_USER_ID))
        );
    }

    /**
     * Accesses the devices ContactsContract to pull all of the devices Contacts and puts the
     * relevant values in a HashMap which will then be used to create or update this app's Contacts.
     * @param userID int the associated userID
     * @return List
     */
    public List<Contact> syncContactsWithPhone(int userID) {
        METHOD = "synchContactsWithPhone()";
        // HashMap and list to store and map the Contacts retrieved
        HashMap<String, String> contactMap ;
        List<HashMap> mapList = new ArrayList<>();

        // Initial query parameter
        Uri contentURI = ContactsContract.Contacts.CONTENT_URI;

        // Content resolver to query
        ContentResolver cr = mCTX.getContentResolver();

        // Initial query to loop through all contacts stored on the device
        Cursor c = cr.query(contentURI, null, null, null, null);

        if(c.getCount() > 0) {
            while(c.moveToNext()) {
                contactMap = new LinkedHashMap<>();
                String hasPhoneNumber = ContactsContract.Contacts.HAS_PHONE_NUMBER;
                // Only map the contact if a phone number exists
                if(Integer.parseInt(c.getString(c.getColumnIndex(hasPhoneNumber))) > 0) {
                    // The fields which will attempt to be retrieved from Android's ContactsContract
                    String  firstName, middleName, lastName,
                            phoneNumber,
                            email,
                            city, street, region, zip;

                    // Name query params
                    Uri nameURI = ContactsContract.Data.CONTENT_URI;
                    String contentType =
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
                    String whereName = ContactsContract.Data.MIMETYPE + " = ? AND "
                            + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
                    String givenName = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME;

                    // Phone query params
                    Uri phoneContentURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String wherePhone = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

                    // Email query params
                    Uri emailContentURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                    String whereEmail = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";

                    // Address query params
                    Uri addressURI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
                    String whereAddress =
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?";

                    // Hold the contactID so we can query for each user individually
                    String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                    // Query and loop for the names for this contact
                    Cursor nameCur = cr.query(nameURI,
                            null,
                            whereName,
                            new String[]{ contentType, contactID },
                            givenName);

                    while (nameCur.moveToNext()){
                        String name =
                                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME;
                        String midName =
                                ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME;
                        String familyName =
                                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME;

                        firstName = nameCur.getString(nameCur.getColumnIndex(name));
                        middleName = nameCur.getString(nameCur.getColumnIndex(midName));
                        lastName = nameCur.getString(nameCur.getColumnIndex(familyName));

                        // Only add the first occurrence of the names in case their are multiples
                        if(firstName != null) {
                            contactMap.put(KEY_FIRSTNAME, firstName);
                            if(middleName != null) {
                                contactMap.put(KEY_MIDDLENAME, middleName);
                            }
                            if(lastName != null) {
                                contactMap.put(KEY_LASTNAME, lastName);
                            }
                            break;
                        }
                    }
                    nameCur.close();

                    // Query and loop for every phone number of the contact
                    Cursor phoneCur = cr.query(phoneContentURI,
                            null,
                            wherePhone,
                            new String[] {contactID},
                            null);

                    if(phoneCur.getCount() == 0) {
                        contactMap.put("phoneNumber", null);
                    }
                    else {
                        String number = ContactsContract.CommonDataKinds.Phone.NUMBER;

                        // List in case there is more than one number associated with the Contact
                        List<String> phoneList = new ArrayList<>();

                        // Loop and add all phone numbers
                        while (phoneCur.moveToNext()) {
                            phoneNumber = phoneCur.getString(phoneCur.getColumnIndex(number));
                            phoneList.add(phoneNumber);
                        }
                        // Add the phone numbers and concatenate with a comma delimiter if > 1
                        contactMap.put(KEY_PHONENUMBER, AppUtil.joinStrings(phoneList, ", "));
                    }

                    // Query and loop for every email of the contact
                    Cursor emailCur = cr.query(emailContentURI,
                            null,
                            whereEmail,
                            new String[] { contactID },
                            null);

                    if(emailCur.getCount() > 0) {
                        String data = ContactsContract.CommonDataKinds.Email.DATA;

                        // List in case there is more than one email associated with the Contact
                        List<String> emailList = new ArrayList<>();

                        // Loop and add all emails
                        while (emailCur.moveToNext()) {
                            email = emailCur.getString(emailCur.getColumnIndex(data));
                            emailList.add(email);
                        }
                        // Add the emails and concatenate with a comma delimiter if > 1
                        contactMap.put(KEY_EMAIL, AppUtil.joinStrings(emailList, ", "));
                    }
                    emailCur.close();

                    // Query and loop for address
                    Cursor addressCur = cr.query(addressURI,
                            null,
                            whereAddress,
                            new String[]{ contactID },
                            null);
                    if(addressCur.getCount() > 0) {
                        String tCity = ContactsContract.CommonDataKinds.StructuredPostal.CITY;
                        String tStreet = ContactsContract.CommonDataKinds.StructuredPostal.STREET;
                        String tRegion = ContactsContract.CommonDataKinds.StructuredPostal.REGION;
                        String tZip = ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE;

                        while(addressCur.moveToNext()){
                            city = addressCur.getString(addressCur.getColumnIndex(tCity));
                            street = addressCur.getString(addressCur.getColumnIndex(tStreet));
                            region = addressCur.getString(addressCur.getColumnIndex(tRegion));
                            zip = addressCur.getString(addressCur.getColumnIndex(tZip));

                            contactMap.put(KEY_ADDRESS, street);
                    phoneCur.close();
                            contactMap.put(KEY_CITY, city);
                            contactMap.put(KEY_STATE, region);
                            contactMap.put(KEY_ZIP, zip);
                        }

                    }
                    addressCur.close();

                    // Add the contactMap to the mapList
                    mapList.add(contactMap);
                }
            }
        }
        c.close();

        // List to store the new Contacts
        List<Contact> contactList = new ArrayList<>();

        // Map all of the HashMaps in mapList to a new contact and add to contactList
        for(HashMap map : mapList){
            Contact newContact = new Contact();
            newContact.setUser_id(userID);
            newContact.setFirstName(noNullMapping(map, KEY_FIRSTNAME));
            newContact.setMiddleName(noNullMapping(map, KEY_MIDDLENAME));
            newContact.setLastName(noNullMapping(map, KEY_LASTNAME));
            newContact.setEmail(noNullMapping(map, KEY_EMAIL));
            newContact.setPhoneNumber(noNullMapping(map, KEY_PHONENUMBER));
            newContact.setAddress(noNullMapping(map, KEY_ADDRESS));
            newContact.setCity(noNullMapping(map, KEY_CITY));
            newContact.setState(noNullMapping(map, KEY_STATE));
            newContact.setZip(noNullMapping(map, KEY_ZIP));

            contactList.add(newContact);
            if(DEBUG) log.i(TAG, METHOD, "New Contact: " + AppUtil.prettyPrintObject(newContact));
        }

        for(Contact contact : contactList){
            saveOrUpdateContact(contact, userID);
        }
        return contactList;
    }

    /**
     * Helper method to ensure null fields are not set
     * @param map The current contactMap LinkedHashMap
     * @param field String the Contact field to set
     * @return String the field or null
     */
    private String noNullMapping(HashMap map, String field){
        if (map.get(field) != null)
            return map.get(field).toString();
        else
            return null;
    }
}
