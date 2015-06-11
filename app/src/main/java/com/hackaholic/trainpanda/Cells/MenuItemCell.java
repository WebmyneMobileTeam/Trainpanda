package com.hackaholic.trainpanda.Cells;


/**
 * Created by Android on 09-04-2015.
 */
public class MenuItemCell implements CheckType {

    String MenuItemName;
    String MenuItemValue2;
    String MenuItemValue3;

    public MenuItemCell(String name, String value2, String value3) {
        this.MenuItemName = name;
        this.MenuItemValue2 = value2;
        this.MenuItemValue3 = value3;
    }


    @Override
    public boolean isCategory() {
        return false;
    }

    @Override
    public boolean isSubCategory() {
        return false;
    }

    @Override
    public boolean isMenuItem() {
        return false;
    }
}
