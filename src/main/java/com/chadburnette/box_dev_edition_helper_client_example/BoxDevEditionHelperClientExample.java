package com.chadburnette.box_dev_edition_helper_client_example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jose4j.lang.JoseException;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxUser;
import com.chadburnette.box_dev_edition_helper.BoxDevEditionHelper;
import com.mashape.unirest.http.exceptions.UnirestException;

public class BoxDevEditionHelperClientExample {
	
    public static void main( String[] args ){     
        try {   
        	
        	Properties prop = new Properties();   
        	InputStream input = new FileInputStream("config.properties");
    		prop.load(input);
    		input.close();
     
    		String clientId = prop.getProperty("boxClientId");
    		String clientSecret = prop.getProperty("boxClientSecret");
    		String enterpriseId = prop.getProperty("boxEnterpriseId");
    		String privateKeyPassword = prop.getProperty("boxPrivateKeyPassword");
    		String privateKey = prop.getProperty("boxPrivateKey");
        	
        	BoxDevEditionHelper boxHelper = new BoxDevEditionHelper(enterpriseId, clientId, clientSecret, privateKey, privateKeyPassword);
        	
			String adminToken = boxHelper.getEnterpriseToken();
			System.out.println("\nRetrieved Enterprise Token: " + adminToken);
			
			BoxAPIConnection adminClient = new BoxAPIConnection(adminToken);
			BoxFolder rootFolder = BoxFolder.getRootFolder(adminClient);
			System.out.println("\nAdmin account root folder items:");
	        for (BoxItem.Info itemInfo : rootFolder) 
	        {
	        	System.out.format("\t[%s] %s\n", itemInfo.getID(), itemInfo.getName());
	        }
	        
	        String userId = boxHelper.createAppUser("test user", adminToken);
	        System.out.println("\nCreated App User: " + userId);
	        
	        String userToken = boxHelper.getUserToken(userId);
	        System.out.println("Retrieved User Token: " + userToken);
	        
	        BoxAPIConnection userClient = new BoxAPIConnection(userToken);
	        BoxUser userInfo = BoxUser.getCurrentUser(userClient);
	        System.out.println("\nApp User details:");
	        System.out.println("\tID: " + userInfo.getID());
	        
	        boxHelper.deleteAppUser(userId, adminToken);
	        System.out.println("\nDeleted App User");
			
		} catch (JoseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnirestException e) {
			e.printStackTrace();
		}
    }
}
