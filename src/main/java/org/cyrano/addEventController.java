package org.cyrano;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.folg.gedcom.model.EventFact;

import java.net.URL;
import java.util.*;

public class addEventController extends ControllerConnectionAbstract implements Initializable {


    @FXML
    private AnchorPane datePane;
    @FXML
    private AnchorPane placePane;
    @FXML
    private AnchorPane valuePane;
    @FXML
    private AnchorPane causePane;
    @FXML
    private TextArea causeTA;
    @FXML
    private Button okBtn;
    @FXML
    private Button addBtn;
    @FXML
    private TextField placeTF;
    @FXML
    private ComboBox<Integer> dayCB;
    @FXML
    private ComboBox<String> monthCB;
    @FXML
    private ComboBox<Integer> yearCB;
    @FXML
    private TextField valueTF;
    @FXML
    private ComboBox<String> sexCB;
    @FXML
    private ComboBox<String> eventTypeCB;

    //EventFact.PERSONAL_EVENT_FACT_TAGS
    //EventFact.DISPLAY_TYPE
    Map<String, String> tagDisplayMap;
    EventFact eventFact = new EventFact();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dayCB.getItems().add(null);
        for (int i = 1; i < 32; i++) {
            dayCB.getItems().add(i);
        }
        monthCB.getItems().addAll(null, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");  //(null, "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
        for (int i = 2021; i > 1000; i--) {

            yearCB.getItems().add(i);
        }
        yearCB.setEditable(true);


    }


    @Override
    void putStartInfo() {
        tagDisplayMap = familyTree.getEventTagDisplayMap();
        List<String> list = new ArrayList<>(tagDisplayMap.values());
        //Collections.sort();
        list.sort(Comparator.naturalOrder());

        eventTypeCB.getItems().addAll(list);
    }

    @Override
    boolean saveData() {

        if (checkDatesWrong(dayCB,monthCB,yearCB))
            return false;

        for (Map.Entry<String, String> entry : tagDisplayMap.entrySet())
            if (entry.getValue().equals(eventTypeCB.getValue())) {
                eventFact.setTag(entry.getKey());
                break;
            }

        if (!placeTF.getText().equals(""))
            eventFact.setPlace(placeTF.getText());
        if (!causeTA.getText().equals(""))
            eventFact.setCause(causeTA.getText());

        if (valueTF.isVisible()){
            if (!valueTF.getText().equals(""))
                eventFact.setValue(valueTF.getText());
            }
        else if (sexCB.isVisible()){
            eventFact.setValue(sexCB.getValue());
            }
        else eventFact.setValue("Y");

        String stringbuffer = "";
        if (dayCB.getValue() != null)
            stringbuffer += dayCB.getValue() + " ";
        if (monthCB.getValue() != null)
            stringbuffer += monthCB.getValue().toUpperCase() + " ";
        if (yearCB.getEditor().getText() != null )
            if(!yearCB.getEditor().getText().equals(""))
                stringbuffer += yearCB.getEditor().getText();

        if (!stringbuffer.equals(""))
            eventFact.setDate(stringbuffer);

        actualPerson.addEventFact(eventFact);


        return true;
    }

    @FXML
    protected void doOk() {
        Stage stage = (Stage) eventTypeCB.getScene().getWindow();
        //actualPerson = newPerson;
        if(saveData()){
            sendData();
            stage.close();
        }
    }

    @FXML
    protected void doCancel() {
        Stage stage = (Stage) eventTypeCB.getScene().getWindow();
        mainController.disableButtons(false);
        sendData();
        stage.close();
    }

    @FXML
    private void selectEvent() {

        clearAll();

        switch (eventTypeCB.getValue()) {
            case "Sex":
                sexCB.getItems().setAll("M", "F", "unknown");
                sexCB.setVisible(true);
                valueTF.setVisible(false);
                datePane.setDisable(true);
                placePane.setDisable(true);
                valuePane.setDisable(false);
                causePane.setDisable(true);
                break;
            case "Death":
            case "Divorce":
                datePane.setDisable(false);
                placePane.setDisable(false);
                valuePane.setDisable(true);
                causePane.setDisable(false);
                break;
            case "Birth":
            case "Engagement":
            case "Marriage":
                datePane.setDisable(false);
                placePane.setDisable(false);
                valuePane.setDisable(true);
                causePane.setDisable(true);

                break;
            case "Email":
            case "Eyes":
                datePane.setDisable(true);
                placePane.setDisable(true);
                valuePane.setDisable(false);
                causePane.setDisable(true);
                break;

            default:
                datePane.setDisable(false);
                placePane.setDisable(false);
                valuePane.setDisable(false);
                causePane.setDisable(false);
        }

        okBtn.setDisable(false);
        addBtn.setDisable(false);
    }

    private void clearAll(){
        sexCB.setVisible(false);
        valueTF.setVisible(true);
        placeTF.clear();
        valueTF.clear();
        causeTA.clear();
        dayCB.getSelectionModel().clearSelection();
        monthCB.getSelectionModel().clearSelection();
        yearCB.getEditor().clear();
        yearCB.getSelectionModel().clearSelection();

    }


    public void doAdd() {
        if (saveData()){
            valueTF.clear();
            placeTF.clear();
            sexCB.getSelectionModel().clearSelection();
            dayCB.getSelectionModel().clearSelection();
            monthCB.getSelectionModel().clearSelection();
            yearCB.getSelectionModel().clearSelection();
            yearCB.getEditor().clear();
            eventFact = new EventFact();
        }
    }
}
