# SQLPracticeDatabase
SQLPracticeDatabase is program that allows you practice making queries to MySQL and SQLite databases. Once you run the jar you will prompted to select a file location. 
Upon selecting a location, the config, problems, settings, and readme file will be saved to that location. This path will then be saved in your systems preferences so it can
be referenced again in the future. From there and in the future the main java gui will auto open which is where you can practice making database queries. You will be able to
create your own problems, answers, and databases or you can just practice making queries with the provided example database.

# How it works
By default, it will run an example tutorial SQLite database which you can practice on. The example database is limited to 2 tables with 6 columns each. However, you can add
as many rows as you would like along with being able to change any of the data. Keep in mind this 6-column limit is also in place for all user created databases but there
won’t be a table limit. In the future I can remove the column restrictions. The practice database also allows you edit, add, or remove any problems. The practice database will 
be saved as tutorial.db 

The following image depicts the general design. You are able to get a problems description which will be generated in the top box. By default, the correct queried data will 
be shown bellow the description but if you want you can make it so this info is only shown when you get the answer via the config. The bottom box is where you the user can 
type. Clicking ‘Query’ will then check your answer. Your queried data will then also appear in top box. Clicking ‘Get Answer’ will then reveal the answer, explanation 
(if one is included) and the data queried. Clicking ‘Next’ will skip the problem you are currently on. You also are able to change the font size and font type via the 
top left corner.

![image](https://user-images.githubusercontent.com/83735831/211122048-c801b523-7d7a-46f1-9919-306974813546.png)

# How to make your own SQLite practice database
Firstly, if you are wanting to make a flat file SQLite database you will first need to download [SQLite Db Browswer](https://sqlitebrowser.org/dl/) which will give you an
easy way to create databases. Using that you can also add all of the tables and columns before then populating it. Once that is done place the db file next to the
other auto generated files. Secondly, go into your config file and make sure 'mysql' is set to false then ensure 'file-type' matches your file type. After that make sure 
you set 'use-example' to false and lastly change 'database' to the name flat file database.

# How to make your own MySQL practice database
I first suggest downloading something like [MySQL Workbench](https://www.mysql.com/) since that will make generating a new database much easier. Once you create your database
and add all of your tables, columns, and rows you will be ready to connect to it. Firstly, go into the config file and change 'use-example' to false. Then change 'mysql'
to true. Lastly, enter your host, port, database, username, and password into the config file. Once all this is done you will be able to create your own problem sets before
finally being able to run SQLPracticeDatabase's jar.

# How to run jar files
Firstly, make sure you have Java (JRE) downloaded. Then you can try to run the jar file by clicking on it. If that does not work you can try running it from your command
prompt. For example if SQLPracticeDatabase-1.0.jar is located on your desktop you can first type 'cd desktop' into your command prompt. Then type 'java -jar SQLPracticeDatabase-1.0.jar'
of course without the ' symbol into cmd before clicking enter. 
