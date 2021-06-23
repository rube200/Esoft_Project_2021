package controllers;

import API.DatabaseConnector;
import model.UniqueId;
import com.google.inject.Inject;
import model.Evento;
import model.Modalidade;
import model.Prova;

import java.lang.reflect.Field;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseQuery implements DatabaseConnector {
    private final String connectionString;
    private final Logger logger;

    @Inject
    private ViewController viewController;

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
                    "PRIMARY KEY (`Id`), " +
                    "CONSTRAINT `Validate_Nome` CHECK (`Nome` <> ''), " +
                    "CONSTRAINT `Validate_Dates` CHECK (`Inicio` <= `Fim`), " +
                    "CONSTRAINT `Validate_Pais` CHECK (`Pais` <> ''), " +
                    "CONSTRAINT `Validate_Local` CHECK (`Local` <> ''));";
            statement.execute(query);

            query = "CREATE TABLE IF NOT EXISTS `modalidades` (" +
                    "`Id` INT AUTO_INCREMENT, " +
                    "`Nome` VARCHAR(50) NOT NULL, " +
                    "`Tipo_De_Contagem` ENUM('S', 'Min', 'M', 'Km') NOT NULL, " +
                    "`Deleted_At` TIMESTAMP NULL DEFAULT NULL, " +
                    "PRIMARY KEY (`Id`), " +
                    "CONSTRAINT `Validate_Name` CHECK (`Nome` <> ''));";
            statement.execute(query);

            query = "CREATE TABLE IF NOT EXISTS `provas` (" +
                    "`Id` INT AUTO_INCREMENT, " +
                    "`Evento_Id` INT NOT NULL, " +
                    "`Modalidade_Id` INT NOT NULL, " +
                    "`Sexo` ENUM('M','F','X') NOT NULL, " +
                    "`Minimos` INT NOT NULL, " +
                    "`Atletas_Por_Provas` TINYINT(1) UNSIGNED NOT NULL DEFAULT 8, " +
                    "`Deleted_At` TIMESTAMP NULL DEFAULT NULL, " +
                    "PRIMARY KEY (`Id`), " +
                    "KEY `Eventos_Provas` (`Evento_Id`), " +
                    "KEY `Modalidades_Provas` (`Modalidade_Id`), " +
                    "CONSTRAINT `Eventos_Provas` FOREIGN KEY (`Evento_Id`) REFERENCES `eventos` (`Id`) ON UPDATE CASCADE, " +
                    "CONSTRAINT `Modalidades_Provas` FOREIGN KEY (`Modalidade_Id`) REFERENCES `modalidades` (`Id`) ON UPDATE CASCADE, " +
                    "CONSTRAINT `Validate_Minimos` CHECK (`Minimos` > '0'), " +
                    "CONSTRAINT `Validate_Atletas_Por_Prova` CHECK (`Atletas_Por_Provas` > '1'));";
            statement.execute(query);

            return true;
        });
    }

    @Override
    public Collection<Evento> getEventos() {
        return getEventos(false, true);
    }

    @Override
    public Collection<Evento> getEventosAtuais() {
        return getEventos(true, false);
    }

    @Override
    public Collection<Evento> getEventoAtuaisOuFuturos() {
        return getEventos(true, true);
    }

    private Collection<Evento> getEventos(boolean decorrer, boolean futuros) {
        //language=MariaDB
        String query = "SELECT * " +
                "FROM `eventos` " +
                "WHERE " + (decorrer ?
                "`Inicio` <= UTC_DATE() AND " +
                        (!futuros ?
                                "`Fim` >= UTC_DATE() AND "
                                : "") : "") +
                "`Deleted_At` IS NULL;";


        return getDataFromQuery(Evento.class, query);
    }

    @Override
    public boolean store(Evento evento) {
        //language=MariaDB
        String query = "INSERT INTO `eventos` " +
                "(`Nome`, `Inicio`, `Fim`, `Pais`, `Local`) " +
                "VALUES (?, ?, ?, ?, ?);";

        return executeUpdate(query, prepare -> {
            prepare.setString(1, evento.getNome());
            prepare.setDate(2, new Date(evento.getInicioTime()));
            prepare.setDate(3, new Date(evento.getFimTime()));
            prepare.setString(4, evento.getPais());
            prepare.setString(5, evento.getLocal());
            return true;
        }, result -> insertModelId(result, evento), ex ->{
            if (ex instanceof SQLNonTransientConnectionException sqlEx)  {
                viewController.mostrarAviso("Falha ao guardar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }
    @Override
    public boolean update(Evento evento) {
        //language=MariaDB
        String query = "UPDATE `eventos` " +
                "SET `Nome` = ?, " +
                "`Inicio` = ?, " +
                "`Fim` = ?, " +
                "`Pais` = ?, " +
                "`Local` = ? " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepare.setString(1, evento.getNome());
            prepare.setDate(2, new Date(evento.getInicioTime()));
            prepare.setDate(3, new Date(evento.getFimTime()));
            prepare.setString(4, evento.getPais());
            prepare.setString(5, evento.getLocal());
            prepare.setInt(6, evento.getId());
            return true;
        }, ex ->{
            if (ex instanceof SQLNonTransientConnectionException sqlEx)  {
                viewController.mostrarAviso("Falha ao atualizar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    @Override
    public Collection<Prova> getProvas() {
        return getProvas(false);
    }

    @Override
    public Collection<Prova> getProvasAtuais()  {
        return getProvas(true);
    }
    private Collection<Prova> getProvas(boolean decorrer) {
        //language=MariaDB
        String query = "SELECT `provas`.* " +
                "FROM `provas` " +
                (decorrer ?
                        "INNER JOIN ( " +
                                "SELECT `Id` " +
                                "FROM `eventos` " +
                                "WHERE " +
                                "`Inicio` <= UTC_DATE() AND " +
                                "`Fim` >= UTC_DATE() AND " +
                                "`Deleted_At` IS NULL" +
                                ") `eventos` " +
                                "ON `eventos`.`Id` = `provas`.`Evento_Id` " : "") +
                "WHERE `Deleted_At` IS NULL;";

        return getDataFromQuery(Prova.class, query);
    }

    @Override
    public boolean store(Prova prova) {
        //language=MariaDB
        String query = "INSERT INTO `provas` " +
                "(`Evento_Id`, `Modalidade_Id`, `Sexo`, `Minimos`, `Atletas_Por_Provas`) " +
                "VALUES (?, ?, ?, ?, ?);";

        return executeUpdate(query, prepare -> {
            prepare.setInt(1, prova.getEventoId());
            prepare.setInt(2, prova.getModalidadeId());
            prepare.setString(3, prova.getSexo().name());
            prepare.setInt(4, prova.getMinimos());
            prepare.setByte(5, prova.getAtletasPorProva());
            return true;
        }, result -> insertModelId(result, prova), ex ->{
            if (ex instanceof SQLNonTransientConnectionException sqlEx)  {
                viewController.mostrarAviso("Falha ao guardar prova! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }
    @Override
    public boolean update(Prova prova) {
        //language=MariaDB
        String query = "UPDATE `provas` " +
                "SET `Evento_Id` = ?, " +
                "`Modalidade_Id` = ?, " +
                "`Sexo` = ?, " +
                "`Minimos` = ?, " +
                "`Atletas_Por_Provas` = ? " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepare.setInt(1, prova.getEventoId());
            prepare.setInt(2, prova.getModalidadeId());
            prepare.setString(3, prova.getSexo().name());
            prepare.setInt(4, prova.getMinimos());
            prepare.setByte(5, prova.getAtletasPorProva());
            prepare.setInt(6, prova.getId());
            return true;
        }, ex ->{
            if (ex instanceof SQLNonTransientConnectionException sqlEx)  {
                viewController.mostrarAviso("Falha ao atualizar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    @Override
    public Collection<Modalidade> getModalidades() {
        //language=MariaDB
        String query = "SELECT * " +
                "FROM `modalidades` " +
                "WHERE `Deleted_At` IS NULL;";

        return getDataFromQuery(Modalidade.class, query);
    }

    @Override
    public boolean store(Modalidade modalidade) {
        //language=MariaDB
        String query = "INSERT INTO `modalidades` " +
                "(`Nome`, `Tipo_De_Contagem`) " +
                "VALUES (?, ?);";

        return executeUpdate(query, prepare -> {
            prepare.setString(1, modalidade.getNome());
            prepare.setString(2, modalidade.getTipoDeContagem().name());
            return true;
        }, result -> insertModelId(result, modalidade), ex ->{
            if (ex instanceof SQLNonTransientConnectionException sqlEx)  {
                viewController.mostrarAviso("Falha ao guardar modalidade! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }
    @Override
    public boolean update(Modalidade modalidade) {
        //language=MariaDB
        String query = "UPDATE `modalidades` " +
                "SET `Nome` = ?, " +
                "`Tipo_De_Contagem` = ? " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepare.setString(1, modalidade.getNome());
            prepare.setString(2, modalidade.getTipoDeContagem().name());
            prepare.setInt(6, modalidade.getId());
            return true;
        }, ex ->{
            if (ex instanceof SQLNonTransientConnectionException sqlEx)  {
                viewController.mostrarAviso("Falha ao atualizar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    private <T> Collection<T> getDataFromQuery(Class<T> implementation, String sql) {
        Collection<T> dataToReturn = new ArrayList<>();
        boolean success = executeQuery(sql, result -> {
            Collection<T> data = getDataFromResult(implementation, result);
            dataToReturn.addAll(data);
            return true;
        });

        return success ? dataToReturn : null;
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

        Collection<Field> declaredFields = getAllDeclaredFields(implementation);
        Map<String, Field> fieldsToFill = new HashMap<>();

        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String fieldNameLower = fieldName.toLowerCase();

            //Não existe entrada para este field na base de dados
            if (!sqlColNameLabel.containsKey(fieldNameLower))
                continue;

            field.setAccessible(true);

            //Verifica se o field ainda não foi registado | (Insensitive case)
            if (!fieldsToFill.containsKey(fieldNameLower)) {
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

                if (fieldType.isEnum()) {
                    //Tristemente em Java é impossível converter estas comparações para um switch
                    if (fieldType == API.Genero.class) {
                        value = API.Genero.valueOf(value.toString());//String.valueOf(Object obj) => obj != null ? obj.toString() ...
                    } else if (fieldType == API.Sexo.class) {
                        value = API.Sexo.valueOf(value.toString());
                    } else if (fieldType == API.TipoDeContagem.class) {
                        value = API.TipoDeContagem.valueOf(value.toString());
                    } else {
                        throw new IllegalArgumentException("Unknown Enum type: " + fieldType);
                    }
                }

                field.set(obj, value);
            }

            data.add(obj);
        }

        return data;
    }
    private Collection<Field> getAllDeclaredFields(Class<?> implementation) {
        Collection<Field> declaredFields = new LinkedList<>(Arrays.asList(implementation.getDeclaredFields()));
        Class<?> parentClass = implementation.getSuperclass();

        if (parentClass != null)
            declaredFields.addAll(getAllDeclaredFields(parentClass));

        return declaredFields;
    }
    private String convertSqlNamingToJava(String originalName) {
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
    private boolean insertModelId(ResultSet result, UniqueId uniqueIdObj) throws SQLException {
        if (!result.next())
            return false;

        uniqueIdObj.setId(result.getInt(1));
        return true;
    }

    private boolean createStatement(SqlExecute<Statement> statementCallback) {
        return createStatement(statementCallback, null);
    }
    private boolean createStatement(SqlExecute<Statement> statementCallback, Consumer<Exception> exceptionCallback) {
        return getConnection(connection -> {
            try (Statement statement = connection.createStatement()) {
                return statementCallback.invoke(statement);
            }
        }, exceptionCallback);
    }

    private boolean executeQuery(String sql, SqlExecute<ResultSet> result) {
        return executeQuery(sql, null, result, null);
    }
    private boolean executeQuery(String sql, SqlExecute<PreparedStatement> preparedQuery, SqlExecute<ResultSet> result, Consumer<Exception> exceptionCallback) {
        return getConnection(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (preparedQuery != null && !preparedQuery.invoke(preparedStatement))
                    return false;

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return result.invoke(resultSet);
                }
            }
        }, exceptionCallback);
    }

    private boolean executeUpdate(String sql, SqlExecute<PreparedStatement> preparedQuery, Consumer<Exception> exceptionCallback) {
        return executeUpdate(sql, preparedQuery, null, exceptionCallback);
    }
    private boolean executeUpdate(String sql, SqlExecute<PreparedStatement> preparedQuery, SqlExecute<ResultSet> result, Consumer<Exception> exceptionCallback) {
        return getConnection(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (preparedQuery != null && !preparedQuery.invoke(preparedStatement))
                    return false;

                if (preparedStatement.executeUpdate() == 0)
                    return false;

                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    return result.invoke(resultSet);
                }
            }
        }, exceptionCallback);
    }
    private boolean getConnection(SqlExecute<Connection> connectionCallback, Consumer<Exception> exceptionCallback) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            return connectionCallback.invoke(connection);
        } catch (Exception ex) {
            if (exceptionCallback != null)
                exceptionCallback.accept(ex);
            else
                logger.log(Level.WARNING, ex.getMessage(), ex);
            return false;
        }
    }

    @FunctionalInterface
    private interface SqlExecute<T> {
        /**
         * Executa esta operação no argumento fornecido.
         *
         * @param action ação a ser executada
         * @return deverá retornar verdadeiro(true) se a operação foi concluida com sucesso, senão falso(false) | se retornar falso(false) a execução de operações deverá parar
         */
        boolean invoke(T action) throws ReflectiveOperationException, SQLException;
    }
}
