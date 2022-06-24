/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chris Clarke
 * TM CJCSoft
 */

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.awt.FileDialog;

import java.awt.*;

import java.io.*;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import java.nio.file.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.LinkedList;

import javax.swing.JOptionPane;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.Collections.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import org.w3c.dom.NodeList;

import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Node;


public class FileIOClass 
{
    String strCustomerDatabaseFileName;
    String strPathToMyDocuments;
    String strPathToFile;
    String strProductsDatabaseFileName;
    
    public final char SEPARATOR = ',';
    
    LinkedList<CustomerAccountClass.CustomerAccountDetails> listOfCustomers = new LinkedList();  
              
    public boolean CheckUserFile()
    {
        
        boolean boolGoodUserProfile = false;
        
        String strPathToFile = GetUserProfileFileName();
        
        if(Files.exists(Paths.get(strPathToFile)))
        {
                    
            boolGoodUserProfile = true;
            
        }
                    
        return boolGoodUserProfile;
        
    }        
    
    public boolean CheckFileExists(String strPath)
    {
            
        boolean boolFileExists = false;
        
        if(Files.exists(Paths.get(strPath)))
        {                    
            
            boolFileExists = true;                   
       
        }             
        
        return boolFileExists;
        
    }
    
    public boolean CheckOrderQuoteNumber(int intCallingProcess, String strCurrentOrderQuoteNumber)
    {
        // Checks the order number to see if the current number has been used before
        
        boolean boolGoodSave = true;
        
        String strPathToOrderQuoteNumbers = ""; 
        
        // intCallingProcess 1 = Order, 2 = Quote
        
        if(intCallingProcess == 1)
        {
        
            strPathToOrderQuoteNumbers = GetPathToOrderNumbersFile();
        
        }
        
        if(intCallingProcess == 2)
        { 
         
           strPathToOrderQuoteNumbers = GetPathToQuoteNumbersFile();
            
        }
        
        String strSavedNumber = ""; // Saved number to compare with the new order/quote number 
        
        if(Files.exists(Paths.get(strPathToOrderQuoteNumbers)))
        {
        
            try(BufferedReader newReader = new BufferedReader(new FileReader(strPathToOrderQuoteNumbers)))
            {         
                                         
                String line; 
                
                while((line = newReader.readLine())!= null)
                {                
            
                    strSavedNumber = line;
                     
                    if(strPathToOrderQuoteNumbers.equals(strSavedNumber))
                    {
                        
                        boolGoodSave = false;
                        
                        break;
                        
                    }
                    
                }
                                                
            }             
            catch (IOException e)
            {
          
                e.printStackTrace();
            
            }
                         
        }
        
        return boolGoodSave;
        
    }
    
    public boolean CheckRunningApplications()
    {
        //Checks to see if OUTLOOK.EXE is running
        
           
        boolean boolRunning = false;
        
        try
        {
            
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "tasklist.exe");
        
            Process process = builder.start();
                                    
            String taskList = ReadOutput(process.getInputStream());
                       
            if(taskList.contains("OUTLOOK.EXE"))
            {
                
                boolRunning = true;
                
            }
            
        }
        catch(IOException ex)
        {
            
            ex.printStackTrace();
            
        }
        
        return boolRunning;
        
    }
    
    public CompleteOrderClass LoadSavedOrderFile(String strFileName)
    {
        //Loads the completeorderclass gson object from the saved file 
        
        CompleteOrderClass savedOrder = new CompleteOrderClass();
        
        Gson gson = new Gson();
        
        try(Reader newJsonReader = new FileReader(strFileName))
        {
           
             savedOrder = gson.fromJson(newJsonReader, CompleteOrderClass.class);
        
        }        
        catch(IOException e)
        {
            
            e.printStackTrace();
            
        }
        
        return savedOrder;
        
    }
         
    public LinkedList<CustomerAccountClass.CustomerAccountDetails> LoadCustomerDatabase()
    {
        //Loads the customers from the file into the array list       
              
        LinkedList<CustomerAccountClass.CustomerAccountDetails> newCustomerList = new LinkedList();
                       
        String strCustomerDatabaseFilePath = GetCustomerDatabaseFile();
      
        CustomerAccountClass customerClass = new CustomerAccountClass();
        
        CustomerAccountClass.CustomerAccountDetails customer;
        
        if(Files.exists(Paths.get(strCustomerDatabaseFilePath)))
        {
            
            try
            {              
                
                // JDOM2
                
                DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();            
               
                DocumentBuilder docBuilder = docBuildFact.newDocumentBuilder();            
                
                org.w3c.dom.Document doc = docBuilder.parse(strCustomerDatabaseFilePath);        
            
                doc.getDocumentElement().normalize();            

                NodeList nodeList = doc.getElementsByTagName("CustomerAccount");
                      
                int intNodeLength = nodeList.getLength();
            
                for(int i = 0; i < nodeList.getLength(); i++)
                {                
                
                    customer = customerClass.new CustomerAccountDetails();
                    
                    Node currentNode = nodeList.item(i);
              
                    if(currentNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                    
                        org.w3c.dom.Element element = (org.w3c.dom.Element) currentNode;
                                                              
                        customer.CustomerAccountNumber = element.getElementsByTagName("AccountNumber").item(0).getTextContent();
                        customer.CustomerName = element.getElementsByTagName("CustomerName").item(0).getTextContent();
                        customer.CustomerAddressLine1 = element.getElementsByTagName("AddressLine1").item(0).getTextContent();
                        customer.CustomerAddressLine2 = element.getElementsByTagName("AddressLine2").item(0).getTextContent();
                        customer.CustomerCity = element.getElementsByTagName("City").item(0).getTextContent();
                        customer.CustomerProvince = element.getElementsByTagName("Province").item(0).getTextContent();
                        customer.CustomerPostalCode = element.getElementsByTagName("PostalCode").item(0).getTextContent();
                        customer.CustomerAttention = element.getElementsByTagName("Attention").item(0).getTextContent();
                        customer.CustomerPhone = element.getElementsByTagName("Phone").item(0).getTextContent();
                        customer.CustomerFax = element.getElementsByTagName("Fax").item(0).getTextContent();
                        customer.CustomerEmail = element.getElementsByTagName("Email").item(0).getTextContent();
                                                   
                        newCustomerList.add(customer);
                    
                    }
                
                }     
            
            }
            catch(Exception ex)
            {
            
                ex.printStackTrace();
                        
            }   
        
        }
        else
        {
            
            newCustomerList = null;
            
            
        }
        
        return newCustomerList;
        
    }
        
    public LinkedList<ProductsClass.ProductDetails> LoadProductDatabase()
    {
             
        // Loads the products database into a list
        
        LinkedList<ProductsClass.ProductDetails> currentProductsList = new LinkedList();
        
        String strProductDatabaseFileName = GetProductsDatabaseFileName();
        
        ProductsClass newProductsClass = new ProductsClass();
        
        ProductsClass.ProductDetails products;
        
        //= newProductsClass.new ProductDetails();
        
        if(Files.exists(Paths.get(strProductDatabaseFileName)))
        {
            
            try
            {
            
                // JDOM2
                
                DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();            
                           
                DocumentBuilder docBuilder = docBuildFact.newDocumentBuilder();                           
            
                org.w3c.dom.Document doc = docBuilder.parse(strProductDatabaseFileName);       
                        
                doc.getDocumentElement().normalize();            

                NodeList nodeList = doc.getElementsByTagName("Product");
                      
                int intNodeLength = nodeList.getLength();
            
                for(int i = 0; i < nodeList.getLength(); i++)
                {                
                
                    products = newProductsClass.new ProductDetails();
                    
                    Node currentNode = nodeList.item(i);
              
                    if(currentNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                    
                        org.w3c.dom.Element element = (org.w3c.dom.Element) currentNode;
                                                            
                        products.VendorNumber = element.getElementsByTagName("VendorNumber").item(0).getTextContent();
                        products.VendorName = element.getElementsByTagName("VendorName").item(0).getTextContent();
                        products.VendorPartNumber = element.getElementsByTagName("VendorPartNumber").item(0).getTextContent();
                        products.EBSPartNumber = element.getElementsByTagName("EBSPartNumber").item(0).getTextContent();
                        products.PartDescription = element.getElementsByTagName("Description").item(0).getTextContent();
                        products.PartPrice = Double.valueOf(element.getElementsByTagName("Price").item(0).getTextContent());
                                                                        
                        currentProductsList.add(products);
                    
                    }
                
                }
                
            }
            catch(Exception ex)
            {
            
                ex.printStackTrace();
                        
            }   
        
        }
        else
        {
            
            currentProductsList = null;
                        
        }        
         
        return currentProductsList;
        
    }
    
    public String GetCustomerDatabaseFile()
    {                      
              
        // Returns the path to the customer database file
        
        strPathToMyDocuments = GetMyDocumentsPath();             
                            
        strCustomerDatabaseFileName = strPathToMyDocuments + "\\CJCOrderPro\\Files\\Customers\\Customer Database.xml";
        
        return strCustomerDatabaseFileName;
        
    } 
    
    public String GetExcelTemplate()
    {
        
        // Returns the path to the Excel template
        
        strPathToMyDocuments = GetMyDocumentsPath(); 
        
        String strPath = strPathToMyDocuments + "\\CJCOrderPro\\Files\\Bin\\Excel Template\\Excel Template.xlsx";
        
        return strPath;        
        
    }     
    
    public String GetMyDocumentsPath()
    {
        
        // Returns the path to the User's MyDocuments/Documents folder
        
        strPathToMyDocuments = System.getProperty("user.home")+ "\\Documents";
        
        return strPathToMyDocuments;
        
    }
    
    public String GetSavedExcelFilesFolderPath()
    {
        
        // Returns the path to the saved Excel files
        
        strPathToMyDocuments = GetMyDocumentsPath();
        
        String strPathToExcelFolder = strPathToMyDocuments + "\\CJCOrderPro\\Files\\Saved Excel Files\\";
        
        return strPathToExcelFolder;        
        
    }    
    
    public String GetPathToMainFormBackGroundImage()
    {
        
        // Returns the path to the image to display in the form's background
        
        String path = GetMyDocumentsPath();       
            
        String strPathToBackGroundImage = path + "\\CJCOrderPro\\Files\\Images\\Cool.jpg";
      
        return strPathToBackGroundImage;
        
    }     
    
    public String GetPathToPrintScreenBackGroundImage()
    {
        
        // Returns the path to the image to display in the form's background
        
        String pathToMyDocuments = GetMyDocumentsPath();
        
        String strPathToBackGroundImage = pathToMyDocuments + "\\CJCOrderPro\\Files\\Images\\Print Screen.jpg";
        
        return strPathToBackGroundImage;
        
    }      
   
    public String GetPathToOrderNumbersFile()
    {        
       // Returns the path to the order numbers file
                
        String pathToMyDocuments = GetMyDocumentsPath();
        
        String strPathToOrderNumbersFile = pathToMyDocuments + "\\CJCOrderPro\\Files\\Bin\\Order Numbers\\Order Numbers.dat";
        
        return strPathToOrderNumbersFile;
                
    }
    
    public String GetPathToQuoteNumbersFile()
    {        
        // Returns the path to the quote numbers file
        
        String pathToMyDocuments = GetMyDocumentsPath();
        
        String strPathToQuoteNumbersFile = pathToMyDocuments + "\\CJCOrderPro\\Files\\Bin\\Quote Numbers\\Quote Numbers.dat";
                                                       
        return strPathToQuoteNumbersFile;
        
    }
    
    public String GetProductsDatabaseFileName()
    {
                
       // Returns the path to the products database file
        
        strPathToMyDocuments = GetMyDocumentsPath();
        
        strProductsDatabaseFileName = strPathToMyDocuments + "\\CJCOrderPro\\Files\\Price List\\2017 Price List.xml";
                
        return strProductsDatabaseFileName;
                
    }
     
     public String GetSaveFileLocation()
    {
        // Gets the file directory to save the order file to        
                
        strPathToMyDocuments = GetMyDocumentsPath();
        
        String strSavedFileLocation = strPathToMyDocuments + "\\CJCOrderPro\\Files\\Saved Orders";
                
        return strSavedFileLocation;
                
    }
     
    public String GetSaveFileName(int intCallingProcessNumber)            
    {        
        // Gets the name of the file to save  
        
        // intCallingProcess values: 1 = save json file, 2 = save pdf file
        
        String strDirectory = "";
        String strNewFileName = "";
        FileNameExtensionFilter fileFilter;
        JFileChooser fileChooser = new JFileChooser();
        
        if(intCallingProcessNumber == 1)
        {
        
            strDirectory = GetSaveFileLocation();
            fileFilter = new FileNameExtensionFilter("Json Files", "json");
            fileChooser.addChoosableFileFilter(fileFilter);
        }
       
        if(intCallingProcessNumber == 2)
        {
        
            strDirectory = GetSavedPDFLocation();
            fileFilter = new FileNameExtensionFilter("PDF Documents", "pdf");
            fileChooser.addChoosableFileFilter(fileFilter);
        
        }        
              
        if(strDirectory != null && !strDirectory.isEmpty())
        {
            
            Path filePath = Paths.get(strDirectory);
        
            fileChooser.setCurrentDirectory(new File(strDirectory));
        
            int intReturnVal = fileChooser.showSaveDialog(null);                                     
        
            if(intReturnVal == 0)
            {
        
                strNewFileName = fileChooser.getSelectedFile().toString();
                
                if(intCallingProcessNumber == 1)
                {
                
                    if(!strNewFileName.contains(".json"))
                    {
         
                    
                        strNewFileName += ".json";
        
                    }
                    
                }
                
                if(intCallingProcessNumber == 2)
                {
                    
                    if(!strNewFileName.contains(".pdf"))
                    {
                             
                       strNewFileName += ".pdf";
        
                    }
                    
                    
                }              
                
            }
            else
            {   
            
            
                strNewFileName = "";
                
            }
            
        }
        
        return strNewFileName;
        
    }       
    
    public String GetOrderHistoryFilePath()
    {
        // Returns the path to the order history file    
        
        String strPathToOrderHistoryFile = "";
        
        strPathToMyDocuments = GetMyDocumentsPath();
        
        strPathToOrderHistoryFile = strPathToMyDocuments + "\\CJCOrderPro\\Files\\Order History\\Order History.xml";
                
        return strPathToOrderHistoryFile;
                        
    }   
   
    public String GetOutlookPath()
    {
        // Gets the users Outlook file path
        
        String strPathToOutlook = "";
        String strPathToTest = "";
        String strLatestVersion = "";        
        
        boolean boolContinueSearch = false;
        
        List<String> OutlookLocations = new ArrayList();
             
        //Outlook 2003
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\Office11\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office\\Office11\\OUTLOOK.EXE");
        
        //Outlook 2007
        
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\OFFICE12\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office\\Office12\\OUTLOOK.EXE");
        
        //Outlook 2010
        
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\Office14\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office\\Office14\\OUTLOOK.EXE");   
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office 14\\ClientX64\\Root\\Office14\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\Office14\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\root\\Office14\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office 14\\root\\Office14\\OUTLOOK.EXE");     
        OutlookLocations.add("C:\\Program Files\\Microsoft Office 14\\ClientX86\\Root\\Office14\\OUTLOOK.EXE");
                       
        //Outlook 2013
        
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\Office15\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office\\Office15\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office 15\\Root\\Office15\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office 15\\root\\Office15\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office 15\\ClientX86\\Root\\Office15\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office 15\\ClientX64\\Root\\Office15\\OUTLOOK.EXE");
      
        //Outlook 2016
    
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\root\\Office16\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\root\\Office16\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office 16\\root\\Office16\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files\\Microsoft Office 16\\ClientX86\\Root\\Office16\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office 16\\ClientX64\\Root\\Office16\\OUTLOOK.EXE");
        
        /*
        
        //Outlook 365
        OutlookLocations.add("C:\\Program Files\\Microsoft Office\\root\\Office16\\OUTLOOK.EXE");
        OutlookLocations.add("C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\OUTLOOK.EXE");
        */
        
        List<String> foundOutlookVersions = new ArrayList();
        
        for(int i = 0; i < OutlookLocations.size(); i++)
        {
            
            strPathToTest = OutlookLocations.get(i);
            
            Path path = Paths.get(strPathToTest);
            
            if(Files.exists(path))
            {
                
                foundOutlookVersions.add(strPathToTest);
                                  
            }
            else
            {
                
                strPathToOutlook = "";
                
            }
            
        }
        
        // Search the found versions for the latest one
        // Outlook 2016
        for (int i = 0; i < foundOutlookVersions.size(); i++)
        {
                        
            strLatestVersion = foundOutlookVersions.get(i);
        
            if(strLatestVersion.contains("16"))
            {
                
                strPathToOutlook = strLatestVersion;
                                
            }
            else
            {
                
                boolContinueSearch = true;
                
            }
            
        }
        
        // Outlook 2013
        if(boolContinueSearch)        
        {
            
            for (int i = 0; i < foundOutlookVersions.size(); i++)
            {                       
            
                strLatestVersion = foundOutlookVersions.get(i);
        
                if(strLatestVersion.contains("15"))
                {
                
                    strPathToOutlook = strLatestVersion;
                    boolContinueSearch = false;      
                    
                }            
                else
                {
                
                    boolContinueSearch = true;
                
                }
            
            }
            
        }
        
        // Outlook 2010
        
        if(boolContinueSearch)               
        {
            for (int i = 0; i < foundOutlookVersions.size(); i++)
            {   
                        
                strLatestVersion = foundOutlookVersions.get(i);
        
                if(strLatestVersion.contains("14"))
                {
                
                    strPathToOutlook = strLatestVersion;
                    boolContinueSearch = false;        
                
                }
                else
                {
                
                    boolContinueSearch = true;
                
                }
                
            }
            
        }   
        
        // Older versions        
        if(boolContinueSearch)
        {
             for (int i = 0; i < foundOutlookVersions.size(); i++)
            {   
                        
                strLatestVersion = foundOutlookVersions.get(i);
        
                if(strLatestVersion.contains("13") || strLatestVersion.contains("12") || strLatestVersion.contains("11"))
                {
                
                    strPathToOutlook = strLatestVersion;
                      
                
                }
                                
            }
                        
        }
        
       // int intEndOfFolder = strPathToOutlook.indexOf("OUTLOOK.EXE");
        //int intTotalLength = strPathToOutlook.length();
        //String editedPath = strPathToOutlook.substring(0, intEndOfFolder); 
        
        return strPathToOutlook;
        
    }          
    
    public String GetQuoteHistoryFilePath()
    {        
        // Returns the path to the quotes history file       
        
        String strPathToQuoteHistoryFile = "";
        
        strPathToMyDocuments = GetMyDocumentsPath();
        
        strPathToQuoteHistoryFile = strPathToMyDocuments + "\\CJCOrderPro\\Files\\Quote History\\Quote History.xml";
                
        return strPathToQuoteHistoryFile;
                        
    }    
                
    public String GetSavedPDFLocation()
    {        
        // Returns the path to the saved PDF files
        
        String strPathToMyDocuments = GetMyDocumentsPath();
        
        String strPDFFolderLocation = GetMyDocumentsPath() + "\\CJCOrderPro\\Files\\Saved PDF's";
        
        return strPDFFolderLocation;
        
    }
    
    public String GetTempPDFFileName()
    {        
        // Returns the path to temp pdf file for printing purposes
        
        String strPathToPDFLocation = GetSavedPDFLocation();
        
        String strPathToTempPDFFile = strPathToPDFLocation + "\\TempPDFToPrint.pdf";        
        
        return strPathToTempPDFFile;        
        
    }
    
    public String GetTempSaveFileName()
    {
        // Creates a temporary file to save completed order 
        
        String strTempFile = GetSaveFileLocation() + "\\Standard Order.json";
        
        return strTempFile;
        
    }
    
    public String GetUserName()
    {
        // Returns the current user name
        
        int intStartOfName = 9;
        
        String strPathToMyDocuments = GetMyDocumentsPath();

        String strStart = strPathToMyDocuments.substring(intStartOfName);
                        
        int intEndOfName = strStart.indexOf("\\") + intStartOfName;
        
        String strUserName = strPathToMyDocuments.substring(intStartOfName, intEndOfName);
                
        return strUserName;
        
    }
    
    public String GetUserProfileFolder()   
    {
        // Returns the user profile folder
        
        String strPathToMyDocuments = GetMyDocumentsPath();
        
        String strUserProfile = strPathToMyDocuments + "\\CJCOrderPro\\Files\\User Profile";
        
        return strUserProfile;
        
    }
    
     public String GetUserProfileFileName()   
    {
        // Returns the user profile filename
        
        String strPathToUserFile = GetUserProfileFolder();
        
        String strUserProfileFile = strPathToUserFile + "\\Profile.dat";
                       
        return strUserProfileFile;
        
    }
    
    private String ReadOutput(InputStream taskListStream)
    {       
        // Returns the scanned output for the xml file
        
        Scanner scanner = new Scanner(taskListStream, "UTF-8").useDelimiter("\\A");
        
        String output = scanner.hasNext() ? scanner.next() : "";
        
        scanner.close();
        
        return output;
        
    }     
     
    public void CreateUserProfile(String[] stringToSave)
    {        
        // Creates the user profile file
        
        String strSaveFileName = GetUserProfileFileName();
        
        String strItem = "";
        
        try
        {           
                                 
            FileWriter writer = new FileWriter(strSaveFileName);
            
            for (int i = 0; i < stringToSave.length; i++)
            {
            
                strItem = stringToSave[i];
                
                writer.write(strItem + System.lineSeparator());
        
            }
            
            writer.close();
            
        }        
        catch(IOException ex)
        {
            
            ex.printStackTrace();          
            //JOptionPane.showMessageDialog(null, ex);
            
        }
        
    }
    
    public void DeleteFile(File strPath)    
    {
        // Deletes the selected file
        
        File newFile = strPath;
        
        try 
        {     
        
            if(strPath.exists())
            {
                
                newFile.delete();
                
            }
            
        }
        catch (Exception e)
        {
    
            System.err.format("%s: no such" + " file or directory%n", strPath);
            
        } 
       
    }
    
    public void DeleteFile(String strPath)
    {        
        // Deletes the specified file
        
        try
        {
            if(Files.exists(Paths.get(strPath)))
            {
                
                Files.delete(Paths.get(strPath));                            
                
            }                       
                        
        }
        catch(IOException ex)
        {
            
            ex.printStackTrace();
            
            //JOptionPane.showMessageDialog(null, ex);
            
        }        
        
    }
    
    public void WriteCustomerListToFile(LinkedList<CustomerAccountClass.CustomerAccountDetails> listOfCustomers)
    {    
        // Writes the customers to the customer database file                      
              
        String strCustomerDatabaseFileName = GetCustomerDatabaseFile();
        
        // Delete existing customer file             
        DeleteFile(strCustomerDatabaseFileName);            
                    
                
        try
        {        
                       
                FileWriter fileWriter = new FileWriter(strCustomerDatabaseFileName);
                
                Document doc = new Document();   
                
                Element customers =  new Element("CustomerDatabase");
                
                doc.setRootElement(customers);                                                      
                                     
                for (int i = 0; i < listOfCustomers.size(); i++)
                {
                
                    Element customer = new Element("CustomerAccount");   
                    
                    customer.addContent(new Element("AccountNumber").setText(listOfCustomers.get(i).CustomerAccountNumber));
                    customer.addContent(new Element("CustomerName").setText(listOfCustomers.get(i).CustomerName));
                    customer.addContent(new Element("AddressLine1").setText(listOfCustomers.get(i).CustomerAddressLine1));
                    customer.addContent(new Element("AddressLine2").setText(listOfCustomers.get(i).CustomerAddressLine2));
                    customer.addContent(new Element("City").setText(listOfCustomers.get(i).CustomerCity));
                    customer.addContent(new Element("Province").setText(listOfCustomers.get(i).CustomerProvince));   
                    customer.addContent(new Element("PostalCode").setText(listOfCustomers.get(i).CustomerPostalCode));
                    customer.addContent(new Element("Attention").setText(listOfCustomers.get(i).CustomerAttention));
                    customer.addContent(new Element("Phone").setText(listOfCustomers.get(i).CustomerPhone));
                    customer.addContent(new Element("Fax").setText(listOfCustomers.get(i).CustomerFax));
                    customer.addContent(new Element("Email").setText(listOfCustomers.get(i).CustomerEmail));                          
            
                    doc.getRootElement().addContent(customer);
            
                }
                                             
                
                XMLOutputter xmlOutput = new XMLOutputter();
            
                xmlOutput.setFormat(Format.getPrettyFormat());                                                                                
                       
                xmlOutput.output(doc, fileWriter);
            
            //}
            
        }
        catch(IOException ex)
        {
            
            ex.printStackTrace();
            
            //JOptionPane.showMessageDialog(null, ex);
            
        }
     
    }
     
}
