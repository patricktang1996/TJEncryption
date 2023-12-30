Development Environment:
1.	IDE: IntelliJ IDEA with JavaFX Scene Builder
2.	SDK: Oracle OpenJDK version 20.0.2
3.	Language level: SKD default
4.	External library: 
 ![image](https://github.com/patricktang1996/TJEncryption/assets/125730507/618a8062-1fa8-4310-85df-a957fa163ea5)
5.	Cloud: AWS
6.	Database: AWS RDS(use MySQL-Workbench to manage connection)


 
Functionalities:
1.	Login:
 1.1 switch to “Create a new account” stage；
 1.2 Error message；
 1.3	Successful login and switch to dashboard；
 1.4	Connect to database(AWS RDS)；
  
2.	Create new users:
 2.1 Create a user successfully；
  2.1.1 connect to database(AWS RDS)；
  2.1.2 User account and passwords in DB is encrypted；
 2.2 Error message (Check whether the account already exist or not)；
 2.3	Switch to login stage；

3.	Use choiceBox to choose encrypt/decrypt algorithm:
 When switching algorithms, the contents of key and result will be automatically cleared.
  
4.	Import text from a file:
 Currently, only the content in TXT format documents can be read.
  
5.	Generate a new key:
 After selecting the encryption/decryption algorithm, click the “NEW KEY” button to generate the corresponding key.
  
6.	Encrypt and export the key to a file: 
 If you save AES/DES key, your key will be encrypted and saved as an encrypted key file. 
 If you save Caesar cipher key, your key will be encrypted(using AES) and saved as a txt file.
 
7.	Load and decrypt a key from a file:
  
8.	Encryption: 
 3 different algorithms: Caesar cipher, AES and DES
  
9.	Decryption:
 3 different algorithms: Caesar cipher, AES and DES
  
10.	Export the encrypted/decrypted result to text file:
  
11.	Store data to DB:
 The key will be encrypted before being stored in DB.











