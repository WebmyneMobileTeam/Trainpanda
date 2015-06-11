package com.hackaholic.trainpanda.Cells;


/**
 * Created by Android on 09-04-2015.
 */
public class CellType implements CheckType {

    boolean isCategory;
    boolean isSubCategory;
    boolean isMenuItem;



    public CellType(boolean formaincatg, boolean forsubcatg, boolean formenuitem){
        this.isCategory = formaincatg;
        this.isSubCategory = forsubcatg;
        this.isMenuItem = formenuitem;
    }

    @Override
    public boolean isCategory() {
        if(isCategory ==  true)
            return true;
        else
            return false;
    }

    @Override
    public boolean isSubCategory() {
        if(isSubCategory ==  true)
            return true;
        else
            return false;
    }

    @Override
    public boolean isMenuItem() {
        if(isMenuItem ==  true)
            return true;
        else
            return false;
    }
}
