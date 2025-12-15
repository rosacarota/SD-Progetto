package model.maglietta;

import model.DAOInterface;
import model.DBConnection;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class MagliettaDAO implements DAOInterface<MagliettaBean, Integer> {
    private static final String TABLE_NAME = "Maglietta";
    private final DataSource ds;
    private static final List<String> ORDERS =
            new ArrayList<>(Arrays.asList("nome", "prezzo", "colore", "tipo"));

    public MagliettaDAO() {
        ds = DBConnection.getDataSource();
    }

    public MagliettaDAO(DataSource ds) {
        this.ds = ds;
    }

    public synchronized Collection<MagliettaBean> doRetrieveByTipo(String tipo) throws SQLException {
        Collection<MagliettaBean> maglietteTipo = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE tipo = ?";

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, tipo);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    MagliettaBean magliettaBean = new MagliettaBean();
                    setMaglietta(resultSet, magliettaBean);
                    maglietteTipo.add(magliettaBean);
                }
            }
        }

        return maglietteTipo;
    }

    // Restituisce un oggetto maglietta con delle caratteristiche (SQL SELECT)
    @Override
    public synchronized MagliettaBean doRetrieveByKey(Integer code) throws SQLException {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE ID = ?";

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (!resultSet.next()) {
                    throw new SQLException("Maglietta non trovata con ID: " + code);
                }

                MagliettaBean magliettaBean = new MagliettaBean();
                setMaglietta(resultSet, magliettaBean);
                return magliettaBean;
            }
        }
    }

    // Restituisce una collezione di magliette che soddisfano una condizione (SQL ORDER BY)
    @Override
    public Collection<MagliettaBean> doRetriveAll(String order) throws SQLException {
        Collection<MagliettaBean> magliette = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT * FROM " + TABLE_NAME + " WHERE Tipo <> 'Personalizzata' AND Tipo <> 'Eliminata'"
        );

        for (String s : ORDERS) {
            if (s.equals(order)) {
                query.append(" ORDER BY ").append(s);
                break;
            }
        }

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                MagliettaBean magliettaBean = new MagliettaBean();
                setMaglietta(resultSet, magliettaBean);
                magliette.add(magliettaBean);
            }
        }

        return magliette;
    }

    // Salva i dati dell'oggetto maglietta nel database (SQL Insert)
    @Override
    public void doSave(MagliettaBean maglietta) throws SQLException {
        String query = "INSERT INTO " + TABLE_NAME +
                " (nome, prezzo, IVA, colore, tipo, grafica, descrizione) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setMagliettaStatement(maglietta, preparedStatement);
            preparedStatement.executeUpdate();
        }
    }

    // Aggiorna i dati dell'oggetto maglietta nel database (SQL UPDATE)
    @Override
    public void doUpdate(MagliettaBean maglietta) throws SQLException {
        String query = "UPDATE " + TABLE_NAME +
                " SET nome = ?, prezzo = ?, IVA = ?, colore = ?, tipo = ?, grafica = ?, descrizione = ? " +
                "WHERE ID = ?";

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            setMagliettaStatement(maglietta, preparedStatement);
            preparedStatement.setInt(8, maglietta.getID());
            preparedStatement.executeUpdate();
        }
    }

    // Cancella i dati dell'oggetto maglietta dal database (SQL DELETE)
    @Override
    public boolean doDelete(Integer code) throws SQLException {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?";
        int result;

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, code);
            result = preparedStatement.executeUpdate();
        }

        return result != 0;
    }

    public boolean deleteMaglietta(Integer code) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET Tipo = 'Eliminata' WHERE ID = ?";
        int result;

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, code);
            result = preparedStatement.executeUpdate();
        }

        return result != 0;
    }

    public int getMaxID() throws SQLException {
        String sessionCacheQuery = "SET @@SESSION.information_schema_stats_expiry = 0;";
        String query = "SELECT AUTO_INCREMENT " +
                "FROM information_schema.tables " +
                "WHERE table_name = ? AND table_schema = 'whiTee'";

        try (Connection connection = ds.getConnection();
             Statement cacheStmt = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            cacheStmt.execute(sessionCacheQuery);

            preparedStatement.setString(1, TABLE_NAME);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("AUTO_INCREMENT non trovato per tabella: " + TABLE_NAME);
                }
                return resultSet.getInt("AUTO_INCREMENT");
            }
        }
    }

    private void setMaglietta(ResultSet resultSet, MagliettaBean magliettaBean) throws SQLException {
        magliettaBean.setID(resultSet.getInt("ID"));
        magliettaBean.setNome(resultSet.getString("nome"));
        magliettaBean.setPrezzo(resultSet.getFloat("prezzo"));
        magliettaBean.setIVA(resultSet.getInt("IVA"));
        magliettaBean.setColore(resultSet.getString("colore"));
        magliettaBean.setTipo(resultSet.getString("tipo"));
        magliettaBean.setGrafica(resultSet.getString("grafica"));
        magliettaBean.setDescrizione(resultSet.getString("descrizione"));
    }

    private void setMagliettaStatement(MagliettaBean maglietta, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, maglietta.getNome());
        preparedStatement.setFloat(2, maglietta.getPrezzo());
        preparedStatement.setInt(3, maglietta.getIVA());
        preparedStatement.setString(4, maglietta.getColore());
        preparedStatement.setString(5, maglietta.getTipo());
        preparedStatement.setString(6, maglietta.getGrafica());
        preparedStatement.setString(7, maglietta.getDescrizione());
    }
}
