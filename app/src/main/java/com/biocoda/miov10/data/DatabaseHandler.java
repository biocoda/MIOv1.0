package com.biocoda.miov10.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.biocoda.miov10.model.CATransactions;
import com.biocoda.miov10.model.User;
import com.biocoda.miov10.util.Util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    // CREATE USER AND TRANSACTION TABLES __________________________________________________________
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "(" +
                Util.KEY_ID + " INTEGER PRIMARY KEY," +
                Util.KEY_FIRSTNAME + " TEXT," +
                Util.KEY_LASTNAME + " TEXT," +
                Util.KEY_EMAIL + " TEXT," +
                Util.KEY_PASSWORD + " TEXT " + ")";
        db.execSQL(CREATE_USER_TABLE);
        String CREATE_CAT_TABLE = "CREATE TABLE " + Util.CAT_TABLE_NAME + "(" +
                Util.KEY_CAT_ID + " INTEGER PRIMARY KEY," +
                Util.KEY_CAT_AMOUNT + " TEXT," +
                Util.KEY_CAT_DORC + " TEXT," +
                Util.KEY_CAT_TYPE + " TEXT," +
                Util.KEY_CAT_DESCRIPTION + " TEXT," +
                Util.KEY_CAT_DATE_ADDED + " TEXT" + ")";
        db.execSQL(CREATE_CAT_TABLE);
        Log.d("stu51272", "Database created");
    }

    // DROP TABLE ON UPGRADE -----------------------------------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = String.valueOf("DROP TABLE IF EXISTS");
        db.execSQL(DROP_TABLE, new String[]{Util.DATABASE_NAME});
        onCreate(db);
    }

    // USER CRUD METHODS ---------------------------------------------------------------------------
    // ADD USER TO DB ------------------------------------------------------------------------------
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put(Util.KEY_FIRSTNAME, user.getFirstName());
            values.put(Util.KEY_LASTNAME, user.getLastName());
            values.put(Util.KEY_EMAIL, user.getEmail());
            values.put(Util.KEY_PASSWORD, user.getPassword());
        db.insert(Util.TABLE_NAME, null, values);
        Log.d("stu51272", "new user registered: " + user.getFirstName() + " " + user.getLastName());
        db.close();
    }

    // GET USER USING ID ---------------------------------------------------------------------------
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.TABLE_NAME, new String[]{
                Util.KEY_ID,
                Util.KEY_FIRSTNAME,
                Util.KEY_LASTNAME,
                Util.KEY_EMAIL,
                Util.KEY_PASSWORD},
                Util.KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null) cursor.moveToFirst();
        User user = new User();
            user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_ID))));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(Util.KEY_FIRSTNAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(Util.KEY_LASTNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(Util.KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(Util.KEY_PASSWORD)));
        return user;
//                    to get user by id:
//                    User gettingUser = user_db.getUser(1);
//                    Log.d("dbh", "userByID: " + gettingUser.getPassword());
    }

    // GET A USER FROM THEIR EMAIL -----------------------------------------------------------------
    public User getUserDetailsFromEmail(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        // SQL selector
        String selectOneUser =
                "SELECT * FROM " + Util.TABLE_NAME +
                        " WHERE " + Util.KEY_ID + " = " +
                        "(SELECT " + Util.KEY_ID +
                        " FROM " + Util.TABLE_NAME +
                        " WHERE " + Util.KEY_EMAIL + " = '" + userEmail +"')";
        Cursor cursor = db.rawQuery(selectOneUser, null);
        if (cursor != null) cursor.moveToFirst();
        User user = new User();
            user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_ID))));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(Util.KEY_FIRSTNAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(Util.KEY_LASTNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(Util.KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(Util.KEY_PASSWORD)));
        return user;
    }

    // SELECT ALL USER RECORDS ---------------------------------------------------------------------
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // select all users
        String selectAllUsers = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAllUsers, null);
        // loop through db
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setEmail(cursor.getString(4));
                // add user objects to list
                userList.add(user);
            } while (cursor.moveToNext());
        } return userList;
    }

    // UPDATE USER ---------------------------------------------------------------------------------
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put(Util.KEY_FIRSTNAME, user.getFirstName());
            values.put(Util.KEY_LASTNAME, user.getLastName());
            values.put(Util.KEY_EMAIL, user.getEmail());
            values.put(Util.KEY_PASSWORD, user.getPassword());
        // update the row
        return db.update(Util.TABLE_NAME, values, Util.KEY_ID + "=?", new String[]{String.valueOf(user.getId())});
//        updating after selecting row using get user by id:
//        gettingUser.setPassword("1234");
//        int updateRow = user_db.updateUser(gettingUser);
    }

    // GET USER COUNT ------------------------------------------------------------------------------
    public int getCount() {
        String countQuery = "SELECT * FROM " + Util.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
//        use to get count:
//        Log.d("dbh", "onClick: " + user_db.getCount());
    }

    // CHECK IF EMAIL EXIST IN DB ------------------------------------------------------------------
    public String Exist(String email) {
        String emailCheck = "";
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.query(Util.TABLE_NAME, null, Util.KEY_EMAIL + "=?", new String[]{String.valueOf(email)},null, null, null);
            if (c == null) {
                return emailCheck;
            } else {
                c.moveToFirst();
                emailCheck = c.getString(c.getColumnIndex(Util.KEY_EMAIL));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return emailCheck;
    }
//-------------------------- CAT DB CRUD METHODS ---------------------------------------------------

    // ADD CAT RECORD TO DB ------------------------------------------------------------------------
    public void addCAT(CATransactions catTransactions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put(Util.KEY_CAT_AMOUNT, catTransactions.getAmount());
            values.put(Util.KEY_CAT_DORC, catTransactions.getCreditOrDebit());
            values.put(Util.KEY_CAT_TYPE, catTransactions.getType());
            values.put(Util.KEY_CAT_DESCRIPTION, catTransactions.getDescription());
            values.put(Util.KEY_CAT_DATE_ADDED, java.lang.System.currentTimeMillis());
        db.insert(Util.CAT_TABLE_NAME, null, values);
        Log.d("stu51272", "new transaction added: ");
        db.close();
    }

    // DELETE CAT RECORD FROM DB -------------------------------------------------------------------
    public void deleteCat(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.CAT_TABLE_NAME, Util.KEY_CAT_ID+ "=?", new String[]{String.valueOf(id)});
        db.close();
        Log.d("stu51272", "deleteCat: Transaction deleted");
//        to delete use:
//        user_db.deleteCat(2);
    }

    // GET CAT RECORD COUNT ------------------------------------------------------------------------
    public int getCATcount() {
        String catCountQuery = "SELECT * FROM " + Util.CAT_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor catCursor = db.rawQuery(catCountQuery, null);
        return catCursor.getCount();
    }
//        to use:
//        int i = [dbhandler in here].getCATcount();
//        Log.d("po", "count " + i);

    // SELECT ALL CAT RECORDS ----------------------------------------------------------------------
    public List<CATransactions> getAllCAT() {
        List<CATransactions> catList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // select CATs SQL
        String selectAllCats = "SELECT * FROM " + Util.CAT_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAllCats, null);

        // loop through db
        if (cursor.moveToFirst()) {
            do {
                CATransactions catTransactions = new CATransactions();
                    catTransactions.setId(cursor.getInt(cursor.getColumnIndex(Util.KEY_CAT_ID)));
                    catTransactions.setAmount(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_AMOUNT)));
                    catTransactions.setCreditOrDebit(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_DORC)));
                    catTransactions.setType(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_TYPE)));
                    catTransactions.setDescription(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_DESCRIPTION)));
                DateFormat catDateFormat = DateFormat.getDateInstance();
                String catDateFormatted = catDateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Util.KEY_CAT_DATE_ADDED))).getTime());
                    catTransactions.setDateTransaction(catDateFormatted);
                // add user objects to list
                catList.add(catTransactions);
            } while (cursor.moveToNext());
        } return catList;
    }

    // GET ONE RECORD FROM CAT DB USING ID ---------------------------------------------------------
    public CATransactions getATransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.CAT_TABLE_NAME, new String[]{
                        Util.KEY_CAT_ID,
                        Util.KEY_CAT_AMOUNT,
                        Util.KEY_CAT_DORC,
                        Util.KEY_CAT_TYPE,
                        Util.KEY_CAT_DESCRIPTION,
                        Util.KEY_CAT_DATE_ADDED},
                Util.KEY_CAT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        CATransactions catTransactions = new CATransactions();
            catTransactions.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_ID))));
            catTransactions.setAmount(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_AMOUNT)));
            catTransactions.setCreditOrDebit(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_DORC)));
            catTransactions.setType(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_TYPE)));
            catTransactions.setDescription(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_DESCRIPTION)));
        DateFormat catDateFormat = DateFormat.getDateInstance();
        String catDateFormatted = catDateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Util.KEY_CAT_DATE_ADDED))).getTime());
            catTransactions.setDateTransaction(catDateFormatted);
        return catTransactions;
    }

    // UPDATE CAT  ---------------------------------------------------------------------------------
    public int updateCat(CATransactions catTransactions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put(Util.KEY_CAT_DESCRIPTION, catTransactions.getDescription());
            values.put(Util.KEY_CAT_TYPE, catTransactions.getType());
            values.put(Util.KEY_CAT_DORC, catTransactions.getCreditOrDebit());
            values.put(Util.KEY_CAT_AMOUNT, catTransactions.getAmount());
        Log.d("stu51272", "transaction updated");
        // update the db record
        return db.update(Util.CAT_TABLE_NAME, values, Util.KEY_CAT_ID + "=?", new String[]{String.valueOf(catTransactions.getId())});
    }

    // GET ALL DEBITS or CREDITS  ------------------------------------------------------------------
    public List<CATransactions> getAllCredit(String dorc) {
        List<CATransactions> catList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // select CATs SQL
        String selectAllCredits =
                "SELECT * FROM " + Util.CAT_TABLE_NAME +
                        " WHERE " + Util.KEY_CAT_DORC + " = " + "'" + dorc +"'";
        Cursor cursor = db.rawQuery(selectAllCredits, null);
        // loop through db
        if (cursor.moveToFirst()) {
            do {
                CATransactions catTransactions = new CATransactions();
                    catTransactions.setAmount(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_AMOUNT)));
                // add CAT objects to list
                catList.add(catTransactions);
            } while (cursor.moveToNext());
        } return catList;
    }

    // Get current account totals
    public double getCurrAccTotal(String dorc) {
        double sumCredits = 0;
        List<CATransactions> currAccTotalCreditList;
        currAccTotalCreditList = getAllCredit(dorc);
        // loop through list of transactions
        for (CATransactions ccl : currAccTotalCreditList) {
            double creditAsDouble = Double.parseDouble(ccl.getAmount().replace(",",""));
            sumCredits = sumCredits + creditAsDouble;
        }
        return sumCredits;
    }

    // GET ALL TYPES  ------------------------------------------------------------------
    public List<CATransactions> getAllTypes(String type) {
        List<CATransactions> catList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // select CATs SQL
        String selectAllCredits =
                "SELECT * FROM " + Util.CAT_TABLE_NAME +
                        " WHERE " + Util.KEY_CAT_TYPE + " = " + "'" + type +"'";
        Cursor cursor = db.rawQuery(selectAllCredits, null);
        // loop through db
        if (cursor.moveToFirst()) {
            do {
                CATransactions catTransactions = new CATransactions();
                catTransactions.setAmount(cursor.getString(cursor.getColumnIndex(Util.KEY_CAT_AMOUNT)));
                // add user objects to list
                catList.add(catTransactions);
            } while (cursor.moveToNext());
        } return catList;
    }

    // get the sum of all the transaction types
    public double getTypeTotal(String type) {
        double sumType = 0;
        List<CATransactions> currAccTotalTypeList;
        currAccTotalTypeList = getAllTypes(type);
        // loop through list of transactions
        for (CATransactions wg : currAccTotalTypeList) {
            double wageAsDouble = Double.parseDouble(wg.getAmount().replace(",", ""));
            sumType = sumType + wageAsDouble;
        }
        return sumType;
    }
}


