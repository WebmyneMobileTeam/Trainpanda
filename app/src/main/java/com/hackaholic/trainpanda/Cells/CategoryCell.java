package com.hackaholic.trainpanda.Cells;


/**
 * Created by Android on 09-04-2015.
 */
public class CategoryCell implements CheckType {

    String CategoryName;
    boolean isCatg,isSubCatg,isMenuItem;

    public CategoryCell(String name,boolean value1,boolean value2,boolean value3) {
        this.CategoryName = name;
        this.isCatg = value1;
        this.isSubCatg = value2;
        this.isMenuItem = value3;
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
