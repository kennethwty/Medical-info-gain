# medical-info-gain
This project has two parts.<br>
<b>Part 1:</b> Creates a relation (table) in MySQL database (using Java) to hold a set of mutations that will be analyzed in part 2.<br>
<b>Part 2:</b> (Data Mining) Finds the gene mutation that is most likely the cause of death in patients/most likely a factor that leads to death in patients by calculating information gains. This builds a decision tree that will predict whether a new patient with one or more of the targeted gene mutations will survive.<br><br>
<em>Technologies used: Java, JDBC, MySQL, and Oracle DB.</em>
<br><br>
<strong>To run the code:</strong>
<br>
1) The first part of the project creates a table that holds the 10 mutations in your local MySQL database. You will need to first download the included MySQL connector and possibly put it in the lib folder in NetBeans<br>
<br>
2) The second part also needs the MySQL connector as well as the Oracle DB connector. Both connectors are already included in this repository. The path to the Oracle DB is already in the .java file. If my school still holds the data, the link will work. <br><br>Thank you.
