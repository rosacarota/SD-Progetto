package control.search;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import model.DBConnection;

@WebServlet("/SearchBar")
public class SearchBar extends HttpServlet {
    private static final String TABLE_NAME = "Maglietta";
    private static DataSource ds;

    public SearchBar() {
        ds = DBConnection.getDataSource();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE Tipo <> 'Personalizzata' AND Tipo <> 'Eliminata' AND nome LIKE ?";
        try (Connection connection = ds.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + search + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            // Lista da far passare in json
            List<Map<String, Object>> results = new ArrayList<>();
            // Per prendere nomi delle colonne e oggetti delle colonne per ogni rigo della tabella
            ResultSetMetaData metaData = resultSet.getMetaData();
            int colonne = metaData.getColumnCount();

            // Per riempire la Lista
            while (resultSet.next()) {
                // Un oggetto Map per ogni valore delle colonne
                Map<String, Object> oggetto = new HashMap<>();
                for (int i = 1; i <= colonne; i++) {
                    String nomeColonna = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    oggetto.put(nomeColonna,value);
                }
                results.add(oggetto);
            }

            String lista = new Gson().toJson(results);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(lista);

        } catch (SQLException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req,resp);
    }
}
