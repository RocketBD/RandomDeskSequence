# RandomDeskSequence

This light program is used to create a random sequence of an ordered list of objects.  
For example, it's a good idea to create the list of **seat order for students who are going
to take an exam**.

# How to Use

**For normal users:**
All you need to is to copy the ordered list (in UTF-8 coded text file (`namelist.txt`)) into where
you save this program. You can adjust the path by creating a `settings.properties` into
where you save this program.  
If you have created `settings.properties`, you can launch it in GUI mode, in English or with
a sorting method which will sort the generated list by its sequence.
**For developers:**
Copy your file into *src/cn/rocket/randdeskseq/main/names.txt*.

# About `settings.properties`

|Key            |Value   |Description|
|---------------|--------|-----------|
|for*n*chars    |integer |           |
|mode           |boolean |           |
|importFile     |string  |           |
|exportFile     |string  |           |
|sortingByOrigin|boolean |           |
|language       |boolean |           |

# Coming Features

- Import and export in Excel files

# Copyright

Copyright(c) 2021 RocketBD