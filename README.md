# EventRecommendationApp

## Summary 
This is an Event Search and Recommendation web app. User can search the events 
around, and the app will analyze users' taste then push specific envents to the user 

## Programming Techniques 
* FrontEnd: HTML|CSS|JavaScript  
* Backend:JAVA
* Server:TomcatServer|mySQLServer 
* Event&Tickets Info: TicketsMaster API 

## Development Tools
* MAMP: manage the SQL Server 
* POSTMAN: Make HTTP request to test the Java Server 

## Design 
### DataBase 
Four tables are used
* users: Store the basic information of users // demonstrates the relations between userId <-> user name. 
* items: Store the basic information of items // demonstrates the relations between itemId <-> item basic info(name, time, location)  
* categories: Only Store the category corresponding to a specific item // demonstrates the relations between itemId <-> category  
* history: Store the love history of users on specific item, each entry contains 1 user and 1 item. // demonstrates the relations between userId <-> ItemId

### Search Items 
* The search functionality is implemented with the help of TicketMaster API, it takes [lat] [long] [term] as parameter to search items in a specific geo range 
* All the search results are stored in the items table in the database 

### User Authentication 
The User Authentication is implemented based on Session. Session Id is bound to a specific Http servlet request, the server side determine if the user who is sending this request has logged in or not by checking if there is a valid session(HttpSession) exists on the server side. 

### Recommendation 
The recommendation of items to users is based on the items loved by users. More specifically, it is based on the categories of those items. The server side will get all users' favorite items and analyze the category of each item. Finally, it will guess 
which category is the user's favorite one, it will search the items with this category inside some specific geography range and then return the search result to user. 
