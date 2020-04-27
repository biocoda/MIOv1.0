package com.biocoda.miov10.model;

import android.content.Context;
import android.util.Log;

import com.biocoda.miov10.data.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class Balance {
    private Context context;
    private String currAccBalance;
    private String currAccTotalCredits;
    private String currAccTotalDebits;
    private String savAccTotal;
    private String allWageCredits;
    private String allOtherIncomeCredits;
    private String allHouseBillsDebits;
    private String allFoodShopDebits;
    private String allOtherShopDebits;
    private String allEntertainmentDebits;
    private String allTravelDebits;
    private String allMiscDebits;
    private String allSavingDebits;
    private DatabaseHandler currAccTotalDbHandler;
    private List<CATransactions> currAccTotalCreditList, currAccTotalDebitList, savAccTotalList;
    private double sumCredits;



    public Balance() {
    }

    public String getCurrAccBalance() {
        currAccTotalDbHandler = new DatabaseHandler(context);
        currAccTotalCreditList = new ArrayList<>();
        currAccTotalCreditList = currAccTotalDbHandler.getAllCredit("credit");
        for (CATransactions ccl : currAccTotalCreditList) {
            double creditAsDouble = Double.parseDouble(ccl.getAmount().replace(",",""));
            sumCredits = sumCredits + creditAsDouble;
        }
        Log.d("po", "getCurrAccBalance: " + sumCredits);
        return currAccBalance;
    }

    public void setCurrAccBalance(String currAccBalance) {
        this.currAccBalance = currAccBalance;
    }

    public String getCurrAccTotalCredits() {
        return currAccTotalCredits;
    }

    public void setCurrAccTotalCredits(String currAccTotalCredits) {
        this.currAccTotalCredits = currAccTotalCredits;
    }

    public String getCurrAccTotalDebits() {
        return currAccTotalDebits;
    }

    public void setCurrAccTotalDebits(String currAccTotalDebits) {
        this.currAccTotalDebits = currAccTotalDebits;
    }

    public String getSavAccTotal() {
        return savAccTotal;
    }

    public void setSavAccTotal(String savAccTotal) {
        this.savAccTotal = savAccTotal;
    }

    public String getAllWageCredits() {
        return allWageCredits;
    }

    public void setAllWageCredits(String allWageCredits) {
        this.allWageCredits = allWageCredits;
    }

    public String getAllOtherIncomeCredits() {
        return allOtherIncomeCredits;
    }

    public void setAllOtherIncomeCredits(String allOtherIncomeCredits) {
        this.allOtherIncomeCredits = allOtherIncomeCredits;
    }

    public String getAllHouseBillsDebits() {
        return allHouseBillsDebits;
    }

    public void setAllHouseBillsDebits(String allHouseBillsDebits) {
        this.allHouseBillsDebits = allHouseBillsDebits;
    }

    public String getAllFoodShopDebits() {
        return allFoodShopDebits;
    }

    public void setAllFoodShopDebits(String allFoodShopDebits) {
        this.allFoodShopDebits = allFoodShopDebits;
    }

    public String getAllOtherShopDebits() {
        return allOtherShopDebits;
    }

    public void setAllOtherShopDebits(String allOtherShopDebits) {
        this.allOtherShopDebits = allOtherShopDebits;
    }

    public String getAllEntertainmentDebits() {
        return allEntertainmentDebits;
    }

    public void setAllEntertainmentDebits(String allEntertainmentDebits) {
        this.allEntertainmentDebits = allEntertainmentDebits;
    }

    public String getAllTravelDebits() {
        return allTravelDebits;
    }

    public void setAllTravelDebits(String allTravelDebits) {
        this.allTravelDebits = allTravelDebits;
    }

    public String getAllMiscDebits() {
        return allMiscDebits;
    }

    public void setAllMiscDebits(String allMiscDebits) {
        this.allMiscDebits = allMiscDebits;
    }

    public String getAllSavingDebits() {
        return allSavingDebits;
    }

    public void setAllSavingDebits(String allSavingDebits) {
        this.allSavingDebits = allSavingDebits;
    }
}
