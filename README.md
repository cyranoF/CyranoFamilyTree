# FamilyTree

Java Project for college

This Software was done complety in Java as a Project for my college at Electrical Engeneering.

It has the function to manage a Genealogy Tree in GEDCOM 5 format, it can import or start a new one. Actualy it can add and edit names(like Surname, Given Name...), informations(like "Sex", "Birth"...) and add new family members.

For the Import and Display I tried to display everything it can. For the adding new things I limited the amount and generalized most for time and objective reasons.

In the Import function it is able to recognize most Tags from WeRelate.org and in the adding function it is able to add several of the most relevant Tags:
            
            "BAPM", "BARM", "BASM", "BIRT", "BLES", "BURI", "CAST", "CENS", "CHR", "CHRA",
            "CONF", "CREM", "DEAT", "DEED", "DEPA", "DIV", "DIVF", "DSCR", "EDUC", "EMAIL", "EMIG", "ENGA", "EVEN",
            "EXCO", "EYES", "FACT", "GRAD", "HAIR", "HEIG", "IDNO", "ILL", "ILLN", "IMMI", "MARR",
            "MOVE", "NATI", "NATU", "NCHI", "NMR", "OCCU", "ORDI", "ORDN", "PHON", "PROB", "PROP", "RELI", "RESI",
            "RETI", "SEX", "SSN", "TITL", "WEIG", "WILL", "_COLOR", "_DEGREE" and the Tags for Names and Family conections.
For Tags description: http://genwiki.genealogy.net/GEDCOM-Tags

It uses a library from "FamilySearch" to convert the .ged file to Java objects (https://github.com/FamilySearch/gedcom5-java).

If you don't want to open it on the IDE, you can execute the "FamilyTree.jar" in the folder "executable", but you need JDK 15 to do it.
Link for JDK: https://www.oracle.com/java/technologies/javase-jdk15-downloads.html

Enjoy It!

