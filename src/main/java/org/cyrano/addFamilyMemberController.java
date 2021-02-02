package org.cyrano;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Name;
import org.folg.gedcom.model.Person;
import java.net.URL;
import java.util.*;

public class addFamilyMemberController extends ControllerConnectionAbstract implements Initializable {

    //region FXML variables
    @FXML
    private Label MarPlaceLabel;
    @FXML
    private Label marDateL;
    @FXML
    private Label divorceDateL;
    @FXML
    private ComboBox<String> DdMonthCB ;
    @FXML
    private ComboBox<Integer> DdYearCB ;
    @FXML
    private ComboBox<Integer> DdDayCB ;
    @FXML
    private ComboBox<String> sexCB ;
    @FXML
    private TextField marriagePlaceTF;
    @FXML
    private ComboBox<String> MdMonthCB ;
    @FXML
    private ComboBox<Integer> MdYearCB ;
    @FXML
    private ComboBox<Integer> MdDayCB;
    @FXML
    private ComboBox<String> liveStatusCB ;
    @FXML
    private ComboBox<String> partnerRelationCB ;
    @FXML
    private TextField givenNameTF;
    @FXML
    private TextField SurnameTF;
    @FXML
    private TextField marriageNameTF;
    @FXML
    private ComboBox<Integer> BdDayCB ;           //carefull month
    @FXML
    private ComboBox<String> BdMonthCB ;
    @FXML
    private ComboBox<Integer> BdYearCB ;
    @FXML
    private TextField BirthplaceTF;
    @FXML
    private AnchorPane partnerPane;
    @FXML
    private ComboBox<String> relationChoice;

    //endregion

    private Person newPerson = new Person();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        relationChoice.getItems().addAll("Partner", "Child", "Parent", "Sibling");
        relationChoice.setOnAction(e-> partnerPane.setVisible(relationChoice.getValue().equals("Partner")));

        BdDayCB.getItems().add(null);
        MdDayCB.getItems().add(null);
        DdDayCB.getItems().add(null);
        for (int i = 1; i < 32; i++) {
            BdDayCB.getItems().add(i);
            MdDayCB.getItems().add(i);
            DdDayCB.getItems().add(i);
        }

        BdMonthCB.getItems().addAll(null, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");  //(null, "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
        MdMonthCB.getItems().addAll(null, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        DdMonthCB.getItems().addAll(null, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

        for (int i = 2021; i > 1000; i--) {
            BdYearCB.getItems().add(i);
            MdYearCB.getItems().add(i);
            DdYearCB.getItems().add(i);
        }
        BdYearCB.setEditable(true);
        MdYearCB.setEditable(true);
        DdYearCB.setEditable(true);

        sexCB.getItems().addAll("M","F","unknown");
        liveStatusCB.getItems().addAll(null, "Alive", "Dead");
        partnerRelationCB.getItems().addAll("Married","Widowed","Divorced", "Engaged", "Partner", "Friend");
        partnerRelationCB.setOnAction(e->{
            if(partnerRelationCB.getValue().equals("Married") | partnerRelationCB.getValue().equals("Widowed")){
                selectRelation(true, "Marriage Date:", false, "Divorce Date:");
            }else
            if(partnerRelationCB.getValue().equals("Divorced")){
                selectRelation(true,"Marriage Date:", true, "Divorce Date:");
            }else
            if (partnerRelationCB.getValue().equals("Engaged")){
                selectRelation(true,"Engagement Date:", false, "");
            }
            else
            {
                selectRelation(false, "", false,"");
            }
        });

    }

    /**
     * Enables or disables the Fields for Marriage and Divorce
     * @param marBoolean Marriage Fields enable
     * @param marLabel Label for "Marriage", could also be only "Engagement Day"
     * @param divBoolean  Divorce Fields enable
     * @param divLabel Label for Divorce Fields
     */
    private void selectRelation(boolean marBoolean,String marLabel, boolean divBoolean, String divLabel){
        marDateL.setVisible(marBoolean);
        marDateL.setText(marLabel);
        MdDayCB.setVisible(marBoolean);
        MdMonthCB.setVisible(marBoolean);
        MdYearCB.setVisible(marBoolean);
        MarPlaceLabel.setVisible(marBoolean);

        marriagePlaceTF.setVisible(marBoolean);

        divorceDateL.setVisible(divBoolean);
        divorceDateL.setText(divLabel);
        DdDayCB.setVisible(divBoolean);
        DdMonthCB.setVisible(divBoolean);
        DdYearCB.setVisible(divBoolean);

    }

    protected void putStartInfo(){
        this.SurnameTF.setText(actualPerson.getNames().get(0).getSurname());
    }


    protected boolean saveData(){
        Alert wrongMissing = new Alert(Alert.AlertType.WARNING, "The \"Surname\", \"Relation\" and \"Sex\" are needed");
        Alert wrongData = new Alert(Alert.AlertType.WARNING, "The data is wrong");
        EventFact eventBuffer;
        if (relationChoice.getValue() == null)      //need relation
        {
            wrongMissing.showAndWait();
            return false;
        }
        Name name = setNames();                     //need Surname
        if (name == null){
            return false;}

        if (sexCB.getValue() == null)               //need Sex
        { wrongMissing.showAndWait();
            return false; }


        if (checkDatesWrong(BdDayCB, BdMonthCB, BdYearCB)){
            wrongData.showAndWait();
            return false; }

        if (checkDatesWrong(MdDayCB, MdMonthCB, MdYearCB)){
            wrongData.showAndWait();
            return false; }

        if (checkDatesWrong(DdDayCB, DdMonthCB, DdYearCB)){
            wrongData.showAndWait();
            return false; }

        List<Name> names = new ArrayList<>();
        names.add(name);
        newPerson.setNames(names);

        eventBuffer = addEvent("BIRT");
        if (eventBuffer != null )
            newPerson.addEventFact(eventBuffer);

        eventBuffer = addEvent("DEAT");
        if (eventBuffer != null)
            newPerson.addEventFact(eventBuffer);

        eventBuffer = addEvent("SEX");
        if (eventBuffer != null)
            newPerson.addEventFact(eventBuffer);

        if (relationChoice.getValue().equals("Partner")) {      //"Married","Widowed","Divorced", "Separated","Engaged"

            if (partnerRelationCB.getValue() != null ) {
                if (partnerRelationCB.getValue().equals("Engaged")) {
                    eventBuffer = addEvent("ENGA");
                } else {
                    eventBuffer = addEvent("MARR");
                }
                if (eventBuffer != null){
                    newPerson.addEventFact(eventBuffer);
                    actualPerson.addEventFact(eventBuffer);
                }

                if (partnerRelationCB.getValue().equals("Divorced")) {
                    eventBuffer = addEvent("DIV");
                    if (eventBuffer != null) {
                        newPerson.addEventFact(eventBuffer);
                        actualPerson.addEventFact(eventBuffer);
                    }
                }
            }else {
                Alert ralationshipMissing = new Alert(Alert.AlertType.WARNING, "The \"Relationship\" is needed");
                ralationshipMissing.showAndWait();
                return false;
            }

        }

        newPerson = familyTree.createNewPersonId(newPerson);

        switch (relationChoice.getValue()){
            case "Child": familyTree.addFamilyMember(actualPerson,newPerson,"Child");break;
            case "Partner": familyTree.addFamilyMember(actualPerson,newPerson,"Partner");break;
            case "Parent": familyTree.addFamilyMember(actualPerson,newPerson,"Parent");break;
            case "Sibling": familyTree.addFamilyMember(actualPerson,newPerson,"Sibling");break;
            default: return false;
        }

        return true;
    }



    private Name setNames(){
        Name name = new Name();
        if(SurnameTF.getText().equals("")){
            final Alert needSurname = new Alert(Alert.AlertType.WARNING, "At least Surname must have a value");
            needSurname.showAndWait();
            return null;
        }
        name.setSurname(SurnameTF.getText());

        if(givenNameTF.getText().equals("")){
            name.setGiven(null);
        }
        else {
            name.setGiven(givenNameTF.getText());
        }
        if (marriageNameTF.getText() == null)
            name.setMarriedName(null);
        else{
            if (!marriageNameTF.getText().equals("")){
                name.setMarriedNameTag("_MARNM");
                name.setMarriedName(marriageNameTF.getText());}
        }

        return name;
    }

    private EventFact addEvent(String tag){

        EventFact eventFact = new EventFact();
        String stringbuffer = "";
        switch (tag){
            case "BIRT":
                eventFact.setTag("BIRT");
                if (!BirthplaceTF.getText().equals("")){
                    eventFact.setPlace(BirthplaceTF.getText());
                }
                if (BdDayCB.getValue() != null)
                    stringbuffer += BdDayCB.getValue() + " ";
                if (BdMonthCB.getValue() != null)
                    stringbuffer += BdMonthCB.getValue().toUpperCase() + " ";
                if (BdYearCB.getValue() != null)
                    stringbuffer += BdYearCB.getValue();

                if (!stringbuffer.equals(""))
                    eventFact.setDate(stringbuffer);
                break;

            case "DEAT":
                eventFact.setTag("DEAT");
                if (liveStatusCB.getValue() == null)
                    return null;

                if (liveStatusCB.getValue().equals("Alive"))
                    return null;
                if (liveStatusCB.getValue().equals("Dead"))
                    eventFact.setValue("Y");
                break;

            case "SEX":
                eventFact.setTag("SEX");
                if (sexCB.getValue() == null)
                    return null;
                eventFact.setValue(sexCB.getValue());
                break;

            case "MARR":
                eventFact.setTag("MARR");
                if (MdDayCB.getValue() != null)
                    stringbuffer += MdDayCB.getValue() + " ";
                if (MdMonthCB.getValue() != null)
                    stringbuffer += MdMonthCB.getValue().toUpperCase() + " ";
                if (MdYearCB.getValue() != null)
                    stringbuffer += MdYearCB.getValue();
                if (!stringbuffer.equals(""))
                    eventFact.setDate(stringbuffer);

                if (!marriagePlaceTF.getText().equals("")){
                    eventFact.setPlace(marriagePlaceTF.getText());
                }
                break;
            case "DIV":
                eventFact.setTag("DIV");
                if (DdDayCB.getValue() != null)
                    stringbuffer += DdDayCB.getValue() + " ";
                if (DdMonthCB.getValue() != null)
                    stringbuffer += DdMonthCB.getValue().toUpperCase() + " ";
                if (DdYearCB.getValue() != null)
                    stringbuffer += DdYearCB.getValue();
                if (!stringbuffer.equals(""))
                    eventFact.setDate(stringbuffer);
                break;

            case "ENGA":
                eventFact.setTag("ENGA");
                if (MdDayCB.getValue() != null)
                    stringbuffer += MdDayCB.getValue() + " ";
                if (MdMonthCB.getValue() != null)
                    stringbuffer += MdMonthCB.getValue().toUpperCase() + " ";
                if (MdYearCB.getValue() != null)
                    stringbuffer += MdYearCB.getValue();
                if (!stringbuffer.equals(""))
                    eventFact.setDate(stringbuffer);
                break;

        }

        return eventFact;
    }

    @FXML
    protected void doOk() {
        Stage stage = (Stage) SurnameTF.getScene().getWindow();
        if(saveData()){
            sendData();
            stage.close();
        }
    }

    @FXML
    protected void doCancel() {
        Stage stage = (Stage) SurnameTF.getScene().getWindow();
        mainController.disableButtons(false);
        stage.close();
    }

}
