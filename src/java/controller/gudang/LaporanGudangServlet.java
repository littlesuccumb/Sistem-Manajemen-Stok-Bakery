package controller.gudang;

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

import dao.BahanBakuDAO;
import model.BahanBaku;

@WebServlet("/gudang/laporan")
public class LaporanGudangServlet extends HttpServlet {
    private BahanBakuDAO bahanBakuDAO;

    @Override
    public void init() {
        bahanBakuDAO = new BahanBakuDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String filterWaktu = request.getParameter("filterWaktu");
        String tanggalMulai = request.getParameter("tanggalMulai");
        String tanggalAkhir = request.getParameter("tanggalAkhir");

        List<BahanBaku> laporanData = null;
        String errorMessage = null;

        try {
            if (filterWaktu == null || filterWaktu.isEmpty() || filterWaktu.equals("all")) {
                laporanData = bahanBakuDAO.getAllBahan();
            } else {
                switch (filterWaktu) {
                    case "mingguan":
                        Date akhirMinggu = new Date();
                        Date mulaiMinggu = new Date(akhirMinggu.getTime() - (7L * 24 * 60 * 60 * 1000));
                        laporanData = bahanBakuDAO.getLaporanByPeriode(mulaiMinggu, akhirMinggu);
                        break;

                    case "bulanan":
                        Date akhirBulan = new Date();
                        Date mulaiBulan = new Date(akhirBulan.getTime() - (30L * 24 * 60 * 60 * 1000));
                        laporanData = bahanBakuDAO.getLaporanByPeriode(mulaiBulan, akhirBulan);
                        break;

                    case "kustom":
                        if (tanggalMulai != null && !tanggalMulai.isEmpty()
                                && tanggalAkhir != null && !tanggalAkhir.isEmpty()) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date mulaiKustom = dateFormat.parse(tanggalMulai);
                            Date akhirKustom = dateFormat.parse(tanggalAkhir);
                            laporanData = bahanBakuDAO.getLaporanByPeriode(mulaiKustom, akhirKustom);
                        } else {
                            errorMessage = "Tanggal mulai dan akhir harus diisi untuk filter kustom";
                            laporanData = bahanBakuDAO.getAllBahan();
                        }
                        break;

                    default:
                        errorMessage = "Filter waktu tidak valid.";
                        laporanData = bahanBakuDAO.getAllBahan();
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage = "Kesalahan database: " + e.getMessage();
        } catch (ParseException e) {
            errorMessage = "Format tanggal tidak valid! Gunakan format YYYY-MM-DD";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Terjadi kesalahan sistem: " + e.getMessage();
        }

        // ✅ Kirim data ke JSP
        request.setAttribute("laporanData", laporanData);
        request.setAttribute("filterWaktu", filterWaktu);
        request.setAttribute("tanggalMulai", tanggalMulai);
        request.setAttribute("tanggalAkhir", tanggalAkhir);
        request.setAttribute("error", errorMessage);

        // ✅ Gunakan layout.jsp dan include laporanGudang.jsp sebagai isi
        request.setAttribute("page", "gudang/laporanGudang.jsp");
        request.getRequestDispatcher("/view/layout.jsp").forward(request, response);
    
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // ⬅️ Agar tombol "Terapkan" tetap bisa pakai method POST
    }
}
