package com.example.beuth.taskql;

/**
 * Created by domar007 on 04.02.2016.
 */
public interface OnSelectLastSelectedTabListener {
    public void selectLastSelectedTab();
    public void selectLastSelectedTabText();
    public void deleteLastSelectedTabText(int position);
    public void addEditedTabPosition(int position);
    public int getFirstEditedTabPosition();
    public void removeFirstEditedTabPosition();
    public int getSelectedTabPosition();
}
