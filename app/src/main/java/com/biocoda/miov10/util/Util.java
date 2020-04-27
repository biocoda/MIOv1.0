package com.biocoda.miov10.util;

public class Util {
    // User db related items
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mio_db";
    public static final String TABLE_NAME = "users";
    public static final String CAT_TABLE_NAME = "caTable";

    // create users table column names
    public static final String KEY_ID = "col_id";
    public static final String KEY_FIRSTNAME = "col_first_name";
    public static final String KEY_LASTNAME = "col_last_name";
    public static final String KEY_EMAIL = "col_email";
    public static final String KEY_PASSWORD = "col_password";

    // create current account table column names
    public static final String KEY_CAT_ID = "col_cat_id";
    public static final String KEY_CAT_AMOUNT = "col_cat_amount";
    public static final String KEY_CAT_DORC = "col_cat_dorc";
    public static final String KEY_CAT_TYPE = "col_cat_type";
    public static final String KEY_CAT_DATE_ADDED = "col_cat_date";
    public static final String KEY_CAT_DESCRIPTION = "col_cat_description";

}

