package model;

import java.util.ArrayList;

public class RowList extends ArrayList<Bubble> {
    private boolean fullFlag;

    /**
     * constructor, sets if the row a full row is
     * (if it fills the screen entirely)
     * @param fullFlag true is it is a full row, else false
     */
    public RowList(boolean fullFlag) {
        this.fullFlag = fullFlag;
    }

    /**
     * returns whether the row is a full row
     * @return true if it is a full row, else false
     */
    public boolean isFullFlag(){
        return fullFlag;
    }

    /**
     * sets the row to a full row
     */
    public void setFull(){
        fullFlag =true;
    }

}
