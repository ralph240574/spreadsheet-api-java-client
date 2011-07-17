#Google Spreadsheet API Java Client

This API can be used to acces Google Spreadsheets. It uses the newer [Google API Java Client](http://code.google.com/p/google-api-java-client/).


## How to get started
This API depends on the core [spreadsheet-api] (https://github.com/ralph240574/spreadsheet-api):

Clone it first:

   git clone git://github.com/ralph240574/spreadsheet-api.git
   

Build with Maven:

   mvn clean install
   
   
Then clone this API:

   git clone git@github.com:ralph240574/spreadsheet-api-java-client.git
   
Build with Maven:

   mvn clean install
   
Note when Maven runs the the Unit tests it will open a browser window and ask for access to your Google account. This is necessary to receive the oauth token for authentication. You can use any google account for this, but your account information will never be exposed to the client directly.

##How to use the client
For example how to use the client look at the examples in the Examples folder. For an introduction.

Here is an introduction for the [Google Spreadsheet API] (http://code.google.com/apis/spreadsheets/).

Note: this client is using version 2.0 of the API






   
      

