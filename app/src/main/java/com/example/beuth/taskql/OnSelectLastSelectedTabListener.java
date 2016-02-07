package com.example.beuth.taskql;

/**
 * @author Wael Gabsi, Stefan Völkel
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
