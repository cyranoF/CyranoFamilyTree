package org.cyrano;

import javafx.scene.control.ComboBox;
import org.folg.gedcom.model.Person;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ControllerConnectionAbstract {

    protected ImportGedcom2Object familyTree;
    protected Person actualPerson;
    protected mainWindowController mainController;

    /**
     * Sets the main Variables to edit the Person Names
     * @param obj   the custom "familyTree" of type ImportGedcom2Object
     * @param ap    th Person to be edited
     * @param controllM    the Controller calling this methode, so it can sand the information back
     */
    public void setData(ImportGedcom2Object obj, Person ap, mainWindowController controllM){
        this.actualPerson = ap;
        this.familyTree = obj;
        this.mainController = controllM;
        putStartInfo();
    }

    abstract void putStartInfo();

    abstract boolean saveData();

    /**
     * Sends to the Controller mainWindowController configured in "setData(...)" the main information
     */
    protected void sendData(){
        mainController.getData(familyTree, actualPerson);
    }

    abstract void doOk();

    abstract void doCancel();

    protected boolean checkDatesWrong(ComboBox<Integer> day, ComboBox<String> month, ComboBox<Integer> year) {
        List<String> month30 = new ArrayList<>(Arrays.asList("Apr", "Jun", "Set","Nov"));
        if(month.getValue() != null && day.getValue() != null){
            if (month30.contains(month.getValue()))
                if (day.getValue() == 31)
                    return true;
            if (month.getValue().equals("Feb"))
            {
                if (day.getValue() > 29){
                    return true;
                }
                if (year.getValue() != null){
                    String tempS = String.valueOf(year.getValue());
                    int tempI = Integer.parseInt(tempS);

                    if (day.getValue() == 29){

                        if (((float)tempI % 400) == 0){
                            return false;}
                        if (((float)tempI%4)== 0){
                            return ((tempI % 100) == 0); }
                        else return true;

                    }
                }
            }
        }
        return false;
    }



}
