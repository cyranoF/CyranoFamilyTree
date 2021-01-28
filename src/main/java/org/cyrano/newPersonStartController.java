package org.cyrano;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Name;

public class newPersonStartController extends ControllerConnectionAbstract {

    @FXML
    private  TextField givenNameTF;
    @FXML
    private  TextField surnameTF;
    @FXML
    private  TextField marriageNameTF;
    @FXML
    private  ComboBox<Integer> BdDayCB;
    @FXML
    private  ComboBox<String> BdMonthCB;
    @FXML
    private  ComboBox<Integer> BdYearCB;
    @FXML
    private  TextField BirthplaceTF;            //aAAADASDASDASDAS
    @FXML
    private  ComboBox<String> sexCB;

    @Override
    void putStartInfo() {
        sexCB.getItems().addAll("M","F","unknown");

        BdDayCB.getItems().add(null);
        for (int i = 1; i < 32; i++) {
            BdDayCB.getItems().add(i);
        }

        BdMonthCB.getItems().addAll(null, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");  //(null, "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

        for (int i = 2021; i > 1000; i--) {
            BdYearCB.getItems().add(i);
        }
        BdYearCB.setEditable(true);

    }

    @Override
    boolean saveData() {
        Alert wrongMissing = new Alert(Alert.AlertType.WARNING, "The \"Sex\" is needed");
        Alert wrongData = new Alert(Alert.AlertType.WARNING, "The data is wrong");
        if (sexCB.getValue() == null)               //need Sex
        { wrongMissing.showAndWait();
            return false; }
        if (checkDatesWrong(BdDayCB, BdMonthCB, BdYearCB)){
            wrongData.showAndWait();
            return false; }

        Name name = setNames();
        if (name == null){
            return false;}
        actualPerson.addName(name);

        EventFact eventFact = new EventFact();
        eventFact.setTag("SEX");
        eventFact.setValue(sexCB.getValue());
        actualPerson.addEventFact(eventFact);

        eventFact = new EventFact();
        eventFact.setTag("BIRT");
        String stringbuffer = "";
        if (BdDayCB.getValue() != null)
            stringbuffer += BdDayCB.getValue() + " ";
        if (BdMonthCB.getValue() != null)
            stringbuffer += BdMonthCB.getValue().toUpperCase() + " ";
        if (BdYearCB.getValue() != null)
            stringbuffer += BdYearCB.getValue();
        if (BirthplaceTF.getText() != null)
            if (!BirthplaceTF.getText().equals(""))
                eventFact.setPlace(BirthplaceTF.getText());

        if (!stringbuffer.equals(""))
            eventFact.setDate(stringbuffer);
        actualPerson.addEventFact(eventFact);

        familyTree.addPerson(actualPerson);


        return true;
    }

    private Name setNames(){
        Name name = new Name();
        if(surnameTF.getText().equals("")){
            final Alert needSurname = new Alert(Alert.AlertType.WARNING, "At least Surname must have a value");
            needSurname.showAndWait();
            return null;
        }
        name.setSurname(surnameTF.getText());

        if(!givenNameTF.getText().equals("")){
            name.setGiven(givenNameTF.getText());
        }

        if (!marriageNameTF.getText().equals("")){
            name.setMarriedNameTag("_MARNM");
            name.setMarriedName(marriageNameTF.getText());
        }


        return name;
    }

    @FXML
    @Override
    void doOk() {
        Stage stage = (Stage) surnameTF.getScene().getWindow();
        if(saveData()){
            sendData();
            stage.close();
        }
    }

    @FXML
    @Override
    void doCancel() {
        Stage stage = (Stage) surnameTF.getScene().getWindow();

        stage.close();
        mainController.disableButtons(false);
    }
}
