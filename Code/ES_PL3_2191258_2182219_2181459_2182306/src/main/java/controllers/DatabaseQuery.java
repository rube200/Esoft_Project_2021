package controllers;

import API.DatabaseConnector;
import com.google.inject.Inject;
import model.Evento;
import model.Prova;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseQuery implements DatabaseConnector {
    private final String connectionString;
    private final Logger logger;

    @Inject
    public DatabaseQuery(Logger logger) {
        connectionString = "jdbc:mariadb://localhost/esoft_projeto?createDatabaseIfNotExist=true&user=root&password=12345";
        this.logger = logger;

        checkSchema();
    }

    private void checkSchema() {
        createStatement(statement -> {
            String query = "CREATE TABLE IF NOT EXISTS `eventos` (" +
                    "`Id` INT AUTO_INCREMENT, " +
                    "`Nome` VARCHAR(50) NOT NULL, " +
                    "`Inicio` DATE NOT NULL, " +
                    "`Fim` DATE NOT NULL, " +
                    "`Pais` VARCHAR(75) NOT NULL, " +
                    "`Local` VARCHAR(75) NOT NULL, " +
                    "`Deleted_At` TIMESTAMP NULL DEFAULT NULL, " +
                    "PRIMARY KEY (`Id`));";
            statement.execute(query);

            query = "CREATE TABLE IF NOT EXISTS `modalidades` (" +
                    "`Id` INT AUTO_INCREMENT, " +
                    "`Nome` VARCHAR(50) NOT NULL, " +
                    "`Tipo_De_Contagem` ENUM('S','M') NOT NULL, " +
                    "PRIMARY KEY (`Id`));";
            statement.execute(query);

            query = "CREATE TABLE IF NOT EXISTS `provas` (" +
                    "`Id` INT AUTO_INCREMENT, " +
                    "`Evento_Id` INT NOT NULL, " +
                    "`Modalidade_Id` INT NOT NULL, " +
                    "`Sexo` ENUM('M','F','X') NOT NULL, " +
                    "`Minimos` INT NOT NULL, " +
                    "`Atletas_Por_Provas` TINYINT(1) NOT NULL DEFAULT 8, " +
                    "`Deleted_At` TIMESTAMP NULL DEFAULT NULL, " +
                    "PRIMARY KEY (`Id`), " +
                    "KEY `Eventos_Provas` (`Evento_Id`), " +
                    "KEY `Modalidades_Provas` (`Modalidade_Id`), " +
                    "CONSTRAINT `Eventos_Provas` FOREIGN KEY (`Evento_Id`) REFERENCES `eventos` (`Id`) ON UPDATE CASCADE, " +
                    "CONSTRAINT `Modalidades_Provas` FOREIGN KEY (`Modalidade_Id`) REFERENCES `modalidades` (`Id`) ON UPDATE CASCADE);";
            statement.execute(query);

            return true;
        });
    }

    @Override
    public Collection<Evento> getEventos() {
        String query = "SELECT * " +
                "FROM `eventos` " +
                "WHERE `Deleted_At` IS NULL;";

        Collection<Evento> eventos = new ArrayList<>();
        boolean success = executeQuery(query, result -> {
            Collection<Evento> data = getDataFromResult(Evento.class, result);
            eventos.addAll(data);
            return true;
        });

        return success ? eventos : null;
    }

    @Override
    public Collection<Prova> getProvas() {
        String query = "SELECT * " +
                "FROM `provas` " +
                "WHERE `Deleted_At` IS NULL;";

        Collection<Prova> provas = new ArrayList<>();
        boolean success = executeQuery(query, result -> {
            Collection<Prova> data = getDataFromResult(Prova.class, result);
            provas.addAll(data);
            return true;
        });

        return success ? provas : null;
    }

    private <T> Collection<T> getDataFromResult(Class<T> implementation, ResultSet resultSet) throws ReflectiveOperationException, SQLException {
        ResultSetMetaData sqlMetaData = resultSet.getMetaData();

        Map<String, String> sqlColNameLabel = new HashMap<>();
        Map<String, String> sqlColNameOrig = new HashMap<>();

        int colCount = sqlMetaData.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            String originalName = sqlMetaData.getColumnName(i);
            //OriginalName precisa de ser processado pois em Java os campos não usam underscore por conveniência
            String name = convertSqlNamingToJava(originalName);

            String lowerName = name.toLowerCase();
            String label = sqlMetaData.getColumnLabel(i);

            sqlColNameLabel.put(lowerName, label);
            sqlColNameOrig.put(lowerName, name);
        }

        Field[] declaredFields = implementation.getDeclaredFields();
        Map<String, Field> fieldsToFill = new HashMap<>();

        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String fieldNameLower = fieldName.toLowerCase();

            //Não existe entrada para este field na base de dados
            if (!sqlColNameLabel.containsKey(fieldNameLower))
                continue;

            field.setAccessible(true);

            //Verifica se o field ainda não foi registado | (Insensitive case)
            if (!fieldsToFill.containsKey(fieldNameLower))
            {
                fieldsToFill.put(fieldNameLower, field);
                continue;
            }

            //A verificações anteriores foram feitas em lower case, agora será um match case
            if (!sqlColNameOrig.get(fieldNameLower).equals(fieldName))
                continue;

            fieldsToFill.put(fieldNameLower, field);
        }

        Collection<T> data = new ArrayList<>();
        while (resultSet.next()) {
            T obj = implementation.getDeclaredConstructor().newInstance();

            for (Map.Entry<String, Field> entry : fieldsToFill.entrySet()) {
                String fieldName = entry.getKey();
                String label = sqlColNameLabel.get(fieldName);

                Field field = entry.getValue();
                Class<?> fieldType = field.getType();
                Object value = resultSet.getObject(label);

                if (fieldType.isEnum())
                {
                    //Tristemente em Java é impossível converter estas comparações para um switch
                    if (fieldType == API.Genero.class)
                    {
                        value = API.Genero.valueOf(value.toString());//String.valueOf(Object obj) => obj != null ? obj.toString() ...
                    }
                    else if (fieldType == API.Sexo.class)
                    {
                        value = API.Sexo.valueOf(value.toString());//String.valueOf(Object obj) => obj != null ? obj.toString() ...
                    }
                    else
                    {
                        throw new IllegalArgumentException("Unknown Enum type: " + fieldType);
                    }
                }

                field.set(obj, value);
            }

            data.add(obj);
        }

        return data;
    }

    private String convertSqlNamingToJava(String originalName)
    {
        boolean capitalizeNext = false;
        StringBuilder stringBuilder = new StringBuilder(originalName);

        for (int i = 0; i < stringBuilder.length(); i++) {
            char currentChar = stringBuilder.charAt(i);
            if (currentChar == '_') {
                stringBuilder.deleteCharAt(i);
                capitalizeNext = true;
                continue;
            }

            if (!capitalizeNext)
                continue;

            stringBuilder.setCharAt(i, Character.toUpperCase(currentChar));
        }

        return stringBuilder.toString();
    }

    private boolean createStatement(SqlConsumer<Statement> statementCallback)
    {
        return createStatement(statementCallback, null);
    }

    private boolean createStatement(SqlConsumer<Statement> statementCallback, Consumer<Exception> exceptionCallback)
    {
        return getConnection(connection -> {
            try (Statement statement = connection.createStatement())
            {
                return statementCallback.invoke(statement);
            }
        }, exceptionCallback);
    }

    private boolean executeQuery(String query, SqlConsumer<ResultSet> result)
    {
        return executeQuery(query, null, result, null);
    }

    private boolean executeQuery(String query, SqlConsumer<PreparedStatement> preparedQuery, SqlConsumer<ResultSet> result, Consumer<Exception> exceptionCallback)
    {
        return getConnection(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query))
            {
                if (preparedQuery != null)
                {
                    if (!preparedQuery.invoke(preparedStatement))
                        return false;
                }

                try (ResultSet resultSet = preparedStatement.executeQuery())
                {
                    return result.invoke(resultSet);
                }
            }
        }, exceptionCallback);
    }

    private boolean getConnection(SqlConsumer<Connection> connectionCallback, Consumer<Exception> exceptionCallback) {
        try (Connection connection = DriverManager.getConnection(connectionString))
        {
            return connectionCallback.invoke(connection);
        }
        catch (Exception ex) {
            if (exceptionCallback != null)
                exceptionCallback.accept(ex);
            else
                logger.log(Level.WARNING, ex.getMessage(), ex);
            return false;
        }
    }

    @FunctionalInterface
    private interface SqlConsumer<T> {
        /**
         * Executa esta operação no argumento fornecido.
         *
         * @param action ação a ser executada
         * @return deverá retornar verdadeiro(true) se a operação foi concluida com sucesso, senão falso(false) | se retornar falso(false) a execução de operações deverá parar
         */
        boolean invoke(T action) throws ReflectiveOperationException, SQLException;
    }
}
