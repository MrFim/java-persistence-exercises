package ua.procamp.dao;

import ua.procamp.exception.DaoOperationException;
import ua.procamp.model.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductDaoImpl implements ProductDao {
    private DataSource dataSource;

    private static final String COMPANY_NAME = "products";
    private static final String SAVE_QUERY = "INSERT INTO " + COMPANY_NAME +
            " (name, producer, price, expiration_date, creation_time)" +
            " values (?, ?, ?, ?, ?);";
    private static final String FIND_ALL_QUERY = "select * from " + COMPANY_NAME;
    private static final String FIND_BY_ID_QUERY = "select * from " + COMPANY_NAME + " where id = (?)";
    private static final String DELETE_QUERY = "delete from " + COMPANY_NAME + " where id = (?)";
    private static final String UPDATE_QUERY = "update " + COMPANY_NAME + " set " +
            "name = (?)," +
            "producer = (?)," +
            "price = (?)," +
            "expiration_date = (?), " +
            "creation_time = (?)" +
            "where id = (?)";

    public ProductDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Product product) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            setDataToInsertedObjectAndValidate(product, preparedStatement);
            preparedStatement.executeUpdate();
             try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                 if (resultSet.next()) {
                     product.setId(resultSet.getLong(1));
                 }
             }
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY);
            while (resultSet.next()) {
                Product product = new Product();
                product = setDataToProductFromResultSet(product, resultSet);
                products.add(product);
            }
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
        return products;
    }

    @Override
    public Product findOne(Long id) {
        validateIdNumber(id);

        Product product = new Product();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                product = setDataToProductFromResultSet(product, resultSet);
            }
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
        return product;
    }

    private Product setDataToProductFromResultSet(Product product, ResultSet resultSet) {
        try {
            product.setId(resultSet.getLong("id"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getBigDecimal("price"));
            product.setProducer(resultSet.getString("producer"));
            product.setExpirationDate(resultSet.getDate("expiration_date").toLocalDate());
            product.setCreationTime(resultSet.getObject("creation_time", LocalDateTime.class));
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
        return product;
    }

    @Override
    public void update(Product product) {
        idNotNull(product);
        validateIdNumber(product.getId());

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
            setDataToInsertedObjectAndValidate(product, preparedStatement);
            preparedStatement.setLong(6, product.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    @Override
    public void remove(Product product) {
        idNotNull(product);
        validateIdNumber(product.getId());

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setLong(1, product.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    private void validateIdNumber(Long id) {
        if (id <= 0) {
            throw new DaoOperationException(String.format("Product with id = %d does not exist", id));
        }
    }

    private void idNotNull(Product product) {
        if (Objects.isNull(product.getId())) {
            throw new DaoOperationException("Product id cannot be null");
        }
    }

    private void setDataToInsertedObjectAndValidate(Product product, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, product.getName());
        preparedStatement.setString(2, product.getProducer());
        preparedStatement.setBigDecimal(3, product.getPrice());
        preparedStatement.setDate(4, Date.valueOf(product.getExpirationDate()));
        if (Objects.isNull(product.getProducer()) || Objects.isNull(product.getName())
                || Objects.isNull(product.getExpirationDate())) {
            throw new DaoOperationException("Error saving product: " + product);
        }
        if (product.getCreationTime() != null) {
            preparedStatement.setTimestamp(5, Timestamp.valueOf(product.getCreationTime()));
        } else {
            preparedStatement.setDate(5, Date.valueOf(LocalDate.now()));
        }
    }

}
