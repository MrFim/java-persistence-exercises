package ua.procamp.util;

import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Optional;

public class OptimisticLockingExample {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "root";
    private static final String QUERY = "select * from programs where id = ?";

    private static DataSource dataSource;

    public static void main(String[] args) {

        dataSource = JdbcUtil.createPostgresDataSource(URL, USER, PASS);
        Long programId = 1L;
    }

    private static void handleProgramUdpateWithOptimisticLocking(Long programId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
            preparedStatement.setLong(1, programId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Program program = new Program();
            program.setName(resultSet.getString("name"));
            program.setId(resultSet.getLong("id"));
            program.setDescription(resultSet.getString("description"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
