package controller.baker;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import dao.ProduksiDAO;
import model.Produksi;

@WebServlet("/baker/produksi")
public class ProduksiListServlet extends HttpServlet {
    private ProduksiDAO produksiDAO;
    
    @Override
    public void init() {
        produksiDAO = new ProduksiDAO();
    }
    
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    ProduksiDAO produksiDAO = new ProduksiDAO();

    try {
        String keyword = request.getParameter("keyword");
        List<Produksi> produksiList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            produksiList = produksiDAO.searchProduksi(keyword.trim());
        } else {
            produksiList = produksiDAO.getAllProduksi();
        }

        request.setAttribute("produksiList", produksiList);
        request.getRequestDispatcher("/view/baker/Produksi.jsp").forward(request, response);

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("error", "Gagal memuat data produksi.");
        request.getRequestDispatcher("/view/baker/Produksi.jsp").forward(request, response);
    }
}

}