package controllers;

import API.DatabaseConnector;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import model.*;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.*;
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
                    "`Atletas_Por_Prova` TINYINT(1) UNSIGNED NOT NULL DEFAULT 8, " +
                    "`Data_Da_Prova` DATETIME NOT NULL, " +
                    "`Deleted_At` TIMESTAMP NULL DEFAULT NULL, " +
                    "PRIMARY KEY (`Id`), " +
                    "KEY `Eventos_Provas` (`Evento_Id`), " +
                    "KEY `Modalidades_Provas` (`Modalidade_Id`), " +
                    "CONSTRAINT `Eventos_Provas` FOREIGN KEY (`Evento_Id`) REFERENCES `eventos` (`Id`) ON UPDATE CASCADE, " +
                    "CONSTRAINT `Modalidades_Provas` FOREIGN KEY (`Modalidade_Id`) REFERENCES `modalidades` (`Id`) ON UPDATE CASCADE, " +
                    "CONSTRAINT `Validate_Minimos` CHECK (`Minimos` > '0'), " +
                    "CONSTRAINT `Validate_Atletas_Por_Prova` CHECK (`Atletas_Por_Prova` > '1'));";
            statement.execute(query);

            query = "CREATE TABLE IF NOT EXISTS `atletas` (" +
                    "`Id` INT AUTO_INCREMENT, " +
                    "`Nome` VARCHAR(255) NOT NULL, " +
                    "`Pais` VARCHAR(75) NOT NULL, " +
                    "`Sexo` ENUM('M','F','X') NOT NULL, " +
                    "`Data_De_Nascimento` DATE NOT NULL, " +
                    "`Contacto` VARCHAR(255) NOT NULL, " +
                    "`Data_De_Registo` DATE NOT NULL DEFAULT CURRENT_DATE()," +
                    "`Deleted_At` TIMESTAMP NULL DEFAULT NULL, " +
                    "PRIMARY KEY (`Id`), " +
                    "CONSTRAINT `Validate_Nome` CHECK (`Nome` <> ''), " +
                    "CONSTRAINT `Validate_Pais` CHECK (`Pais` <> ''), " +
                    "CONSTRAINT `Validate_Data_De_Nascimento` CHECK (DATEDIFF(`Data_De_Registo`, `Data_De_Nascimento`) BETWEEN 2920 AND 36500), " +//2920 -> 8years | 36500year
                    "CONSTRAINT `Validate_Contacto` CHECK (`Contacto` <> ''));";
            statement.execute(query);

            query = "CREATE TABLE IF NOT EXISTS `inscricoes` (" +
                    "`Prova_Id` INT, " +
                    "`Atleta_Id` INT, " +
                    "PRIMARY KEY (`Prova_Id`, `Atleta_Id`), " +
                    "KEY `Inscricoes_Provas` (`Prova_Id`), " +
                    "KEY `Inscricoes_Atletas` (`Atleta_Id`), " +
                    "CONSTRAINT `Inscricoes_Provas` FOREIGN KEY (`Prova_Id`) REFERENCES `provas` (`Id`) ON UPDATE CASCADE, " +
                    "CONSTRAINT `Inscricoes_Atletas` FOREIGN KEY (`Atleta_Id`) REFERENCES `atletas` (`Id`) ON UPDATE CASCADE);";
            statement.execute(query);

            return true;
        });
    }

    @Override
    public Collection<Atleta> getAtletasInscritos(Prova prova) {
        //language=MariaDB
        String query = "SELECT `atletas`.* " +
                "FROM `atletas` " +
                "INNER JOIN (" +
                "SELECT `Atleta_Id` " +
                "FROM `inscricoes` " +
                "WHERE `Prova_Id` = ?" +
                ") `inscricoes` " +
                "ON `inscricoes`.`Atleta_Id` = `atletas`.`Id` " +
                "WHERE `Deleted_At` IS NULL;";

        return getDataFromQuery(Atleta.class, query, prepare -> {
            prepare.setInt(1, prova.getId());
            return true;
        });
    }

    @Override
    public Collection<Atleta> getAtletasNaoInscritos(Prova prova) {
        //language=MariaDB
        String query = "SELECT `atletas`.* " +
                "FROM `atletas` " +
                "LEFT JOIN (" +
                "SELECT `Atleta_Id` " +
                "FROM `inscricoes` " +
                "WHERE `Prova_Id` = ?" +
                ") `inscricoes` " +
                "ON `inscricoes`.`Atleta_Id` = `atletas`.`Id` " +
                "WHERE `inscricoes`.`Atleta_Id` IS NULL AND " +
                "`Deleted_At` IS NULL;";

        return getDataFromQuery(Atleta.class, query, prepare -> {
            prepare.setInt(1, prova.getId());
            return true;
        });
    }

    @Override
    public boolean delete(Atleta atleta) {
        //language=MariaDB
        String query = "UPDATE `atletas` " +
                "SET `Deleted_At` = NOW() " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepare.setInt(1, atleta.getId());
            return true;
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao apagar atleta! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }
    @Override
    public Collection<Atleta> getAtletas() {
        //language=MariaDB
        String query = "SELECT * " +
                "FROM `atletas` " +
                "WHERE `Deleted_At` IS NULL;";

        return getDataFromQuery(Atleta.class, query);
    }

    @Override
    public boolean store(Atleta atleta) {
        //language=MariaDB
        String query = "INSERT INTO `atletas` " +
                "(`Nome`, `Pais`, `Sexo`, `Data_De_Nascimento`, `Contacto`) " +
                "VALUES (?, ?, ?, ?, ?);";

        return executeUpdate(query, prepare -> {
            prepareAtleta(atleta, prepare);
            return true;
        }, result -> insertModelId(result, atleta), ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao guardar atleta! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    @Override
    public boolean update(Atleta atleta) {
        //language=MariaDB
        String query = "UPDATE `atletas` " +
                "SET `Nome` = ?, " +
                "`Pais` = ?, " +
                "`Sexo` = ?, " +
                "`Data_De_Nascimento` = ?, " +
                "`Contacto` = ? " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepareAtleta(atleta, prepare);
            prepare.setInt(6, atleta.getId());
            return true;
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao atualizar atleta! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    private void prepareAtleta(Atleta atleta, PreparedStatement prepare) throws SQLException {
        prepare.setString(1, atleta.getNome());
        prepare.setString(2, atleta.getPais());
        prepare.setString(3, atleta.getSexo().name());
        prepare.setDate(4, new Date(atleta.getDataDeNascimentoTime()));
        prepare.setString(5, atleta.getContacto());
    }

    @Override
    public boolean delete(Evento evento) {
        //language=MariaDB
        String query = "UPDATE `eventos` " +
                "SET `Deleted_At` = NOW() " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepare.setInt(1, evento.getId());
            return true;
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao apagar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    @Override
    public Evento getEvento(Prova prova) {
        //language=MariaDB
        String query = "SELECT * " +
                "FROM `eventos` " +
                "WHERE `Id` = ? AND " +
                "`Deleted_At` IS NULL;";

        Collection<Evento> eventos = getDataFromQuery(Evento.class, query, prepare -> {
            prepare.setInt(1, prova.getEventoId());
            return true;
        });

        if (eventos == null || eventos.isEmpty())
            return null;

        return Iterables.get(eventos, 0);
    }

    @Override
    public Collection<Evento> getEventos() {
        return getEventos(false, true, true);
    }

    @Override
    public Collection<Evento> getEventosAtuais() {
        return getEventos(true, false, false);
    }

    @Override
    public Collection<Evento> getEventoAtuaisOuAnteriores() {
        return getEventos(true, true, false);
    }

    @Override
    public Collection<Evento> getEventoAtuaisOuFuturos() {
        return getEventos(true, false, true);
    }

    /**
     * when decorrer is false, antigos and futuros are ignored
     */
    private Collection<Evento> getEventos(boolean decorrer, boolean antigos, boolean futuros) {
        //language=MariaDB
        String query = "SELECT * " +
                "FROM `eventos` " +
                "WHERE " + (decorrer ?
                (!antigos  ? "`Fim` >= CURRENT_DATE() AND " : "") +
                (!futuros ? "`Inicio` <= CURRENT_DATE() AND " : "")
                : "") +
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
            prepareEvento(evento, prepare);
            return true;
        }, result -> insertModelId(result, evento), ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
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
            prepareEvento(evento, prepare);
            prepare.setInt(6, evento.getId());
            return true;
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao atualizar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    private void prepareEvento(Evento evento, PreparedStatement prepare) throws SQLException {
        prepare.setString(1, evento.getNome());
        prepare.setDate(2, new Date(evento.getInicioTime()));
        prepare.setDate(3, new Date(evento.getFimTime()));
        prepare.setString(4, evento.getPais());
        prepare.setString(5, evento.getLocal());
    }

    @Override
    public boolean delete(Prova prova) {
        //language=MariaDB
        String query = "UPDATE `provas` " +
                "SET `Deleted_At` = NOW() " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepare.setInt(1, prova.getId());
            return true;
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao apagar prova! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
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
    public Collection<Prova> getProvasAtuais() {
        return getProvas(true);
    }

    private Collection<Prova> getProvas(boolean decorrer) {
        //language=MariaDB
        String query = "SELECT `provas`.*, CONCAT(`eventos`.`Nome`, ' - ', `modalidades`.`Nome`) AS nome " +
                "FROM `provas` " +
                "INNER JOIN (" +
                "SELECT `Id`, `Nome` " +
                "FROM `eventos` " +
                "WHERE " + (decorrer ?
                "`Inicio` <= CURRENT_DATE() AND " +
                        "`Fim` >= CURRENT_DATE() AND " : "") +
                "`Deleted_At` IS NULL" +
                ") `eventos` " +
                "ON `eventos`.`Id` = `provas`.`Evento_Id` " +
                "INNER JOIN (" +
                "SELECT `Id`, `Nome` " +
                "FROM `modalidades` " +
                "WHERE `Deleted_At` IS NULL" +
                ") `modalidades` " +
                "ON `modalidades`.`Id` = `provas`.`Modalidade_Id` " +
                "WHERE `Deleted_At` IS NULL;";

        return getDataFromQuery(Prova.class, query);
    }

    @Override
    public boolean store(Prova prova) {
        //language=MariaDB
        String query = "INSERT INTO `provas` " +
                "(`Evento_Id`, `Modalidade_Id`, `Sexo`, `Minimos`, `Atletas_Por_Prova`, `Data_Da_Prova`) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        return executeUpdate(query, prepare -> {
            prepareProva(prova, prepare);
            return true;
        }, result -> insertModelId(result, prova), ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
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
                "`Atletas_Por_Prova` = ?, " +
                "`Data_Da_Prova` = ? " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepareProva(prova, prepare);
            prepare.setInt(7, prova.getId());
            return true;
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao atualizar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    private void prepareProva(Prova prova, PreparedStatement prepare) throws SQLException {
        prepare.setInt(1, prova.getEventoId());
        prepare.setInt(2, prova.getModalidadeId());
        prepare.setString(3, prova.getSexo().name());
        prepare.setInt(4, prova.getMinimos());
        prepare.setByte(5, prova.getAtletasPorProva());
        prepare.setTimestamp(6, new Timestamp(prova.getDataDaProvaTime()));
    }

    @Override
    public boolean delete(Modalidade modalidade) {
        //language=MariaDB
        String query = "UPDATE `modalidades` " +
                "SET `Deleted_At` = NOW() " +
                "WHERE `Id`= ?;";

        return executeUpdate(query, prepare -> {
            prepare.setInt(1, modalidade.getId());
            return true;
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao apagar modalidade! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
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
        }, result -> insertModelId(result, modalidade), ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
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
        }, ex -> {
            if (ex instanceof SQLNonTransientConnectionException sqlEx) {
                viewController.mostrarAviso("Falha ao atualizar evento! | Erro: Falha ao connectar com a base de dados | Code:" + sqlEx.getSQLState());
                return;
            }

            logger.log(Level.WARNING, ex.getMessage(), ex);
        });
    }

    @Override
    public boolean inscreverAtletasEmProva(Prova prova, List<Atleta> inscrever, List<Atleta> desinscrever) {
        int provaId = prova.getId();
        return getConnection(connection -> {
            connection.setAutoCommit(false);

            if (!inscrever.isEmpty()) {
                StringBuilder queryBuilder = new StringBuilder("INSERT IGNORE INTO `inscricoes` " +
                        "(`Prova_Id`, `Atleta_Id`) VALUES ");

                int size = inscrever.size();
                for (int i = 1; i <= size; i++)
                {
                    queryBuilder.append("(?, ?)");
                    if (i == size)
                        queryBuilder.append(";");
                    else
                        queryBuilder.append(", ");
                }

                if (prepareInscreverAtletaEmProva(inscrever, provaId, connection, queryBuilder, size))
                    return false;
            }

            if (!desinscrever.isEmpty()) {
                StringBuilder queryBuilder = new StringBuilder("DELETE FROM`inscricoes` " +
                        "WHERE ");

                int size = desinscrever.size();
                for (int i = 1; i <= size; i++)
                {
                    queryBuilder.append("(`Prova_Id` = ? AND `Atleta_Id` = ?)");
                    if (i == size)
                        queryBuilder.append(";");
                    else
                        queryBuilder.append(" OR ");
                }

                if (prepareInscreverAtletaEmProva(desinscrever, provaId, connection, queryBuilder, size))
                    return false;
            }

            connection.commit();
            return true;
        }, ex -> logger.log(Level.WARNING, ex.getMessage(), ex));
    }

    private boolean prepareInscreverAtletaEmProva(List<Atleta> collection, int provaId, Connection connection, StringBuilder queryBuilder, int size) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 1; i <= size; i++)
            {
                Atleta atleta = collection.get(i-1);
                int sqlIndex = i * 2;
                preparedStatement.setInt(sqlIndex - 1, provaId);
                preparedStatement.setInt(sqlIndex, atleta.getId());
            }

            if (preparedStatement.executeUpdate() == 0)
                return true;
        }
        return false;
    }

    private <T> Collection<T> getDataFromQuery(Class<T> implementation, String query) {
        Collection<T> dataToReturn = new ArrayList<>();
        boolean success = executeQuery(query, result -> {
            Collection<T> data = getDataFromResult(implementation, result);
            dataToReturn.addAll(data);
            return true;
        });

        return success ? dataToReturn : null;
    }
    private <T> Collection<T> getDataFromQuery(Class<T> implementation, String query, SqlExecute<PreparedStatement> preparedQuery) {
        Collection<T> dataToReturn = new ArrayList<>();
        boolean success = executeQuery(query, preparedQuery, result -> {
            Collection<T> data = getDataFromResult(implementation, result);
            dataToReturn.addAll(data);
            return true;
        });

        return success ? dataToReturn : null;
    }

    private <T> Collection<T> getDataFromResult(Class<T> implementation, ResultSet resultSet) throws ReflectiveOperationException, SQLException {
        ResultSetMetaData sqlMetaData = resultSet.getMetaData();

        Map<String, Integer> sqlColNameIndex = new HashMap<>();
        Map<String, String> sqlColNameOrig = new HashMap<>();

        int colCount = sqlMetaData.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            String originalName = sqlMetaData.getColumnName(i);
            //OriginalName precisa de ser processado pois em Java os campos não usam underscore por conveniência
            String name = convertSqlNamingToJava(originalName);

            String lowerName = name.toLowerCase();

            sqlColNameIndex.put(lowerName, i);
            sqlColNameOrig.put(lowerName, name);
        }

        Collection<Field> declaredFields = getAllDeclaredFields(implementation);
        Map<String, Field> fieldsToFill = new HashMap<>();

        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String fieldNameLower = fieldName.toLowerCase();

            //Não existe entrada para este field na base de dados
            if (!sqlColNameIndex.containsKey(fieldNameLower))
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
                Integer colIndex = sqlColNameIndex.get(fieldName);

                Field field = entry.getValue();
                Class<?> fieldType = field.getType();
                Object value = resultSet.getObject(colIndex);

                if (fieldType.isEnum()) {
                    //Tristemente em Java é impossível converter estas comparações para um switch
                    if (fieldType == API.Sexo.class) {
                        value = API.Sexo.valueOf(value.toString());//String.valueOf(Object obj) => obj != null ? obj.toString() ...
                    } else if (fieldType == API.TipoDeContagem.class) {
                        value = API.TipoDeContagem.valueOf(value.toString());
                    } else {
                        throw new IllegalArgumentException("Unknown Enum type: " + fieldType);
                    }
                }

                //Fix sql not converting TinyInt to Byte
                if ((field.getType() == byte.class || field.getType() == Byte.class) && value.getClass() == Integer.class) {
                    value = (byte) (int) value;
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
        return executeQuery(sql, null, result);
    }

    private boolean executeQuery(String sql, SqlExecute<PreparedStatement> preparedQuery, SqlExecute<ResultSet> result) {
        return executeQuery(sql, preparedQuery, result, null);
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

                if (result == null)
                    return true;

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
        boolean invoke(T action) throws ReflectiveOperationException, SQLException;
    }
}
