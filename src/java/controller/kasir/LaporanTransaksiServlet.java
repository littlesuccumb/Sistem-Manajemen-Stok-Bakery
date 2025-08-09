package controller.kasir;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.PenjualanDAO;
import model.DetailPenjualan;
import model.Penjualan;
import model.User;

@WebServlet({"/kasir/laporan", "/kasir/laporan/detail", "/kasir/laporan/edit", "/kasir/laporan/hapus"})
public class LaporanTransaksiServlet extends HttpServlet {
    private PenjualanDAO penjualanDAO;

    @Override
    public void init() {
        penjualanDAO = new PenjualanDAO();
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
            switch (servletPath) {
                case "/kasir/laporan":
                    showLaporanList(request, response);
                    break;
                case "/kasir/laporan/detail":
                    showDetailTransaksi(request, response);
                    break;
                case "/kasir/laporan/edit":
                    editTransaksi(request, response);
                    break;
                case "/kasir/laporan/hapus":
                    hapusTransaksi(request, response);
                    break;
                default:
                    showLaporanList(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Terjadi kesalahan database: " + e.getMessage());
            request.getRequestDispatcher("/view/layout.jsp?page=kasir/LaporanTransaksi.jsp").forward(request, response);
        }
    }

    private void showLaporanList(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String tanggalMulai = request.getParameter("tanggalMulai");
        String tanggalAkhir = request.getParameter("tanggalAkhir");
        String search = request.getParameter("search");
        String filterWaktu = request.getParameter("filterWaktu") != null ? request.getParameter("filterWaktu") : "all";

        List<Penjualan> penjualanList;

        if (search != null && !search.trim().isEmpty()) {
            penjualanList = penjualanDAO.searchPenjualan(search.trim());
        } else if ("mingguan".equals(filterWaktu)) {
            Date endDate = new Date();
            Date startDate = new Date(endDate.getTime() - (7L * 24 * 60 * 60 * 1000));
            penjualanList = penjualanDAO.getPenjualanByPeriode(startDate, endDate);
        } else if ("bulanan".equals(filterWaktu)) {
            Date endDate = new Date();
            Date startDate = new Date(endDate.getTime() - (30L * 24 * 60 * 60 * 1000));
            penjualanList = penjualanDAO.getPenjualanByPeriode(startDate, endDate);
        } else if ("kustom".equals(filterWaktu) && tanggalMulai != null && tanggalAkhir != null &&
                   !tanggalMulai.isEmpty() && !tanggalAkhir.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(tanggalMulai);
                Date endDate = sdf.parse(tanggalAkhir);
                penjualanList = penjualanDAO.getPenjualanByPeriode(startDate, endDate);
            } catch (ParseException e) {
                request.setAttribute("error", "Format tanggal tidak valid");
                penjualanList = penjualanDAO.getAllPenjualan();
            }
        } else {
            penjualanList = penjualanDAO.getAllPenjualan();
        }

        int totalPenjualan = 0;
        for (Penjualan p : penjualanList) {
            totalPenjualan += p.getTotalHarga();
        }

        request.setAttribute("penjualanList", penjualanList);
        request.setAttribute("totalPenjualan", totalPenjualan);
        request.setAttribute("jumlahTransaksi", penjualanList.size());

        request.setAttribute("tanggalMulai", tanggalMulai);
        request.setAttribute("tanggalAkhir", tanggalAkhir);
        request.setAttribute("filterWaktu", filterWaktu);
        request.setAttribute("searchQuery", search);

        request.getRequestDispatcher("/view/layout.jsp?page=kasir/LaporanTransaksi.jsp").forward(request, response);
    }

    private void showDetailTransaksi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/kasir/laporan");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Penjualan transaksi = penjualanDAO.getPenjualanById(id);
            List<DetailPenjualan> detailList = penjualanDAO.getDetailPenjualanByPenjualanId(id);

            if (transaksi == null) {
                request.setAttribute("error", "Transaksi tidak ditemukan");
                response.sendRedirect(request.getContextPath() + "/kasir/laporan");
                return;
            }

            request.setAttribute("transaksi", transaksi);
            request.setAttribute("detailList", detailList);
            request.getRequestDispatcher("/view/layout.jsp?page=kasir/DetailTransaksi.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/kasir/laporan");
        }
    }

    private void editTransaksi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/kasir/laporan");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/kasir/FormTransaksi?id=" + idParam);
    }

    private void hapusTransaksi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/kasir/laporan");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Penjualan penjualan = penjualanDAO.getPenjualanById(id);
            List<DetailPenjualan> detailList = penjualanDAO.getDetailPenjualanByPenjualanId(id);

            if (penjualan != null && detailList != null) {
                boolean success = penjualanDAO.deletePenjualan(id);

                if (success) {
                    String successMsg = "Transaksi " + penjualan.getKodeTransaksi() + " berhasil dihapus";
                    response.sendRedirect(request.getContextPath() + "/kasir/laporan?success=" +
                            java.net.URLEncoder.encode(successMsg, "UTF-8"));
                } else {
                    String errorMsg = "Gagal menghapus transaksi";
                    response.sendRedirect(request.getContextPath() + "/kasir/laporan?error=" +
                            java.net.URLEncoder.encode(errorMsg, "UTF-8"));
                }
            } else {
                String errorMsg = "Transaksi tidak ditemukan";
                response.sendRedirect(request.getContextPath() + "/kasir/laporan?error=" +
                        java.net.URLEncoder.encode(errorMsg, "UTF-8"));
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/kasir/laporan");
        }
    }
}
