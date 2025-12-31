package model.misura;

import model.DBConnection;
import model.acquisto.AcquistoBean;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MisuraDAO {
    private static final String TABLE_NAME = "Misura";
    private static DataSource ds;

    public MisuraDAO() {
        ds = DBConnection.getDataSource();
    }

    public MisuraDAO(DataSource ds) {
        MisuraDAO.ds = ds;
    }

    public void doSave(MisuraBean product) throws SQLException {
        String query = "INSERT INTO " + TABLE_NAME + " (IDMaglietta, taglia, quantita)" + " VALUES (?, ?, ?)";

        try (Connection connection = ds.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, product.getIDMaglietta());
            preparedStatement.setString(2, product.getTaglia());
            preparedStatement.setInt(3, product.getQuantita());

            preparedStatement.executeUpdate();
        }
    }

    public void doUpdate(MisuraBean misuraBean) throws SQLException {
        String query = "UPDATE " + TABLE_NAME +
                " SET quantita = ?" +
                " WHERE IDMaglietta = ? AND taglia = ?";

        try (Connection connection = ds.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, misuraBean.getQuantita());
            preparedStatement.setInt(2, misuraBean.getIDMaglietta());
            preparedStatement.setString(3, misuraBean.getTaglia());

            preparedStatement.executeUpdate();
        }
    }

    public void doUpdateUtente(AcquistoBean product, String taglia) throws SQLException {
        String query = "UPDATE " + TABLE_NAME +
                       " SET quantita = quantita - ? " +
                       "WHERE IDMaglietta = ? AND taglia = ?";

        try (Connection connection = ds.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, product.getQuantita());
            preparedStatement.setInt(2, product.getIDMaglietta());
            preparedStatement.setString(3, taglia);

            preparedStatement.executeUpdate();
        }
    }

    public Collection<MisuraBean> doRetrieveAll(int idMaglietta) throws SQLException {
        Collection<MisuraBean> misure = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE idMaglietta = ?";

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idMaglietta);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    MisuraBean misuraBean = new MisuraBean();
                    setMisura(resultSet, misuraBean);
                    misure.add(misuraBean);
                }
            }
        }

        return misure;
    }

    private void setMisura(ResultSet resultSet, MisuraBean misuraBean) throws SQLException {
        misuraBean.setIDMaglietta(resultSet.getInt("IDMaglietta"));
        misuraBean.setTaglia(resultSet.getString("taglia"));
        misuraBean.setQuantita(resultSet.getInt("quantita"));
    }
}
