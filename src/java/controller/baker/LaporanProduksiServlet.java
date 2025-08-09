package controller.baker;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dao.ProduksiDAO;
import model.Produksi;
import model.User;

@WebServlet("/baker/laporan")
public class LaporanProduksiServlet extends HttpServlet {
    private ProduksiDAO produksiDAO;

    @Override
    public void init() throws ServletException {
        produksiDAO = new ProduksiDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Cek session user
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null || !"baker".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/auth.jsp");
            return;
        }

        try {
            // Debug parameters
            System.out.println("=== LAPORAN PRODUKSI DEBUG ===");
            System.out.println("Filter Waktu: " + request.getParameter("filterWaktu"));
            System.out.println("Tanggal Mulai: " + request.getParameter("tanggalMulai"));
            System.out.println("Tanggal Akhir: " + request.getParameter("tanggalAkhir"));
            
            // Ambil parameter filter
            String filterWaktu = request.getParameter("filterWaktu");
            String tanggalMulaiStr = request.getParameter("tanggalMulai");
            String tanggalAkhirStr = request.getParameter("tanggalAkhir");
            
            // Set default filter jika kosong
            if (filterWaktu == null || filterWaktu.isEmpty()) {
                filterWaktu = "all";
            }
            
            List<Produksi> laporanProduksi = null;
            Date tanggalMulai = null;
            Date tanggalAkhir = null;
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            
            switch (filterWaktu) {
                case "mingguan":
                    // Minggu ini (Senin - Minggu)
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    tanggalMulai = cal.getTime();
                    cal.add(Calendar.DAY_OF_WEEK, 6);
                    tanggalAkhir = cal.getTime();
                    System.out.println("Filter Mingguan: " + sdf.format(tanggalMulai) + " s/d " + sdf.format(tanggalAkhir));
                    break;
                    
                case "bulanan":
                    // Bulan ini
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    tanggalMulai = cal.getTime();
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    tanggalAkhir = cal.getTime();
                    System.out.println("Filter Bulanan: " + sdf.format(tanggalMulai) + " s/d " + sdf.format(tanggalAkhir));
                    break;
                    
                case "kustom":
                    // Tanggal kustom
                    if (tanggalMulaiStr != null && !tanggalMulaiStr.isEmpty() &&
                        tanggalAkhirStr != null && !tanggalAkhirStr.isEmpty()) {
                        try {
                            tanggalMulai = sdf.parse(tanggalMulaiStr);
                            tanggalAkhir = sdf.parse(tanggalAkhirStr);
                            System.out.println("Filter Kustom: " + sdf.format(tanggalMulai) + " s/d " + sdf.format(tanggalAkhir));
                        } catch (ParseException e) {
                            request.setAttribute("error", "Format tanggal tidak valid!");
                            System.out.println("Error parsing tanggal: " + e.getMessage());
                        }
                    } else {
                        request.setAttribute("error", "Silakan isi tanggal mulai dan tanggal akhir!");
                    }
                    break;
                    
                default: // "all"
                    System.out.println("Filter: Semua data");
                    break;
            }
            
            // Ambil data berdasarkan filter
            if (tanggalMulai != null && tanggalAkhir != null) {
                laporanProduksi = produksiDAO.getLaporanProduksiByPeriode(tanggalMulai, tanggalAkhir);
            } else if ("all".equals(filterWaktu)) {
                laporanProduksi = produksiDAO.getAllProduksi();
            }
            
            System.out.println("Jumlah data ditemukan: " + (laporanProduksi != null ? laporanProduksi.size() : 0));
            
            // Set attributes untuk JSP
            request.setAttribute("laporanProduksi", laporanProduksi);
            request.setAttribute("filterWaktu", filterWaktu);
            
            if (tanggalMulai != null) {
                request.setAttribute("tanggalMulai", sdf.format(tanggalMulai));
            } else {
                request.setAttribute("tanggalMulai", tanggalMulaiStr);
            }
            
            if (tanggalAkhir != null) {
                request.setAttribute("tanggalAkhir", sdf.format(tanggalAkhir));
            } else {
                request.setAttribute("tanggalAkhir", tanggalAkhirStr);
            }
            
            // Forward ke JSP melalui layout
            request.getRequestDispatcher("/view/layout.jsp?page=baker/LaporanProduksi.jsp")
                   .forward(request, response);
                   
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Terjadi kesalahan saat mengambil data: " + e.getMessage());
            request.getRequestDispatcher("/view/layout.jsp?page=baker/LaporanProduksi.jsp")
                   .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}