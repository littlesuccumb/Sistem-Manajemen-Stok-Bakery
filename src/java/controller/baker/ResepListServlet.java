package controller.baker;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import dao.ResepDAO;
import model.Resep;

@WebServlet("/baker/resep")
public class ResepListServlet extends HttpServlet {
    private ResepDAO resepDAO;

    @Override
    public void init() {
        resepDAO = new ResepDAO();
    }

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    try {
        String keyword = request.getParameter("keyword");
        List<Resep> resepList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            resepList = resepDAO.searchResep(keyword.trim());
        } else {
            resepList = resepDAO.getAllResep();
        }

        request.setAttribute("resepList", resepList);
        request.getRequestDispatcher("/view/baker/Resep.jsp").forward(request, response);
    } catch (SQLException e) {
        e.printStackTrace();
        response.sendRedirect(request.getContextPath() + "/view/error.jsp");
    }
}

}
