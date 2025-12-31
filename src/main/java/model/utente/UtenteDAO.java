package model.utente;

import model.DAOInterface;
import model.DBConnection;
import model.security.CryptoKeyProvider;
import model.security.CryptoUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class UtenteDAO implements DAOInterface<UtenteBean, String> {

    private static final String TABLE_NAME = "Utente";
    private static final SecretKey KEY = CryptoKeyProvider.getKey();
    private static DataSource ds;

    public UtenteDAO() {
        ds = DBConnection.getDataSource();
    }

    public UtenteDAO(DataSource ds) {
        UtenteDAO.ds = ds;
    }

    @Override
    public UtenteBean doRetrieveByKey(String code) throws SQLException {
        UtenteBean user = new UtenteBean();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
        return getUtenteBean(code, user, query);
    }

    @Override
    public Collection<UtenteBean> doRetriveAll(String order) throws SQLException {
        Collection<UtenteBean> users = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UtenteBean user = new UtenteBean();
                setUtente(resultSet, user);
                users.add(user);
            }
        }

        return users;
    }

    public UtenteBean doRetrieveByEmail(String email) throws SQLException {
        UtenteBean user = new UtenteBean();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";
        return getUtenteBean(email, user, query);
    }

    @Override
    public synchronized void doSave(UtenteBean u) throws SQLException {
        String query = "INSERT INTO " + TABLE_NAME +
                " (username, pwd, nome, cognome, email, dataNascita, nomeCarta, cognomeCarta, numCarta, dataScadenza, CVV, cap, via, citta, tipo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, BCrypt.hashpw(u.getPwd(), BCrypt.gensalt()));
            ps.setString(3, u.getNome());
            ps.setString(4, u.getCognome());
            ps.setString(5, u.getEmail());
            ps.setDate(6, Date.valueOf(u.getDataNascita()));

            try {
                ps.setString(7, encryptOrNull(KEY, u.getNomeCarta()));
                ps.setString(8, encryptOrNull(KEY, u.getCognomeCarta()));
                ps.setString(9, encryptOrNull(KEY, u.getNumCarta()));
                ps.setString(10, encryptOrNull(KEY, u.getDataScadenza() == null ? null : u.getDataScadenza().toString()));
                ps.setString(11, encryptOrNull(KEY, u.getCVV()));
            } catch (Exception e) {
                throw new SQLException("Encryption error", e);
            }

            ps.setString(12, u.getCap());
            ps.setString(13, u.getVia());
            ps.setString(14, u.getCitta());
            ps.setString(15, u.getTipo());

            ps.executeUpdate();
        }
    }

    @Override
    public synchronized void doUpdate(UtenteBean u) throws SQLException {
        String query = "UPDATE " + TABLE_NAME +
                " SET pwd = ?, nome = ?, cognome = ?, email = ?, dataNascita = ?, " +
                "numCarta = ?, dataScadenza = ?, CVV = ?, nomeCarta = ?, cognomeCarta = ?, " +
                "cap = ?, via = ?, citta = ?, tipo = ? WHERE username = ?";

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, BCrypt.hashpw(u.getPwd(), BCrypt.gensalt()));
            ps.setString(2, u.getNome());
            ps.setString(3, u.getCognome());
            ps.setString(4, u.getEmail());
            ps.setDate(5, Date.valueOf(u.getDataNascita()));

            try {
                ps.setString(6, encryptOrNull(KEY, u.getNumCarta()));
                ps.setString(7, encryptOrNull(KEY, u.getDataScadenza() == null ? null : u.getDataScadenza().toString()));
                ps.setString(8, encryptOrNull(KEY, u.getCVV()));
                ps.setString(9, encryptOrNull(KEY, u.getNomeCarta()));
                ps.setString(10, encryptOrNull(KEY, u.getCognomeCarta()));
            } catch (Exception e) {
                throw new SQLException("Encryption error", e);
            }

            ps.setString(11, u.getCap());
            ps.setString(12, u.getVia());
            ps.setString(13, u.getCitta());
            ps.setString(14, u.getTipo());
            ps.setString(15, u.getUsername());

            ps.executeUpdate();
        }
    }

    @Override
    public boolean doDelete(String code) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE username = ?")) {

            ps.setString(1, code);
            return ps.executeUpdate() != 0;
        }
    }

    private UtenteBean getUtenteBean(String code, UtenteBean user, String query) throws SQLException {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst())
                return null;

            rs.next();
            setUtente(rs, user);
        }

        return user;
    }

    private void setUtente(ResultSet rs, UtenteBean u) throws SQLException {
        u.setUsername(rs.getString("username"));
        u.setPwd(rs.getString("pwd"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setEmail(rs.getString("email"));

        Date birth = rs.getDate("dataNascita");
        if (birth != null) u.setDataNascita(birth.toLocalDate());

        try {
            u.setNomeCarta(decryptOrNull(KEY, rs.getString("nomeCarta")));
            u.setCognomeCarta(decryptOrNull(KEY, rs.getString("cognomeCarta")));
            u.setNumCarta(decryptOrNull(KEY, rs.getString("numCarta")));
            u.setCVV(decryptOrNull(KEY, rs.getString("CVV")));

            String exp = rs.getString("dataScadenza");
            u.setDataScadenza(exp == null ? null : LocalDate.parse(decryptOrNull(KEY, exp)));

        } catch (Exception e) {
            throw new SQLException("Decryption error", e);
        }

        u.setCap(rs.getString("cap"));
        u.setVia(rs.getString("via"));
        u.setCitta(rs.getString("citta"));
        u.setTipo(rs.getString("tipo"));
    }

    private String encryptOrNull(SecretKey key, String v) throws GeneralSecurityException {
        return (v == null || v.isEmpty()) ? null : CryptoUtils.encrypt(key, v);
    }

    private String decryptOrNull(SecretKey key, String v) throws GeneralSecurityException {
        return (v == null) ? null : CryptoUtils.decrypt(key, v);
    }
}

