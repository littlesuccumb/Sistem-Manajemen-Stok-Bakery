package controller.gudang;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.BahanBakuDAO;
import model.BahanBaku;
import model.User;

@WebServlet({"/gudang/tambah-bahan", "/gudang/edit-bahan", "/gudang/FormBahan"})
public class BahanBakuServlet extends HttpServlet {
    private BahanBakuDAO bahanBakuDAO;

    @Override
    public void init() {
        bahanBakuDAO = new BahanBakuDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"gudang".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        boolean isEdit = (idParam != null && !idParam.trim().isEmpty());

        if (isEdit) {
            try {
                int id = Integer.parseInt(idParam);
                BahanBaku bahan = bahanBakuDAO.getBahanById(id);
                if (bahan != null) {
                    request.setAttribute("bahan", bahan);
                    request.setAttribute("isEdit", true);
                } else {
                    request.setAttribute("error", "Data bahan tidak ditemukan.");
                }
            } catch (NumberFormatException | SQLException e) {
                request.setAttribute("error", "Kesalahan: " + e.getMessage());
            }
        }

        // Forward ke layout.jsp sambil membawa FormBahan.jsp sebagai isi kontennya
        request.setAttribute("page", "/view/gudang/FormBahan.jsp");
        request.getRequestDispatcher("/view/layout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"gudang".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");
        boolean isEdit = (idParam != null && !idParam.trim().isEmpty());

        String namaBahan = request.getParameter("namaBahan");
        if (namaBahan == null || namaBahan.isEmpty()) {
            namaBahan = request.getParameter("nama_bahan");
        }

        String stokStr = request.getParameter("stok");
        if (stokStr == null || stokStr.isEmpty()) {
            stokStr = request.getParameter("jumlah");
        }

        String satuan = request.getParameter("satuan");

        String hargaSatuanStr = request.getParameter("hargaSatuan");
        if (hargaSatuanStr == null || hargaSatuanStr.isEmpty()) {
            hargaSatuanStr = request.getParameter("harga_satuan");
        }

        String tanggalMasukStr = request.getParameter("tanggalMasuk");
        if (tanggalMasukStr == null || tanggalMasukStr.isEmpty()) {
            tanggalMasukStr = request.getParameter("tanggal_masuk");
        }

        if (tanggalMasukStr == null || tanggalMasukStr.isEmpty()) {
            tanggalMasukStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }

        try {
            if (namaBahan == null || namaBahan.trim().isEmpty()) {
                throw new IllegalArgumentException("Nama bahan tidak boleh kosong.");
            }

            if (stokStr == null || stokStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Stok tidak boleh kosong.");
            }

            if (satuan == null || satuan.trim().isEmpty()) {
                throw new IllegalArgumentException("Satuan harus dipilih.");
            }

            int stok = Integer.parseInt(stokStr.trim());
            int hargaSatuan = (hargaSatuanStr == null || hargaSatuanStr.trim().isEmpty())
                    ? 0 : Integer.parseInt(hargaSatuanStr.trim());

            if (stok < 0 || hargaSatuan < 0) {
                throw new IllegalArgumentException("Stok atau harga tidak boleh negatif.");
            }

            Date tanggalMasuk = new SimpleDateFormat("yyyy-MM-dd").parse(tanggalMasukStr.trim());

            boolean success;
            String message;

            if (isEdit) {
                int id = Integer.parseInt(idParam);
                if (bahanBakuDAO.isBahanExists(namaBahan.trim(), id)) {
                    throw new IllegalArgumentException("Nama bahan sudah digunakan.");
                }
                BahanBaku bahan = new BahanBaku(id, namaBahan.trim(), stok, satuan, hargaSatuan, tanggalMasuk);
                success = bahanBakuDAO.updateBahan(bahan);
                message = "ubah";
            } else {
                if (bahanBakuDAO.isBahanExists(namaBahan.trim())) {
                    throw new IllegalArgumentException("Nama bahan sudah digunakan.");
                }
                BahanBaku bahan = new BahanBaku(namaBahan.trim(), stok, satuan, hargaSatuan, tanggalMasuk);
                success = bahanBakuDAO.createBahan(bahan);
                message = "tambah";
            }

            if (success) {
                // Redirect ke halaman index bahan dengan layout
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=/view/gudang/index.jsp&status=" + message);
            } else {
                request.setAttribute("error", "Gagal menyimpan data bahan.");
                loadFormData(request, isEdit);
                request.setAttribute("page", "/view/gudang/FormBahan.jsp");
                request.getRequestDispatcher("/view/layout.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            loadFormData(request, isEdit);
            request.setAttribute("page", "/view/gudang/FormBahan.jsp");
            request.getRequestDispatcher("/view/layout.jsp").forward(request, response);
        }
    }

    private void loadFormData(HttpServletRequest request, boolean isEdit) {
        request.setAttribute("namaBahan", request.getParameter("namaBahan"));
        request.setAttribute("stok", request.getParameter("stok"));
        request.setAttribute("satuan", request.getParameter("satuan"));
        request.setAttribute("hargaSatuan", request.getParameter("hargaSatuan"));
        request.setAttribute("tanggalMasuk", request.getParameter("tanggalMasuk"));

        if (isEdit) {
            request.setAttribute("id", request.getParameter("id"));
            request.setAttribute("isEdit", true);
        }
    }
}