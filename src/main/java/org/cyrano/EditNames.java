package org.cyrano;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.folg.gedcom.model.Name;
import java.util.ArrayList;
import java.util.List;


public class EditNames extends ControllerConnectionAbstract {

    @FXML
    private TextField namePrefixTextField;
    @FXML
    private TextField nameSufixTextField;
    @FXML
    private TextField givNameTextField;
    @FXML
    private TextField surnameTextField;
    @FXML
    private TextField marNameTextField;
/*
    ImportGedcom2Object familyTree;
    Person actualPerson;
    mainWindowController mainController;
*/

    public EditNames(){

    }


    protected void putStartInfo(){

        this.marNameTextField.setText(actualPerson.getNames().get(0).getMarriedName());
        this.surnameTextField.setText(actualPerson.getNames().get(0).getSurname());
        this.givNameTextField.setText(actualPerson.getNames().get(0).getGiven());
        this.namePrefixTextField.setText(actualPerson.getNames().get(0).getPrefix());
        this.nameSufixTextField.setText(actualPerson.getNames().get(0).getSuffix());

    }


    /**
     * Takes the Informaition in the TextFields and changes the different Names of the Person
     * @return  if there is a surname
     */
    protected boolean saveData(){            //do Not set the ID!!!
        List<Name> names = new ArrayList<>();
        Name name = new Name();

        if(surnameTextField.getText() == null){
            final Alert needSurname = new Alert(Alert.AlertType.WARNING, "At least Surname must have a value");
            needSurname.showAndWait();
            return false;
        }

        if(surnameTextField.getText().equals("")){
            final Alert needSurname = new Alert(Alert.AlertType.WARNING, "At least Surname must have a value");
            needSurname.showAndWait();
            return false;
        }

        name.setSurname(surnameTextField.getText());

        if (givNameTextField.getText() != null)
            if (!(givNameTextField.getText()).equals("")) {
                name.setGiven(givNameTextField.getText());
            } else name.setGiven(null);
            else name.setGiven(null);

        name.setGiven(givNameTextField.getText());

        if (marNameTextField.getText() != null) {
            if (!marNameTextField.getText().equals("")){
            name.setMarriedNameTag("_MARNM");
            name.setMarriedName(marNameTextField.getText());}
        } else name.setMarriedName(null);


        if (namePrefixTextField.getText() != null)
            if(!namePrefixTextField.getText().equals("")) {
                name.setPrefix(namePrefixTextField.getText());
            }
        else name.setPrefix(null);


        if (nameSufixTextField.getText() != null)
            if(!nameSufixTextField.getText().equals("")) {
                name.setSuffix(nameSufixTextField.getText());
            }
        else name.setSuffix(null);


        names.add(name);

        actualPerson.setNames(names);
        familyTree.changePersonNames(actualPerson);

        return true;
    }

    /**
     * only closes the stage
     */
    @FXML
    protected void doCancel() {
        Stage stage = (Stage) surnameTextField.getScene().getWindow();
        mainController.disableButtons(false);
        stage.close();
    }

    /**
     * Mathode for the OkBtn, so it sends the information to the mainController and closes the stage
     */
    @FXML
    protected void doOk() {
        Stage stage = (Stage) surnameTextField.getScene().getWindow();
        if(saveData()){
            sendData();
            stage.close();
        }
    }
}
