package control.utente;

import model.utente.UtenteBean;
import model.utente.UtenteDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/Registrazione")
public class Registrazione extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("usernameReg");
        String password = req.getParameter("passwordReg");
        String nome = req.getParameter("nomeReg");
        String cognome = req.getParameter("cognomeReg");
        String email = req.getParameter("emailReg");
        String tipo = req.getParameter("tipo");
        LocalDate dataNascita;
        try {
            dataNascita = LocalDate.parse(req.getParameter("dataNascitaReg"));
        } catch (Exception ex) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();

        try {
            if (utenteDAO.doRetrieveByKey(username) != null ||
                    utenteDAO.doRetrieveByEmail(email) != null) {
                req.getSession().setAttribute("utentePresente", true);
            } else {
                req.getSession().setAttribute("utentePresente", false);
                UtenteBean utenteBean = new UtenteBean();
                utenteBean.setUsername(username);
                utenteBean.setPwd(password);
                utenteBean.setNome(nome);
                utenteBean.setCognome(cognome);
                utenteBean.setEmail(email);
                utenteBean.setDataNascita(dataNascita);
                utenteBean.setTipo(tipo);

                utenteDAO.doSave(utenteBean);
            }
        } catch (SQLException e) {
            req.getRequestDispatcher("/pages/errorpage.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect("pages/login.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req,resp);
    }
}
