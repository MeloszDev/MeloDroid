package com.dev.melosz.melodroid.classes;

/**
 * Created by marek.kozina on 10/19/2015.
 * TODO: May not use this route...
 */
public class PhoneNumber {
    private String number;
    private String type;

    private enum PhoneType {
        MOBILE("Mobile"),
        HOME("Home"),
        WORK("Work"),
        WORK_FAX("Work Fax"),
        HOME_FAX("Home Fax"),
        PAGER("Pager"),
        OTHER("Other");

        private final String value;

        private PhoneType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type.getValue();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
