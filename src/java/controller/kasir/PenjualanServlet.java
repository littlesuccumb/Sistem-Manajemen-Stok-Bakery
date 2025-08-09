package controller.kasir;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.PenjualanDAO;
import dao.DetailPenjualanDAO;
import dao.ProduksiDAO;
import model.Penjualan;
import model.DetailPenjualan;
import model.Produksi;
import model.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;

@WebServlet("/PenjualanServlet")
public class PenjualanServlet extends HttpServlet {
    private PenjualanDAO penjualanDAO;
    private DetailPenjualanDAO detailDAO;
    private ProduksiDAO produksiDAO;

    @Override
    public void init() {
        penjualanDAO = new PenjualanDAO();
        detailDAO = new DetailPenjualanDAO();
        produksiDAO = new ProduksiDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("form".equals(action)) {
                handleForm(request, response);
            } else if ("detail".equals(action)) {
                handleDetail(request, response);
            } else if ("laporan".equals(action)) {
                handleLaporan(request, response);
            } else if ("delete".equals(action)) {
                handleDelete(request, response);
            } else {
                // Redirect ke layout dengan halaman kasir index
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=kasir/index.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=kasir/index.jsp&status=gagal");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                handleCreateTransaksi(request, response);
            } else if ("update".equals(action)) {
                handleUpdateTransaksi(request, response);
            } else if ("laporan".equals(action)) {
                handleGenerateLaporan(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                request.setAttribute("error", "Gagal memproses transaksi: " + e.getMessage());
                List<Produksi> produkList = produksiDAO.getAllProduksi();
                String kodeTransaksi = penjualanDAO.generateKodeTransaksi();
                request.setAttribute("produkList", produkList);
                request.setAttribute("kodeTransaksi", kodeTransaksi);
                request.setAttribute("page", "kasir/FormTransaksi.jsp");

                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/layout.jsp");
                dispatcher.forward(request, response);
            } catch (SQLException sqlEx) {
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=kasir/index.jsp&status=gagal");
            }
        }
    }

    private void handleForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            List<Produksi> produkList = produksiDAO.getAllProduksi();
            String kodeTransaksi = penjualanDAO.generateKodeTransaksi();

            request.setAttribute("produkList", produkList);
            request.setAttribute("kodeTransaksi", kodeTransaksi);
            request.setAttribute("page", "kasir/FormTransaksi.jsp");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/view/layout.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil data produk: " + e.getMessage(), e);
        }
    }

    private void handleCreateTransaksi(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                throw new Exception("Session expired. Silakan login ulang.");
            }

            String metodePembayaran = request.getParameter("metodePembayaran");
            String[] produksiIds = request.getParameterValues("produkId");
            String[] jumlahArray = request.getParameterValues("jumlah");

            if (produksiIds == null || jumlahArray == null || produksiIds.length != jumlahArray.length) {
                throw new Exception("Data produk tidak lengkap atau tidak valid");
            }

            List<DetailPenjualan> detailList = new ArrayList<>();
            int totalHarga = 0;

            for (int i = 0; i < produksiIds.length; i++) {
                if (produksiIds[i] == null || produksiIds[i].isEmpty() || 
                    jumlahArray[i] == null || jumlahArray[i].isEmpty()) {
                    continue; // Skip empty entries
                }
                
                int produksiId = Integer.parseInt(produksiIds[i]);
                int jumlah = Integer.parseInt(jumlahArray[i]);

                if (jumlah <= 0) continue; // Skip invalid quantities

                Produksi produk = produksiDAO.getProduksiById(produksiId);
                if (produk == null) {
                    throw new Exception("Produk dengan ID " + produksiId + " tidak ditemukan");
                }

                if (produk.getJumlahProduksi() < jumlah) {
                    throw new Exception("Stok " + produk.getNamaProduk() + " tidak mencukupi! " +
                            "Tersedia: " + produk.getJumlahProduksi() +
                            ", Diminta: " + jumlah);
                }

                int subtotal = produk.getHargaJual() * jumlah;
                totalHarga += subtotal;

                detailList.add(new DetailPenjualan(0, produksiId, jumlah, produk.getHargaJual(), subtotal));
            }

            if (detailList.isEmpty()) {
                throw new Exception("Tidak ada produk yang valid untuk diproses");
            }

            String kodeTransaksi = penjualanDAO.generateKodeTransaksi();
            Penjualan penjualan = new Penjualan(kodeTransaksi, new Date(), totalHarga, metodePembayaran, user.getId());
            int penjualanId = penjualanDAO.createPenjualan(penjualan);

            if (penjualanId > 0) {
                for (DetailPenjualan detail : detailList) {
                    detail.setPenjualanId(penjualanId);
                    detailDAO.createDetail(detail);
                    produksiDAO.kurangiStokProduksi(detail.getProduksiId(), detail.getJumlah());
                }

                request.getSession().setAttribute("lastTransaction", penjualanId);
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=kasir/index.jsp&status=berhasil&kode=" + kodeTransaksi);
            } else {
                throw new Exception("Gagal menyimpan transaksi");
            }
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage(), e);
        }
    }

    private void handleUpdateTransaksi(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                throw new Exception("Session expired. Silakan login ulang.");
            }

            int penjualanId = Integer.parseInt(request.getParameter("id"));
            String metodePembayaran = request.getParameter("metodePembayaran");
            String[] produksiIds = request.getParameterValues("produkId");
            String[] jumlahArray = request.getParameterValues("jumlah");

            // Get existing transaction
            Penjualan existingPenjualan = penjualanDAO.getPenjualanById(penjualanId);
            if (existingPenjualan == null) {
                throw new Exception("Transaksi tidak ditemukan");
            }

            // Restore stock from old transaction
            List<DetailPenjualan> oldDetails = detailDAO.getDetailByPenjualanId(penjualanId);
            for (DetailPenjualan detail : oldDetails) {
                produksiDAO.tambahStokProduksi(detail.getProduksiId(), detail.getJumlah());
            }

            // Delete old details
            detailDAO.deleteByPenjualanId(penjualanId);

            // Process new details
            List<DetailPenjualan> newDetailList = new ArrayList<>();
            int totalHarga = 0;

            for (int i = 0; i < produksiIds.length; i++) {
                if (produksiIds[i] == null || produksiIds[i].isEmpty() || 
                    jumlahArray[i] == null || jumlahArray[i].isEmpty()) {
                    continue;
                }
                
                int produksiId = Integer.parseInt(produksiIds[i]);
                int jumlah = Integer.parseInt(jumlahArray[i]);

                if (jumlah <= 0) continue;

                Produksi produk = produksiDAO.getProduksiById(produksiId);
                if (produk == null) {
                    throw new Exception("Produk dengan ID " + produksiId + " tidak ditemukan");
                }

                if (produk.getJumlahProduksi() < jumlah) {
                    throw new Exception("Stok " + produk.getNamaProduk() + " tidak mencukupi! " +
                            "Tersedia: " + produk.getJumlahProduksi() +
                            ", Diminta: " + jumlah);
                }

                int subtotal = produk.getHargaJual() * jumlah;
                totalHarga += subtotal;

                newDetailList.add(new DetailPenjualan(0, produksiId, jumlah, produk.getHargaJual(), subtotal));
            }

            if (newDetailList.isEmpty()) {
                throw new Exception("Tidak ada produk yang valid untuk diproses");
            }

            // Update penjualan
            existingPenjualan.setTotalHarga(totalHarga);
            existingPenjualan.setMetodePembayaran(metodePembayaran);
            penjualanDAO.updatePenjualan(existingPenjualan);

            // Create new details
            for (DetailPenjualan detail : newDetailList) {
                detail.setPenjualanId(penjualanId);
                detailDAO.createDetail(detail);
                produksiDAO.kurangiStokProduksi(detail.getProduksiId(), detail.getJumlah());
            }

            response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=kasir/index.jsp&status=update&kode=" + existingPenjualan.getKodeTransaksi());
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage(), e);
        }
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            Penjualan penjualan = penjualanDAO.getPenjualanById(id);
            List<DetailPenjualan> detailList = detailDAO.getDetailByPenjualanId(id);

            request.setAttribute("penjualan", penjualan);
            request.setAttribute("detailList", detailList);
            request.setAttribute("page", "kasir/DetailTransaksi.jsp");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/view/layout.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil detail transaksi: " + e.getMessage(), e);
        }
    }

    private void handleLaporan(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            // Ambil parameter filter
            String tanggalMulai = request.getParameter("tanggalMulai");
            String tanggalAkhir = request.getParameter("tanggalAkhir");
            String search = request.getParameter("search");
            String filterWaktu = request.getParameter("filterWaktu");
            
            // Set default filterWaktu jika null
            if (filterWaktu == null || filterWaktu.isEmpty()) {
                filterWaktu = "all";
            }
            
            List<Penjualan> penjualanList = null;

            // Handle search
            if (search != null && !search.trim().isEmpty()) {
                penjualanList = penjualanDAO.searchPenjualan(search.trim());
            } 
            // Handle filter waktu
            else if ("mingguan".equals(filterWaktu)) {
                Date endDate = new Date();
                Date startDate = new Date(endDate.getTime() - (7L * 24 * 60 * 60 * 1000));
                penjualanList = penjualanDAO.getPenjualanByPeriode(startDate, endDate);
            } 
            else if ("bulanan".equals(filterWaktu)) {
                Date endDate = new Date();
                Date startDate = new Date(endDate.getTime() - (30L * 24 * 60 * 60 * 1000));
                penjualanList = penjualanDAO.getPenjualanByPeriode(startDate, endDate);
            } 
            else if ("kustom".equals(filterWaktu) && tanggalMulai != null && tanggalAkhir != null &&
                     !tanggalMulai.isEmpty() && !tanggalAkhir.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date startDate = sdf.parse(tanggalMulai);
                    Date endDate = sdf.parse(tanggalAkhir);
                    penjualanList = penjualanDAO.getPenjualanByPeriode(startDate, endDate);
                } catch (Exception e) {
                    request.setAttribute("error", "Format tanggal tidak valid");
                    penjualanList = penjualanDAO.getAllPenjualan();
                }
            } 
            else {
                // Default: ambil semua data atau data hari ini untuk kasir
                User user = (User) request.getSession().getAttribute("user");
                if ("kasir".equals(user.getRole()) && "hari_ini".equals(filterWaktu)) {
                    penjualanList = penjualanDAO.getPenjualanHariIni(user.getId());
                } else {
                    penjualanList = penjualanDAO.getAllPenjualan();
                }
            }

            // Hitung total dan jumlah transaksi
            int totalPenjualan = 0;
            if (penjualanList != null) {
                for (Penjualan p : penjualanList) {
                    totalPenjualan += p.getTotalHarga();
                }
            } else {
                penjualanList = new ArrayList<>();
            }

            // Set attributes
            request.setAttribute("penjualanList", penjualanList);
            request.setAttribute("totalPenjualan", totalPenjualan);
            request.setAttribute("jumlahTransaksi", penjualanList.size());
            request.setAttribute("tanggalMulai", tanggalMulai);
            request.setAttribute("tanggalAkhir", tanggalAkhir);
            request.setAttribute("filterWaktu", filterWaktu);
            request.setAttribute("searchQuery", search);
            request.setAttribute("page", "kasir/LaporanTransaksi.jsp");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/view/layout.jsp");
            dispatcher.forward(request, response);
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil data laporan: " + e.getMessage(), e);
        }
    }

    private void handleGenerateLaporan(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            String tanggalMulaiStr = request.getParameter("tanggal_mulai");
            String tanggalAkhirStr = request.getParameter("tanggal_akhir");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date tanggalMulai = sdf.parse(tanggalMulaiStr);
            Date tanggalAkhir = sdf.parse(tanggalAkhirStr);

            List<Penjualan> penjualanList = penjualanDAO.getPenjualanByPeriode(tanggalMulai, tanggalAkhir);
            List<DetailPenjualan> produkTerlaris = detailDAO.getPenjualanProdukByPeriode(tanggalMulai, tanggalAkhir);

            int totalPenjualan = 0;
            if (penjualanList != null) {
                for (Penjualan p : penjualanList) {
                    totalPenjualan += p.getTotalHarga();
                }
            }

            request.setAttribute("penjualanList", penjualanList);
            request.setAttribute("produkTerlaris", produkTerlaris);
            request.setAttribute("totalPenjualan", totalPenjualan);
            request.setAttribute("jumlahTransaksi", penjualanList != null ? penjualanList.size() : 0);
            request.setAttribute("tanggalMulai", tanggalMulai);
            request.setAttribute("tanggalAkhir", tanggalAkhir);
            request.setAttribute("page", "kasir/LaporanTransaksi.jsp");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/view/layout.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new Exception("Gagal generate laporan: " + e.getMessage(), e);
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            List<DetailPenjualan> detailList = detailDAO.getDetailByPenjualanId(id);
            Penjualan penjualan = penjualanDAO.getPenjualanById(id);

            if (penjualan == null) {
                throw new Exception("Transaksi tidak ditemukan");
            }

            // Kembalikan stok sebelum menghapus transaksi
            for (DetailPenjualan detail : detailList) {
                produksiDAO.tambahStokProduksi(detail.getProduksiId(), detail.getJumlah());
            }

            // Hapus detail dan transaksi
            detailDAO.deleteByPenjualanId(id);
            penjualanDAO.deletePenjualan(id);

            // Redirect ke laporan dengan status hapus
            response.sendRedirect(request.getContextPath() + "/PenjualanServlet?action=laporan&status=hapus&kode=" + penjualan.getKodeTransaksi());
        } catch (SQLException e) {
            throw new Exception("Gagal menghapus transaksi: " + e.getMessage(), e);
        }
    }
}