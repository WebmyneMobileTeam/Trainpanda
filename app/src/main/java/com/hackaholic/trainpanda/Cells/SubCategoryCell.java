package com.hackaholic.trainpanda.Cells;


/**
 * Created by Android on 09-04-2015.
 */
public class SubCategoryCell implements CheckType {

    String SubCategoryName;
    String SubCategoryValue2;
    String SubCategoryValue3;

    public SubCategoryCell(String name,String value2,String value3) {
        this.SubCategoryName = name;
        this.SubCategoryValue2 = value2;
        this.SubCategoryValue3 = value3;
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
