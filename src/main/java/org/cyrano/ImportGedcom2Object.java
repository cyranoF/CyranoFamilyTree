package org.cyrano;

import org.folg.gedcom.model.*;
import org.folg.gedcom.parser.ModelParser;
import org.folg.gedcom.visitors.GedcomWriter;
import org.xml.sax.SAXParseException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImportGedcom2Object {

    private Gedcom gc;
    private final Map<String, String> eventTagDisplayMap = new HashMap<>();

    public List<Person> getAllPersons(){
        return gc.getPeople();
    }

    private static final Set<String> eventFactTagsList = new HashSet<>(Arrays.asList("ADOP",
            "BAPM", "BARM", "BASM", "BIRT", "BLES", "BURI", "CAST", "CENS", "CHR", "CHRA",
            "CONF", "CREM", "DEAT", "DEED", "DEPA", "DIV", "DIVF", "DSCR", "EDUC", "EMAIL", "EMIG", "ENGA", "EVEN",
            "EXCO", "EYES", "FACT", "GRAD", "HAIR", "HEIG", "IDNO", "ILL", "ILLN", "IMMI", "MARR",
            "MOVE", "NATI", "NATU", "NCHI", "NMR", "OCCU", "ORDI", "ORDN", "PHON", "PROB", "PROP", "RELI", "RESI",
            "RETI", "SEX", "SSN", "TITL", "WEIG", "WILL", "_COLOR", "_DEGREE"));

    /**
    Constructor to import the GEDCOM from a .ged file
     */
    public ImportGedcom2Object(File file) {

        try {
            ModelParser mp = new ModelParser();
            gc = mp.parseGedcom(file);
        } catch (SAXParseException | IOException e) {
            e.printStackTrace();
        }

        //TreeParser tp = new TreeParser();                 //for the Tags
        //List<GedcomTag> gtList = tp.parseGedcom(file);
        gc.createIndexes();
        setEventTags();
    }

    /**
     * Constructor in case there is no File to import
     */
    public ImportGedcom2Object(){
        gc = new Gedcom();
        gc.createIndexes();
        setEventTags();
        List<Family> familyList = new ArrayList<>();
        gc.setFamilies(familyList);
    }

    /**
     * creates a map for the tags ant the respective descriptions, e.g. SURM, Surname
     * uses the Tags I selected on "eventFactTagsList"
     */
    private void setEventTags(){
        for( String Tags: eventFactTagsList){
            String stringbuffer = EventFact.DISPLAY_TYPE.get(Tags);

            if (stringbuffer != null){
                eventTagDisplayMap.put(Tags,stringbuffer);
            }
        }
    }

    public Map<String, String> getEventTagDisplayMap(){
        return eventTagDisplayMap;
    }

    /**
     * after adding new People or modifying the Family need to redo the Indexes
     */
    public void reloadIndexes(){
        gc.createIndexes();
    }

    /**
    Function to find first grade associates, Spouse, Children, Sister, Brother using:
    HUSB, WIFE and CHIL | FAMS and FAMC
     @param p person to be analysed
     @param assTag "AssociationTag" can be HUSB, WIFE and CHIL. Used to know "who from typeFam"
     @param typeFam only FAMS and FAMC. To know if the person is a "Spouse" or "Children" from the Family
     */
    public List<Person> getNearFamily(Person p, String assTag, String typeFam) {

        List<SpouseFamilyRef> spouseFamilyRefList=  p.getSpouseFamilyRefs();
        List<ParentFamilyRef> parentFamilyRefList = p.getParentFamilyRefs();
        List<Person> personBuffer = new ArrayList<>();

        switch (assTag){
            case "HUSB":
                if (typeFam.equals("FAMS")){                    //husband
                    for (SpouseFamilyRef sRef : spouseFamilyRefList){
                        personBuffer = gc.getFamily(sRef.getRef()).getHusbands(gc);
                    }
                }
                else if(typeFam.equals("FAMC")){                //father
                    for (ParentFamilyRef pRef : parentFamilyRefList){
                        personBuffer = gc.getFamily(pRef.getRef()).getHusbands(gc);
                    }
                }
                break;

            case "WIFE":
                if (typeFam.equals("FAMS")){                    //wife
                    for (SpouseFamilyRef sRef : spouseFamilyRefList){
                        personBuffer = gc.getFamily(sRef.getRef()).getWives(gc);
                    }
                }
                else if(typeFam.equals("FAMC")){                //mother
                    for (ParentFamilyRef pRef : parentFamilyRefList){
                        personBuffer = gc.getFamily(pRef.getRef()).getWives(gc);
                    }
                }
                break;

            case "CHIL":
                if (typeFam.equals("FAMS")){                    //children
                    for (SpouseFamilyRef sRef : spouseFamilyRefList){
                        personBuffer = gc.getFamily(sRef.getRef()).getChildren(gc);
                    }
                }
                else if(typeFam.equals("FAMC")){                //sibling
                    for (ParentFamilyRef pRef : parentFamilyRefList){
                        personBuffer = gc.getFamily(pRef.getRef()).getChildren(gc);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + assTag);
        }

        return personBuffer;

    }

    /**
    Get the Information Classified as "Event"
    @param p Person to be analysed
     @return List of "EventFacts" like sex, birth, death...
     */
    public List<EventFact> getEventFact(Person p){

        return p.getEventsFacts();

    }

    /**
     * Get the Information Classified as "Event" from the person with the wanted Tag, Example "SEX"
     * @param p Person
     * @param Tag   GEDCOM Tag wanted
     * @return  Eventfact with the Tag
     */
    public EventFact getEventFact(Person p, String Tag){
        EventFact eventFact = null;
        List<EventFact> eventFactList;
        eventFactList = p.getEventsFacts();
        for (EventFact event : eventFactList){
            if (event.getTag().equals(Tag))
                eventFact = event;
        }
        return eventFact;
    }

    /**
     * changes the Names of the person at the GEDCOM Variable
     * @param person person with the changed Names
     */
    public void changePersonNames(Person person){
       String id  = person.getId();

       gc.getPerson(id).setNames(person.getNames());

    }

    /**
     * Adds the new Family member
     * @param mainPerson the basis Person
     * @param newPerson  the new Member with ID, Name etc
     * @param relation type of relation (Child, Partner,Parent,Sibling) between both
     */
    public void addFamilyMember(Person mainPerson, Person newPerson, String relation){      //Child, Partner,Parent,Sibling
        String reference;
        Family family = null, oldFamily = null;
        List<Family> familyList = gc.getFamilies();
        ChildRef childRef = new ChildRef();
        SpouseRef spouseRef = new SpouseRef();
        SpouseFamilyRef spouseFamilyRef = new SpouseFamilyRef();
        ParentFamilyRef parentFamilyRef = new ParentFamilyRef();

        switch (relation){
            case"Child":
                                                //what if many families????????????
                if (!mainPerson.getSpouseFamilyRefs().isEmpty()){       //there is already a Family Reference
                    family = mainPerson.getSpouseFamilyRefs().get(0).getFamily(gc);
                    oldFamily = mainPerson.getSpouseFamilyRefs().get(0).getFamily(gc);
                    reference = family.getId();


                }
                else {
                    family = new Family();
                    oldFamily = null;
                    reference = newFamilyId();
                    family.setId(reference);
                    //adds the main person as Dad/Mother

                    spouseRef.setRef(mainPerson.getId());
                    if (getSex(mainPerson).equals("M")){           //get sex from main!!!!!!!!!!!!!!!!!
                        family.addHusband(spouseRef);
                    }else {
                        family.addWife(spouseRef);
                    }

                    //Set at Parent the connection
                    spouseFamilyRef.setRef(reference);
                    mainPerson.addSpouseFamilyRef(spouseFamilyRef);

                }

                //add the new person to the Family
                childRef.setRef(newPerson.getId());
                family.addChild(childRef);
                //set at the person FAMC ***
                parentFamilyRef.setRef(reference);
                newPerson.addParentFamilyRef(parentFamilyRef);
                break;

            case"Sibling":

                if (!mainPerson.getParentFamilyRefs().isEmpty()){       //there is already a Family Reference
                    family = mainPerson.getParentFamilyRefs().get(0).getFamily(gc);
                    oldFamily = mainPerson.getParentFamilyRefs().get(0).getFamily(gc);
                    reference = family.getId();
                    parentFamilyRef.setRef(reference);                      //test if multiple use work

                }
                else {                      //test if diferent IDs work
                    family = new Family();                      //problems
                    oldFamily = null;
                    reference = newFamilyId();
                    family.setId(reference);
                    //adds the main person as sibling
                    childRef.setRef(mainPerson.getId());                    //
                    family.addChild(childRef);

                    //Set at Parent the connection
                    parentFamilyRef.setRef(reference);
                    mainPerson.addParentFamilyRef(parentFamilyRef);
                }
                //adds the new person as a Child
                childRef = new ChildRef();              //carefull use same ref
                childRef.setRef(newPerson.getId());
                family.addChild(childRef);

                //set at the person FAMC ***
                newPerson.addParentFamilyRef(parentFamilyRef);
                break;

            case "Partner":
                if (!mainPerson.getSpouseFamilyRefs().isEmpty()){       //there is already a Family Reference
                    family = mainPerson.getSpouseFamilyRefs().get(0).getFamily(gc);
                    oldFamily = mainPerson.getSpouseFamilyRefs().get(0).getFamily(gc);
                    reference = family.getId();
                    spouseFamilyRef.setRef(reference);
                }
                else {              //new reference
                    family = new Family();
                    oldFamily = null;
                    reference = newFamilyId();
                    family.setId(reference);

                    spouseRef.setRef(mainPerson.getId());//who?
                    if (getSex(mainPerson).equals("M")){
                        family.addHusband(spouseRef);
                    }else {
                        family.addWife(spouseRef);
                    }
                    spouseFamilyRef.setRef(reference);
                    mainPerson.addSpouseFamilyRef(spouseFamilyRef);


                }
                //add the new person to the Family
                spouseRef = new SpouseRef();
                spouseRef.setRef(newPerson.getId());
                if (getSex(newPerson).equals("M")){
                    family.addHusband(spouseRef);
                }else {
                    family.addWife(spouseRef);
                }

                //set at the person FAMS ***
                newPerson.addSpouseFamilyRef(spouseFamilyRef);


                break;
            case "Parent":
                if (!mainPerson.getParentFamilyRefs().isEmpty()){       //there is already a Family Reference
                    family = mainPerson.getParentFamilyRefs().get(0).getFamily(gc);
                    oldFamily = mainPerson.getParentFamilyRefs().get(0).getFamily(gc);
                    reference = family.getId();

                }                           //erro
                else {
                    family = new Family();
                    oldFamily = null;
                    reference = newFamilyId();
                    family.setId(reference);

                    childRef.setRef(mainPerson.getId());
                    family.addChild(childRef);
                    parentFamilyRef.setRef(reference);
                    mainPerson.addParentFamilyRef(parentFamilyRef);

                }

                //add the new person to the Family
                spouseRef.setRef(newPerson.getId());
                if (getSex(newPerson).equals("M")){           //get sex from main!!!!!!!!!!!!!!!!!
                    family.addHusband(spouseRef);
                }else {
                    family.addWife(spouseRef);
                }

                //set at the person FAMS ***
                spouseFamilyRef.setRef(reference);
                newPerson.addSpouseFamilyRef(spouseFamilyRef);
                break;

        }

        if (family != null){
            familyList.remove(oldFamily);
            addPerson(newPerson);
            System.out.println("new Family Set");
            familyList.add(family);
            gc.setFamilies(familyList);

        }


        reloadIndexes();
    }

    /**
     * Searches the last Family ID and adds 1 to the index. May have problems if IDs not organized
     * @return new Family ID of type String
     */
    private String newFamilyId(){

        if (gc.getFamilies().isEmpty())
            return "F1";
        String idS = gc.getFamilies().get(gc.getFamilies().size()-1).getId().replace("F", "");
        int id =  Integer.parseInt(idS);
        idS = "F" + (id+1);
        return idS;
    }

    /**
     * It searches in the EventFacts for the Tag "SEX" and gives the value as Output
     * It only gives the first "SEX" if many
     * @param person    person to be analysed
     * @return  the sex or "notFound" if not found
     */
    public String getSex(Person person){
        List<EventFact> eventFactList = person.getEventsFacts();
        for (EventFact eventFact : eventFactList){
            if (eventFact.getTag().equals("SEX"))
                return eventFact.getValue();
        }
        return "notFound";
    }

    /**
     *
     * @param id    Exaample "I1"
     * @return      the person
     */
    public Person getPerson(String id){
        return gc.getPerson(id);
    }

    /**
     *
     * @param file the file to be saved(where to save)
     * @return if it was successful
     */
    public boolean saveGedcom(File file)  {
        try{
            if(file!=null){
                GedcomWriter writer = new GedcomWriter();
                writer.write(gc, file);
                return true;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Searches the last persons ID and adds 1. May have problem if Ids not organizes
     * @param person    person to have new ID
     * @return  person with the new ID
     */
    public Person createNewPersonId(Person person){
        String idS = gc.getPeople().get(gc.getPeople().size()- 1).getId().replace("I", "");
        int id =  Integer.parseInt(idS);
        idS = "I" + (id+1);
        person.setId(idS);
        return person;
    }

    /**
     * add the new Person if not already existent
     * @param person Person to be added
     */
    public void addPerson(Person person){

        if (person != null){
            if(!gc.getPeople().contains(person)){
                if (person.getId().isEmpty())
                    person = createNewPersonId(person);
                gc.addPerson(person);
                reloadIndexes();
                return;
            }
            System.out.println("Person to add, already exist");
        }

    }

    /*
        rearrange the Ids?
     */
    public boolean deletePerson(Person person){

        if(person != null &(gc.getPeople().contains(person))){
            //String idS = person.getId();            //the Persons Id will be unused

            gc.getPeople().remove(person);
            reloadIndexes();
            return true;

        }

        return false;
    }

    //change to boolean?
    public void deleteConnection(Person mainPerson,Person otherPerson){
        //not implemented
    }

    /**
     * Removes the event wanted
     * @param person    person to be modified
     * @param eventFact tipical string is "Sex: M", it is made to take the value from the EventListView
     */
    public void deleteEvent(Person person, String eventFact){
        String[] event = eventFact.split(": ");
        event[0] =  event[0].replace(":","");
        int i = 0;

        List<EventFact> eventFactList= person.getEventsFacts();
        for (EventFact e : eventFactList){
            if (e.getDate() != null){
                if (e.getDate().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setDate(null);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getValue() != null){
                if (e.getValue().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setValue(null);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getPlace() != null){
                if (e.getPlace().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setPlace(null);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getCause() != null){
                if (e.getCause().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setCause(null);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getEmail() != null){
                if (e.getEmail().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setEmail(null);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getPhone() != null){
                if (e.getPhone().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setPhone(null);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }

            i++;
        }

        gc.getPerson(person.getId()).setEventsFacts(eventFactList);

    }

    public void editEvent(Person person, String[] event){       //0 dysplay, 1 old value, 2 new value

        int i = 0;
        List<EventFact> eventFactList= person.getEventsFacts();

        for (EventFact e : eventFactList){
            if (e.getDate() != null){
                if (e.getDate().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setDate(event[2]);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getValue() != null){
                if (e.getValue().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setValue(event[2]);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getPlace() != null){
                if (e.getPlace().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setPlace(event[2]);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getCause() != null){
                if (e.getCause().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setCause(event[2]);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getEmail() != null){
                if (e.getEmail().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setEmail(event[2]);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }
            if (e.getPhone() != null){
                if (e.getPhone().equals(event[1]) && e.getDisplayType().equals(event[0])){
                    eventFactList.get(i).setPhone(event[2]);
                    System.out.println(e.getTag() + " Changed");
                    break;
                }
            }

            i++;
        }

        gc.getPerson(person.getId()).setEventsFacts(eventFactList);
    }


}
