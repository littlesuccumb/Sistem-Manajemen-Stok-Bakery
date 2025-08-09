package controller.kasir;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.ProduksiDAO;
import model.Produksi;
import model.User;

@WebServlet({"/kasir/dashboard", "/kasir/load-produk"})
public class KasirDashboardServlet extends HttpServlet {
    private ProduksiDAO produksiDAO;

    @Override
    public void init() {
        produksiDAO = new ProduksiDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"kasir".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String servletPath = request.getServletPath();
        
        try {
            if ("/kasir/load-produk".equals(servletPath)) {
                // Untuk include dari JSP
                loadProdukData(request, response);
            } else {
                // Untuk dashboard utama
                loadDashboard(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Terjadi kesalahan saat memuat data produk: " + e.getMessage());
            request.getRequestDispatcher("/view/kasir/index.jsp").forward(request, response);
        }
    }

    private void loadProdukData(HttpServletRequest request, HttpServletResponse response) 
        throws SQLException, ServletException, IOException {
    
    String keyword = request.getParameter("keyword");
    List<Produksi> produkList;
    
    if (keyword != null && !keyword.trim().isEmpty()) {
        produkList = produksiDAO.searchProduksi(keyword.trim());
    } else {
        produkList = produksiDAO.getProdukTersedia();
    }

    request.setAttribute("produkList", produkList);
}

    private void loadDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        // Load produk data
        loadProdukData(request, response);
        
        // Forward ke dashboard
        request.getRequestDispatcher("/view/kasir/index.jsp").forward(request, response);
    }
}