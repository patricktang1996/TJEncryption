package com.example.tjencryption;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

public class DashBoard {
    @FXML
    private TextField text;
    @FXML
    private TextField key;
    @FXML
    private TextArea result;
    @FXML
    private Label message;
    @FXML
    private ChoiceBox<String> choice;

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private SecretKey secretkey;
    String algorithm ="";
    @FXML
    public void encrypt() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        String inputText = text.getText();
        String inputKey = key.getText();
        if(inputKey.isEmpty()  && inputText.isEmpty()){
            message.setText("Please type in or import text and key.");
        }else if(inputKey.isEmpty()){
            message.setText("Please type in or import the key.");
        }else if(inputText.isEmpty()){
            message.setText("Please type in or import the text.");
        } else{
            if (algorithm.equals("cc")){
                this.CCEncrypt(inputText,inputKey);
            } else if (algorithm.equals("AES")) {
                this.AESEncrypt(inputText);
            } else if(algorithm.equals("DES")){
                this.DESEncrypt(inputText);
            }
        }

    }
    @FXML
    public void decrypt() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        String inputText = text.getText();
        String inputKey = key.getText();
        if(inputKey.isEmpty() && inputText.isEmpty()){
            message.setText("Please type in or import text and key.");
        }else if(inputKey.isEmpty()){
            message.setText("Please type in or import the key.");
        }else if(inputText.isEmpty()){
            message.setText("Please type in or import the text.");
        } else{
            if (algorithm.equals("cc")){
                this.CCDecrypt(inputText,inputKey);
            } else if (algorithm.equals("AES")) {
                this.AESDecrypt(inputText);
            } else if(algorithm.equals("DES")){
                this.DESDecrypt(inputText);
            }
        }
    }
    @FXML
    public void importText(){
        String importText ="";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select the file to import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
                message.setText("Import successful！");
                importText = content.toString().trim();
            } catch (IOException e) {
                message.setText("Import failure：" + e.getMessage());
            }
        } else {
            System.out.println("User cancels import.");
        }
        text.setText(importText);
    }
    @FXML
    public void importKey(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Encrypted File to Import");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String encryptedContent = reader.readLine();
                String decryptedContent = keyDecryptAES(encryptedContent);
                key.setText(decryptedContent);
                message.setText("Import successful!");
            } catch (Exception e) {
                message.setText("Import failure: " + e.getMessage());
            }
        } else {
            message.setText("User cancels import.");
        }
    }

    public String keyDecryptAES(String encryptedContent) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, generateSecretKey("MySecretPassword"));

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void exportTextAndResultToFile(){
        String textContent = "Text: "+text.getText()+ "\n";
        String resultContent = "Result: "+result.getText()+ "\n";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("please choose the address");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textContent);
                writer.write(resultContent);
                message.setText("Export successful！");
            } catch (IOException e) {
                message.setText("Export failure!" + e.getMessage());
            }
        } else {
            message.setText("User cancels export.");
        }
    }
    @FXML
    public void exportKeyToFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Encrypted Key File");
        if(algorithm.equals("cc")){
            String keyContent = key.getText();
            String encryptedContent = keyEncryptAES(keyContent);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(encryptedContent);
                    message.setText("Export to file and DB successfully!");
                } catch (IOException e) {
                    message.setText("Export failure!" + e.getMessage());
                }
            } else {
                message.setText("User cancels export.");
            }
        } else if (algorithm.equals("AES") || algorithm.equals("DES")) {
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Encrypted Key Files (*.encrypted)", "*.encrypted");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                try {
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey("MySecretPassword"));
                    byte[] encryptedKey = cipher.doFinal(this.secretkey.getEncoded());
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                        oos.writeObject(encryptedKey);
                        message.setText("Encrypted Key export successful!");
                    }
                } catch (Exception e) {
                    message.setText("Encrypted Key export failure: " + e.getMessage());
                }
            } else {
                message.setText("User canceled encrypted key export.");
            }
        } else{
            message.setText("Please select an algorithm before export key.");
        }
    }
    public String keyEncryptAES(String keyContent) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey("MySecretPassword"));
            byte[] encryptedBytes = cipher.doFinal(keyContent.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @FXML
    public void loadKey(){
        if(algorithm.equals("cc")){
            this.importKey();
        } else if (algorithm.equals("AES") || algorithm.equals("DES")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Encrypted Key File");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Encrypted Key Files (*.encrypted)", "*.encrypted");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                try {
                    byte[] encryptedKey;
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        encryptedKey = (byte[]) ois.readObject();
                    }
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, generateSecretKey("MySecretPassword")); // Use the same encryption key used for encryption
                    byte[] decryptedKeyBytes = cipher.doFinal(encryptedKey);
                    this.secretkey = new SecretKeySpec(decryptedKeyBytes, "AES");
                    key.setText(Base64.getEncoder().encodeToString(secretkey.getEncoded()));
                    message.setText("Encrypted Key import and decryption successful!");
                } catch (Exception e) {
                    message.setText("Encrypted Key import and decryption failure: " + e.getMessage());
                }
            } else {
                message.setText("User canceled encrypted key import.");
            }
        } else{
            message.setText("Please select an algorithm before import key.");
        }
    }

    public SecretKey generateSecretKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "AES");
    }
    @FXML
    public void algorithmChoose() {
        String selectedAlgorithm = choice.getValue();
        if (selectedAlgorithm.equals("Caesar cipher")){
            algorithm = "cc";
            key.setText("");
            result.setText("");
            message.setText("Caesar cipher: only encrypt and decrypt plain alphabetic text.");

        } else if (selectedAlgorithm.equals("AES")) {
            key.setText("");
            result.setText("");
            algorithm = "AES";
            message.setText("AES: Click a button to generate or import a key.");
        } else if (selectedAlgorithm.equals("DES")) {
            key.setText("");
            result.setText("");
            algorithm = "DES";
            message.setText("DES: Click a button to generate or import a key..");
        }
    }
    @FXML
    public void generateKey() throws NoSuchAlgorithmException {
        if(algorithm.equals("AES") || algorithm.equals("DES")){
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            this.setSecretkey(keyGen.generateKey());
            String keyString = Base64.getEncoder().encodeToString(secretkey.getEncoded());
            key.setText(keyString);
            message.setText("A new key has been generated for you.");
        } else if (algorithm.equals("cc")) {
            Random random = new Random();
            key.setText(String.valueOf(random.nextInt(1,26)));
        } else {
            message.setText("Please select an algorithm before generate a key.");
        }

    }
    @FXML
    public void AllToDB() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String JDBC_URL = "jdbc:mysql://database-1.ch0uqgjn65pq.us-east-1.rds.amazonaws.com:3306/mswdev2023cloudandsecurity";
        String USERNAME = "admin";
        String PASSWORD = "adminadmin";
        String encryptKey ="";
        String textContent= text.getText();
        String resultContent =result.getText();
        if (algorithm.equals("cc")){
            encryptKey = this.keyEncryptAES(key.getText());
        } else if (algorithm.equals("AES")||algorithm.equals("DES")) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey("MySecretPassword"));
            byte[] encryptedKey = cipher.doFinal(this.secretkey.getEncoded());
            encryptKey = Base64.getEncoder().encodeToString(encryptedKey);
        }
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO `allData` (`text`, `key`, `result`) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, textContent);
                insertStatement.setString(2, encryptKey);
                insertStatement.setString(3, resultContent);
                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    message.setText("Successfully store all data to DB");
                } else {
                    message.setText("Failed to store all data to DB");
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void DESEncrypt(String inputText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] byteDataToEncrypt = inputText.getBytes();
        byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);
        result.setText(Base64.getEncoder().encodeToString(byteCipherText));
        message.setText("Successful encryption");
    }
    public void DESDecrypt(String inputText) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedBytes = Base64.getDecoder().decode(inputText);
        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
        byte[] byteDecryptedText = desCipher.doFinal(encryptedBytes);
        result.setText(new String(byteDecryptedText)) ;
        message.setText("Successful decryption");
    }
    public void AESEncrypt(String inputText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher desCipher = Cipher.getInstance("AES");
        desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] byteDataToEncrypt = inputText.getBytes();
        byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);
        result.setText(Base64.getEncoder().encodeToString(byteCipherText));
        message.setText("Successful encryption");
    }
    public void AESDecrypt(String inputText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedBytes = Base64.getDecoder().decode(inputText);
        Cipher desCipher = Cipher.getInstance("AES");
        desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
        byte[] byteDecryptedText = desCipher.doFinal(encryptedBytes);
        result.setText(new String(byteDecryptedText)) ;
        message.setText("Successful decryption");
    }
    public void CCEncrypt(String inputText, String inputKey){
        inputText =inputText.toLowerCase();
        String encryptString ="";
        try {
            int shiftKey = Integer.parseInt(inputKey);
            for (int i = 0; i < inputText.length(); i++) {
                int position = ALPHABET.indexOf(inputText.charAt(i));
                int encryptPosition = (shiftKey + position) % 26;
                char encryptChar = ALPHABET.charAt(encryptPosition);
                encryptString = encryptString + encryptChar;
            }
            result.setText(encryptString);
            message.setText("Successful encryption");
        } catch (NumberFormatException e) {
            message.setText("Please enter a numeric key.");
        }
    }
    public void CCDecrypt(String inputText, String inputKey){
        inputText =inputText.toLowerCase();
        String decryptString ="";
        try {
            int shiftKey = Integer.parseInt(inputKey);
            for (int i = 0; i < inputText.length(); i++) {
                int position = ALPHABET.indexOf(inputText.charAt(i));
                int decryptPosition = (position-shiftKey+26)%26;
                char decryptChar = ALPHABET.charAt(decryptPosition);
                decryptString = decryptString + decryptChar;
            }
            result.setText(decryptString);
            message.setText("Successful decryption");
        } catch (NumberFormatException e) {
            message.setText("Please enter a numeric key.");
        }
    }
    public SecretKey getSecretkey() {
        return secretkey;
    }
    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }


}
