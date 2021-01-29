package org.cyrano;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.folg.gedcom.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * JavaFX FamilyTree program
 * by Cyrano Fischer
 */
public class mainWindowController implements Initializable {

    @FXML
    private Button editNamesBtn;
    @FXML
    private Button addEventBtn;
    @FXML
    private ListView<String> NamesListView;
    @FXML
    private Button addFamilyBtn;
    @FXML
    private ListView<String> EventListView;
    @FXML
    private ListView<Hyperlink> FamilyListView;
    @FXML
    private ListView<Hyperlink> PersonsListView;

    //Variables for File
    FileChooser fileChooser = new FileChooser();

    File file = null, tempFile = null;
    ImportGedcom2Object familyTree = null; //
    Stage stage2;
    Stage stage;
    Person actualPerson;


    //Change file Confirmation
    private static final Alert open = new Alert(Alert.AlertType.CONFIRMATION,"Do you what to open a File?");

    private static final Alert newFileConfirm = new Alert(Alert.AlertType.CONFIRMATION,"Do you what to create a new File?");

    private static final Alert Save = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to Save your File?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

    private static final Alert wrongFile = new Alert(Alert.AlertType.WARNING, "Wrong File, it needs to be a .ged file");

    private static final Alert close = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close the Program?");

    private static final Alert success = new Alert(Alert.AlertType.INFORMATION, "Success");
    private static final Alert failure = new Alert(Alert.AlertType.WARNING, "failure");


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        //SexComboBox.getItems().addAll( "M", "F","unknown");

        initializeRightClick();

        configFileChooser();



    }

    /**
     * Initializes a new FamilyTree and the First person
     */
    public void newStart(){
        actualPerson = new Person();
        actualPerson.setId("I1");
        familyTree = new ImportGedcom2Object();

        try {
            disableButtons(true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newPersonStart.fxml"));
            Parent root = loader.load();

            newPersonStartController newPersonStartController = loader.getController();
            newPersonStartController.setData(familyTree, actualPerson, this);
            stage2 = new Stage();

            stage2.setScene(new Scene(root));
            stage2.setTitle("New Person");
            stage2.setOnCloseRequest(e->{
                e.consume();
                disableButtons(false);
                putData();
                stage2.close();

            });

            disableButtons(true);
            stage2.showAndWait();

        } catch (IOException ex) {
            System.err.printf("Error: %s%n", ex.getMessage());
        }

    }

    /**
     * for the GEDCOM Format
     */
    private void configFileChooser(){
        fileChooser.setInitialDirectory( new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gedcom", "*.ged"));
    }

    /**
    Calls the context Menus to implement them
     */
    private void initializeRightClick() {

        this.EventListView.setContextMenu(contextMenuEvent());
        this.FamilyListView.setContextMenu(contextMenuFamily());
        this.PersonsListView.setContextMenu(contextMenuPersons());
        this.NamesListView.setContextMenu(contextMenuNames());

    }

    /**
     Configuration for the ContextMenu(Right-Click) in the NamesListView
     */
    private ContextMenu contextMenuNames() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(event -> {                             //Edit Event
            if(this.NamesListView.getSelectionModel().getSelectedItem() != null){
                editNames();
            }
        });

        MenuItem copyItem = new MenuItem("Copy Name");       //copy Event
        copyItem.setOnAction(event -> {
            if(this.NamesListView.getSelectionModel().getSelectedItem() != null){
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();

                String selectedName = this.NamesListView.getSelectionModel().getSelectedItem();
                content.putString(selectedName);
                clipboard.setContent(content);
            }
        });

        contextMenu.getItems().addAll(copyItem, editItem);

        return contextMenu;
    }

    /**
    Configuration for the ContextMenu(Right-Click) in the PersonsListView
    */
    @FXML
    private ContextMenu contextMenuPersons(){

        ContextMenu contextMenu = new ContextMenu();

        //
        MenuItem deleteItem = new MenuItem("Delete");   //Remove only if no more conected
        deleteItem.setOnAction(event -> {
            System.out.println("Delete Event");
            Alert delete = new Alert(Alert.AlertType.CONFIRMATION, "Not Implemented Yet"); //Do you want to delete the Person?

            Optional<ButtonType> result = delete.showAndWait();
            if (result.get() == ButtonType.OK){
                System.out.println("Person will be deleted");
                //Hyperlink selectedHyperlinks = this.PersonsListView.getSelectionModel().getSelectedItem();
                //Person person = familyTree.get

                //familyTree.deletePerson();
            }

        });

        MenuItem copyItem = new MenuItem("Copy Name");
        copyItem.setOnAction(event -> {
            System.out.println("Copy Event");

            if(this.PersonsListView.getSelectionModel().getSelectedItem() != null){
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();

                Hyperlink selectedHyperlink = this.PersonsListView.getSelectionModel().getSelectedItem();
                content.putString(selectedHyperlink.getText());
                clipboard.setContent(content);
            }

        });

        contextMenu.getItems().addAll(copyItem);    //, deleteItem

        return contextMenu;
    }

    /**
    Configuration for the ContextMenu(Right-Click) in the FamilyListView
    */
    private ContextMenu contextMenuFamily(){

        ContextMenu contextMenu = new ContextMenu();

        MenuItem newItem = new MenuItem("New Member");
        newItem.setOnAction(event -> addFamilyMember());

        /*
        MenuItem deleteItem = new MenuItem("Delete Connection");    //
        deleteItem.setOnAction(event -> {
            Hyperlink selectedHyperlinks = this.FamilyListView.getSelectionModel().getSelectedItem();
            String[] id = selectedHyperlinks.getText().split("ID:");
            Person selPerson = familyTree.getPerson(id[1]);
            familyTree.deleteConnection(actualPerson, selPerson);
        });       //Remove Event*/

        MenuItem copyItem = new MenuItem("Copy Name");
        copyItem.setOnAction(event -> {
            if(this.FamilyListView.getSelectionModel().getSelectedItem() != null){
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();

                Hyperlink selectedHyperlink = this.FamilyListView.getSelectionModel().getSelectedItem();
                content.putString(selectedHyperlink.getText());
                clipboard.setContent(content);
            }
        });       //copy Event

        contextMenu.getItems().addAll(newItem, copyItem);

        return contextMenu;
    }

    /**
    Configuration for the ContextMenu(Right-Click) in the EventListView
     */
    private ContextMenu contextMenuEvent(){

        ContextMenu contextMenu = new ContextMenu();


        MenuItem editItem = new MenuItem("Edit");       //not implemented
        editItem.setOnAction(event -> {
            String selectedEvent = this.EventListView.getSelectionModel().getSelectedItem();
            editEvent(selectedEvent);
        });

        MenuItem newItem = new MenuItem("New Event");
        newItem.setOnAction(event -> {
            addEvent();
            putData();
        });

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            String selectedEvent = this.EventListView.getSelectionModel().getSelectedItem();
            familyTree.deleteEvent(actualPerson,selectedEvent);
            putData();
        });       //Remove Event

        MenuItem copyItem = new MenuItem("Copy Text");
        copyItem.setOnAction(event -> {
            if(this.EventListView.getSelectionModel().getSelectedItem() != null){
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();

                String selectedEvent = this.EventListView.getSelectionModel().getSelectedItem();
                content.putString(selectedEvent);
                clipboard.setContent(content);
            }

        });       //copy Event

        contextMenu.getItems().addAll(newItem, copyItem, deleteItem,editItem);

        return contextMenu;
    }

    /**
     * takes the selected eventFact (Information) to chage it
     * @param eventFact the object selected in "Informations"
     */
    private void editEvent(String eventFact) {
        String[] event = eventFact.split(": ");
        event[0] =  event[0].replace(":","");
        stage = (Stage) NamesListView.getScene().getWindow();

        stage2 = new Stage();
        stage2.setTitle("Edit Information");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        Label editLabel = new Label(event[0]);                //
        editLabel.setFont(Font.font(20));
        gridPane.add(editLabel, 0, 0, 2, 1);

        TextField editEventTF = new TextField();
        editEventTF.setText(event[1]);
        gridPane.add(editEventTF, 0, 1, 2, 1);

        Button OKbtn = new Button("OK");
        OKbtn.setOnAction(actionEvent -> {
            String[] temp = {editLabel.getText(),event[1], editEventTF.getText()};
            familyTree.editEvent(actualPerson, temp);
            stage2.close();
        });
        gridPane.add(OKbtn, 0, 2);

        Button Cancelbtn = new Button("Cancel");
        Cancelbtn.setOnAction(actionEvent -> stage2.close());
        gridPane.add(Cancelbtn, 1, 2);


        //gridPane.getChildren().addAll(Cancelbtn, editEventTF);
        stage2.setScene(new Scene(gridPane, 200, 200));
        stage.hide();
        stage2.showAndWait();
        stage.show();

        putData();

    }


    /**
    Creates a hyperlink for each person showing the Name. By clicking changes the Person on the main window.
     */
    private void initializeLinks(List<Person> personList){
        Hyperlink tempLink;
        PersonsListView.getItems().clear();

        for(Person person : personList){
            String tempS = person.getNames().get(0).getGiven();
            if (tempS == null)
                tempS = "";
            tempLink = new Hyperlink( tempS + " " + person.getNames().get(0).getSurname()
            +" ID:" + person.getId());
            tempLink.setOnAction(e -> {
                this.actualPerson = person;
                putData();
            });

            PersonsListView.getItems().add(tempLink);

        }




    }

    /**
    Asks for confirmation and then opens the file chooser to Select the File. After selected it initializes
    the FamilyTree and fill the Window with the informations and Links.
    */
    @FXML
    public void selectFile() {

        Optional<ButtonType> result = open.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {

                fileChooser.setTitle("Open Gedcom File");
                this.tempFile = fileChooser.showOpenDialog(stage);      //opens Window to select File
                if (tempFile.getName().contains(".ged"))            //Test if Right format
                {
                    System.out.println("File Import successful");
                    this.file = this.tempFile;
                    this.familyTree = new ImportGedcom2Object(file);
                    this.actualPerson = (this.familyTree.getAllPersons().get(0));
                    disableButtons(false);
                    initializeLinks(this.familyTree.getAllPersons());
                    putData();

                } else {
                    wrongFile.showAndWait();
                    System.out.println("Wrong Extention File");
                    tempFile = null;
                }


            } catch (Exception ex) {
                System.out.println("Error File Import");
            }
        }

        if (this.familyTree == null)
            newStart();


    }

    /**
        creates a new File in the wanted location and initializes a new familyTree and clears the GUI
     */
    @FXML
    private void newFile(){
        fileChooser.setTitle("Create Gedcom File");
        Optional<ButtonType> result = newFileConfirm.showAndWait();
        if (result.get() == ButtonType.OK){
            try{
                tempFile = fileChooser.showSaveDialog(stage);
                if(tempFile!=null){

                    familyTree = new ImportGedcom2Object();
                    file = tempFile;
                    stage = (Stage) this.NamesListView.getScene().getWindow();
                    stage.hide();
                    newStart();
                    stage.show();

                    initializeLinks(this.familyTree.getAllPersons());
                    putData();
                    disableButtons(false);

                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }


    }

    /**
    Saves the Written GEDCOM to the File. It creates a new File in case there is not any.
     */
    @FXML
    private boolean saveFile() {
        //fileChooser.setTitle("Save Gedcom File");

        if (file == null){
            return  saveFileAs();
        }
        else {

            Optional<ButtonType> saveResult = Save.showAndWait();
            if (saveResult.get() == ButtonType.YES) {
                System.out.println("Save Started");
                if(stage2 != null)
                    stage2.close();

                if (familyTree.saveGedcom(this.file)) {
                    success.showAndWait();
                    return true;
                } else {
                    failure.showAndWait();
                    return false;
                }


            } else {
                /*if(saveResult.get() == ButtonType.NO){
                    if(stage2 != null)
                        stage2.close();
                }*/
                return saveResult.get() == ButtonType.NO;
            }
        }
    }

    /**
    Saves creating a new File or Overwriting one
     */
    @FXML
    private boolean saveFileAs() {
        fileChooser.setTitle("Save Gedcom File");

        Optional<ButtonType> saveResult = Save.showAndWait();
        if(saveResult.get() == ButtonType.YES){
            System.out.println("Save Started");
            try{
                tempFile = fileChooser.showSaveDialog(stage);
                if(tempFile!=null){


                    if(familyTree.saveGedcom(this.tempFile)) {
                        file = tempFile;
                        success.showAndWait();
                        return true;
                    }
                    else {
                        failure.showAndWait();
                        return false;
                    }

                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }


        }else return saveResult.get() == ButtonType.NO;
        return false;
    }

    /**
    Function to ask for confirmation to close and if the person want to save the Program
     */
    public boolean closeProgram(){
        Optional<ButtonType> closeResult = close.showAndWait();
        if(closeResult.get() == ButtonType.OK){

            System.out.println("Save Process");
            try {
                return saveFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else
            return false;

        return false;
    }

    /**
    Insert the GEDCOM Information in the Fields, Combo-box, ListView...
     */
    @FXML
    private void putData() {

        this.EventListView.getItems().clear();
        this.FamilyListView.getItems().clear();
        this.NamesListView.getItems().clear();
        setFamilyListView();
        setEventListView();
        setNamesListView();

    }

    /**
    Insert the Information from the Category "Name"

     */
    private void setNamesListView(){

        String stringBuffer;

        for (Name name : actualPerson.getNames()){
             stringBuffer = name.getSurname();
            if(stringBuffer != null)
                NamesListView.getItems().add("Surname: " + stringBuffer);
            stringBuffer = name.getGiven();
            if (stringBuffer != null)
                NamesListView.getItems().add("Given name: " + stringBuffer);
            stringBuffer = name.getMarriedName();
            if (stringBuffer != null)
                NamesListView.getItems().add("Marriage name: " + stringBuffer);
            stringBuffer = name.getNickname();
            if (stringBuffer != null)
                NamesListView.getItems().add("Nickname: "+stringBuffer);
            stringBuffer = name.getPrefix();
            if (stringBuffer != null)
                NamesListView.getItems().add("Prefix: " + stringBuffer);
            stringBuffer = name.getAka();
            if (stringBuffer != null)
                NamesListView.getItems().add("Aka: "+stringBuffer);
            stringBuffer = name.getSuffix();
            if (stringBuffer != null)
                NamesListView.getItems().add("Suffix: " + stringBuffer);
            stringBuffer = name.getSurnamePrefix();
            if (stringBuffer != null)
                NamesListView.getItems().add("Surname prefix: "+stringBuffer);
            stringBuffer = name.getFone();
            if (stringBuffer != null)
                NamesListView.getItems().add("Fone: "+stringBuffer);

        }
    }

    /**
    Insert the Information from the Category "Event", like Birth, Death, Marriage... in the GUI(ListView)
     */
    private void setEventListView() {

        List<EventFact> eventFactList;
        String stringBuffer;
        eventFactList = familyTree.getEventFact(actualPerson);
        for (EventFact event : eventFactList){
            stringBuffer = event.getDisplayType() + ": ";

            if (event.getValue() != null)
                this.EventListView.getItems().add(stringBuffer + event.getValue());

            if (event.getDate() != null)
                this.EventListView.getItems().add(stringBuffer + event.getDate());

            if (event.getCause() != null)
                this.EventListView.getItems().add(stringBuffer + event.getCause());

            if (event.getEmail() != null)
                this.EventListView.getItems().add(stringBuffer + event.getEmail());

            if (event.getFax() != null)
                this.EventListView.getItems().add(stringBuffer + event.getFax());

            if (event.getPhone() != null)
                this.EventListView.getItems().add(stringBuffer + event.getPhone());

            if (event.getPlace() != null)
                this.EventListView.getItems().add(stringBuffer + event.getPlace());

            if (event.getRin() != null)
                this.EventListView.getItems().add(stringBuffer + event.getRin());
        }

    }

    /**
    Inserts the near related Family in the ListView calling the "setFamilyHelper" function
     */
    private void setFamilyListView(){

        setFamilyHelper("HUSB", "FAMS", "Husband: ");
        setFamilyHelper("WIFE","FAMS","Wife: ");
        setFamilyHelper("CHIL", "FAMS", "Child: ");

        setFamilyHelper("HUSB", "FAMC", "Father: ");
        setFamilyHelper("WIFE", "FAMC", "Mother: ");
        setFamilyHelper("CHIL", "FAMC", "Sibling: ");

    }

    /**
    Find the Person with the wanted relation and creates a hyperlink to access the person. It apears in the
    FamilyListView. As parameters it needs ("HUSB", "WIFE" or "CHIL"), ("FAMS" for the persons marriage family
    or "FAMC" for the persons parents Family) and a String for the output, "Father: " for example.
    It will check if it is the same person, so the Husband doesn't appears in the Family again as husband from
    himself.
     */
    private void setFamilyHelper(String assTag, String typeFam, String assName){
        List<Person> personBuffer;
        Hyperlink tempLink;

        personBuffer = familyTree.getNearFamily(this.actualPerson, assTag, typeFam);
        if(personBuffer != null){
            for (Person p : personBuffer){
                if (p != this.actualPerson){
                    tempLink = new Hyperlink(assName + p.getNames().get(0).getGiven()
                            + " " + p.getNames().get(0).getSurname()
                            + " ID:"+p.getId());
                    tempLink.setOnAction(e -> {
                        this.actualPerson = p;
                        putData();
                    });

                    this.FamilyListView.getItems().add(tempLink);
                }

            }
        }

    }

    /**
     *
     */
    @FXML
    private void addFamilyMember() {
        stage = (Stage) NamesListView.getScene().getWindow();
        try {
            disableButtons(true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addFamilyMember.fxml"));
            Parent root = loader.load();

            addFamilyMemberController addFamilyMemberController = loader.getController();
            addFamilyMemberController.setData(familyTree, actualPerson, this);
            stage2 = new Stage();

            //stage.setTitle("New Familiar from " + user.getActualPerson().getNames().get(0).getDisplayValue());
            stage2.setScene(new Scene(root));
            stage2.setTitle("New Family Member of "+ actualPerson.getNames().get(0).getDisplayValue());
            stage2.setOnCloseRequest(e->{
                e.consume();
                disableButtons(false);
                stage2.close();
            });

            disableButtons(true);
            stage.hide();
            stage2.showAndWait();
            stage.show();

        } catch (IOException ex) {
            System.err.printf("Error: %s%n", ex.getMessage());
        }

    }

    /**
     * eneables and disables the buttons in the window
     * @param disable if disable, true. Eneable false
     */
    public void disableButtons(boolean disable){
        addFamilyBtn.setDisable(disable);
        addEventBtn.setDisable(disable);
        editNamesBtn.setDisable(disable);

    }

    /**
     * opens a stage to edit the Person's Name
     */
    @FXML
    private void editNames() {
        stage = (Stage) NamesListView.getScene().getWindow();
        try{
            disableButtons(true);
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("editNames.fxml"));
            Parent root = fxmlLoader.load();

            EditNames editNamesController = fxmlLoader.getController();
            editNamesController.setData(familyTree, actualPerson, this);    //need a scene.setController? is now working

            stage2 = new Stage();
            stage2.setScene(new Scene(root));

            stage2.setTitle("Edit "+ actualPerson.getNames().get(0).getDisplayValue());
            stage2.setOnCloseRequest(e->{
                e.consume();
                stage2.close();
                disableButtons(false);
            });

            stage.hide();
            stage2.showAndWait();
            stage.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }

    /**
     * Connection with other controllers to exchange data
     * @param obj   a object of type ImportGedcom2Object
     * @param ap    the actual person
     */
    public void getData(ImportGedcom2Object obj, Person ap){
        this.familyTree = obj;
        this.actualPerson = ap;
        putData();
        initializeLinks(familyTree.getAllPersons());
        disableButtons(false);
    }

    @FXML
    private void addEvent(){
        stage = (Stage) NamesListView.getScene().getWindow();
        try{
            disableButtons(true);
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("addEvent.fxml"));
            Parent root = fxmlLoader.load();

            addEventController addEventController = fxmlLoader.getController();
            addEventController.setData(familyTree, actualPerson, this);    //need a scene.setController? is now working

            stage2 = new Stage();
            stage2.setScene(new Scene(root));

            stage2.setTitle("Add Info for "+ actualPerson.getNames().get(0).getDisplayValue());
            stage2.setOnCloseRequest(e->{
                e.consume();
                stage2.close();
                disableButtons(false);
            });

            stage.hide();
            stage2.showAndWait();
            stage.show();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}




