package controllers;

import com.google.inject.Inject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileIOController {
    private static final String DEFAULT_SETTINGS_FILE_NAME = "settings.bin";
    private static final String DEFAULT_CONNECTION_STRING = "jdbc:mariadb://localhost/esoft_projeto?createDatabaseIfNotExist=true&user=root&password=password";
    private String connectionString;

    @Inject
    private ViewController viewController;

    public FileIOController() {
        try (InputStream inputStream = new FileInputStream(DEFAULT_SETTINGS_FILE_NAME)) {
            connectionString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException ex) {
            saveConnectionString(DEFAULT_CONNECTION_STRING);
        } catch (IOException ex) {
            ex.printStackTrace();
            connectionString = DEFAULT_CONNECTION_STRING;
        }
    }

    public void saveConnectionString(String connectionString) {
        this.connectionString = connectionString;

        File file = new File(DEFAULT_SETTINGS_FILE_NAME);
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(connectionString.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            viewController.mostrarAviso("Falha ao guardar no ficheiro! " + ex.getMessage());
        }
    }

    public String connectionString() {
        return connectionString;
    }
}
