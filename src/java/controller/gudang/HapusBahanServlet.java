package controller.gudang;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.BahanBakuDAO;
import model.BahanBaku;
import model.User;

@WebServlet("/gudang/hapus-bahan")
public class HapusBahanServlet extends HttpServlet {
    private BahanBakuDAO bahanBakuDAO;

    @Override
    public void init() {
        bahanBakuDAO = new BahanBakuDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Cek sesi login dan role
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"gudang".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);

                // Ambil bahan berdasarkan ID
                BahanBaku bahan = bahanBakuDAO.getBahanById(id);
                if (bahan != null) {
                    // Hapus bahan
                    bahanBakuDAO.deleteBahan(id);
                    session.setAttribute("success", "Bahan '" + bahan.getNamaBahan() + "' berhasil dihapus.");
                } else {
                    session.setAttribute("error", "Data bahan tidak ditemukan.");
                }

            } catch (NumberFormatException e) {
                session.setAttribute("error", "ID bahan tidak valid.");
            } catch (SQLException e) {
                session.setAttribute("error", "Terjadi kesalahan database: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            session.setAttribute("error", "ID bahan tidak valid.");
        }

        // Redirect ke layout dengan halaman gudang/index.jsp
        response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=gudang/index.jsp&status=hapus");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
