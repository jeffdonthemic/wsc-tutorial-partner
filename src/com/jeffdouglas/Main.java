package com.jeffdouglas;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Main {
  
  static final String USERNAME = "";
  static final String PASSWORD = "";
  static PartnerConnection connection;

  public static void main(String[] args) {

    ConnectorConfig config = new ConnectorConfig();
    config.setUsername(USERNAME);
    config.setPassword(PASSWORD);
    //config.setTraceMessage(true);
    
    try {
      
      connection = Connector.newConnection(config);
      
      // display some current settings
      System.out.println("Auth EndPoint: "+config.getAuthEndpoint());
      System.out.println("Service EndPoint: "+config.getServiceEndpoint());
      System.out.println("Username: "+config.getUsername());
      System.out.println("SessionId: "+config.getSessionId());
      
      queryContacts();
      createAccounts();
      updateAccounts();
      deleteAccounts();
      
    } catch (ConnectionException e1) {
        e1.printStackTrace();
    }  

  }
  
  
  private static void queryContacts() {
    
    System.out.println("Querying for the 5 newest Contacts...");
    
    try {
       
      // query for the 5 newest contacts      
      QueryResult queryResults = connection.query("SELECT Id, FirstName, LastName, Account.Name " +
            "FROM Contact WHERE AccountId != NULL ORDER BY CreatedDate DESC LIMIT 5");
      if (queryResults.getSize() > 0) {
        for (SObject s : queryResults.getRecords()) {
          System.out.println("Id: " + s.getId() + " " + s.getField("FirstName") + " " + 
              s.getField("LastName") + " - " + s.getChild("Account").getField("Name"));
        }
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }    
    
  }

  private static void createAccounts() {
    
    System.out.println("Creating 5 new test Accounts...");
    SObject[] records = new SObject[5];
    
    try {
       
      // create 5 test accounts
      for (int i=0;i<5;i++) {
        SObject so = new SObject();
        so.setType("Account");
        so.setField("Name", "Test Account "+i);
        records[i] = so;
      }
      
      // insert the records
      SaveResult[] saveResults = connection.create(records);
      
      // check the results for any errors
      for (int i=0; i< saveResults.length; i++) {
        if (saveResults[i].isSuccess()) {
          System.out.println(i+". Successfully created record - Id: " + saveResults[i].getId());
        } else {
          Error[] errors = saveResults[i].getErrors();
          for (int j=0; j< errors.length; j++) {
            System.out.println("ERROR creating record: " + errors[j].getMessage());
          }
        }    
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }    
    
  }
  
  private static void updateAccounts() {
    
    System.out.println("Update the 5 new test Accounts...");
    SObject[] records = new SObject[5];
    
    try {
       
      QueryResult queryResults = connection.query("SELECT Id, Name FROM Account ORDER BY CreatedDate DESC LIMIT 5");
      if (queryResults.getSize() > 0) {
        for (int i=0;i<queryResults.getRecords().length;i++) {
          SObject so = (SObject)queryResults.getRecords()[i];
          System.out.println("Updating Id: " + so.getId() + " - Name: "+so.getField("Name"));
          // create an sobject and only send fields to update
          SObject soUpdate = new SObject();
          soUpdate.setType("Account");
          soUpdate.setId(so.getId());
          soUpdate.setField("Name", so.getField("Name")+" -- UPDATED");
          records[i] = soUpdate;
        }
      }
      
      // update the records
      SaveResult[] saveResults = connection.update(records);
      
      // check the results for any errors
      for (int i=0; i< saveResults.length; i++) {
        if (saveResults[i].isSuccess()) {
          System.out.println(i+". Successfully updated record - Id: " + saveResults[i].getId());
        } else {
          Error[] errors = saveResults[i].getErrors();
          for (int j=0; j< errors.length; j++) {
            System.out.println("ERROR updating record: " + errors[j].getMessage());
          }
        }    
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }    
    
  }
  
  private static void deleteAccounts() {
    
    System.out.println("Deleting the 5 new test Accounts...");
    String[] ids = new String[5];
    
    try {
       
      QueryResult queryResults = connection.query("SELECT Id, Name FROM Account ORDER BY CreatedDate DESC LIMIT 5");
      if (queryResults.getSize() > 0) {
        for (int i=0;i<queryResults.getRecords().length;i++) {
          SObject so = (SObject)queryResults.getRecords()[i];
          ids[i] = so.getId();
          System.out.println("Deleting Id: " + so.getId() + " - Name: "+so.getField("Name"));
        }
      }
      
      // delete the records
      DeleteResult[] deleteResults = connection.delete(ids);
      
      // check the results for any errors
      for (int i=0; i< deleteResults.length; i++) {
        if (deleteResults[i].isSuccess()) {
          System.out.println(i+". Successfully deleted record - Id: " + deleteResults[i].getId());
        } else {
          Error[] errors = deleteResults[i].getErrors();
          for (int j=0; j< errors.length; j++) {
            System.out.println("ERROR deleting record: " + errors[j].getMessage());
          }
        }    
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }    
    
  }

}
