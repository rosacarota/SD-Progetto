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
    private static final String ERROR_PAGE = "/pages/errorpage.jsp";

    public SearchBar() {
        ds = DBConnection.getDataSource();
    }

    public SearchBar(DataSource ds) {
        SearchBar.ds = ds;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String search = req.getParameter("search");
        if (search == null) {
            search = "";
        }

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE Tipo <> 'Personalizzata' AND Tipo <> 'Eliminata' AND nome LIKE ?";

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + search + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int colonne = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> oggetto = new HashMap<>();
                for (int i = 1; i <= colonne; i++) {
                    oggetto.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                results.add(oggetto);
            }

            String lista = new Gson().toJson(results);

            writeJsonResponse(resp, lista);

        } catch (SQLException | IOException e) {
            req.getRequestDispatcher(ERROR_PAGE).forward(req, resp);
        }

    }

    private void writeJsonResponse(HttpServletResponse resp,
                                   String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}
