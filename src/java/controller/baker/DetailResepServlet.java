package controller.baker;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.ResepDAO;
import dao.DetailResepDAO;
import model.Resep;
import model.DetailResep;
import model.User;

@WebServlet("/baker/detail-resep")
public class DetailResepServlet extends HttpServlet {
    private ResepDAO resepDAO;
    private DetailResepDAO detailResepDAO;

    @Override
    public void init() {
        resepDAO = new ResepDAO();
        detailResepDAO = new DetailResepDAO(); // ✅ Tambahkan ini
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"baker".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/baker/resep?error=ID resep tidak valid");
                return;
            }

            int resepId = Integer.parseInt(idParam.trim());

            // Ambil data resep utama
            Resep resep = resepDAO.getResepById(resepId);
            if (resep == null) {
                response.sendRedirect(request.getContextPath() + "/baker/resep?error=Resep tidak ditemukan");
                return;
            }

            // ✅ Ambil detail bahan dari DAO yang benar
            List<DetailResep> bahanList = detailResepDAO.getDetailByResepId(resepId);

            // Set ke request attribute
            request.setAttribute("resep", resep);
            request.setAttribute("bahanList", bahanList);

            // Forward ke JSP
            request.getRequestDispatcher("/view/baker/DetailResep.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/baker/resep?error=Format ID resep tidak valid");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/baker/resep?error=Terjadi kesalahan sistem");
        }
    }
}
