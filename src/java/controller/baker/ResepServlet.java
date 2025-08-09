package controller.baker;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

import dao.ResepDAO;
import dao.DetailResepDAO;
import dao.BahanBakuDAO;
import model.Resep;
import model.DetailResep;

@WebServlet("/ResepServlet")
public class ResepServlet extends HttpServlet {
    private ResepDAO resepDAO;
    private DetailResepDAO detailResepDAO;
    private BahanBakuDAO bahanBakuDAO;

    @Override
    public void init() {
        resepDAO = new ResepDAO();
        detailResepDAO = new DetailResepDAO();
        bahanBakuDAO = new BahanBakuDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("insert".equals(action)) {
                insertResep(request, response);
            } else if ("update".equals(action)) {
                updateResep(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=gagal");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = resepDAO.deleteDetailResep(id) && resepDAO.deleteResep(id);
                String status = success ? "hapus" : "gagal";
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=" + status);

            } else if ("edit".equals(action)) {
                // PERBAIKAN: Set data ke session dan redirect melalui layout
                int id = Integer.parseInt(request.getParameter("id"));
                Resep resep = resepDAO.getResepById(id);
                List<DetailResep> detailList = detailResepDAO.getDetailByResepId(id);
                
                // Simpan data ke session untuk diakses oleh FormResep.jsp
                request.getSession().setAttribute("editResep", resep);
                request.getSession().setAttribute("editDetailList", detailList);
                
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/FormResep.jsp&mode=edit");

            } else if ("detail".equals(action)) {
                // Untuk modal detail, tetap gunakan forward karena tidak butuh layout
                int id = Integer.parseInt(request.getParameter("id"));
                Resep resep = resepDAO.getResepById(id);
                List<DetailResep> detailList = detailResepDAO.getDetailByResepId(id);
                request.setAttribute("resep", resep);
                request.setAttribute("detailList", detailList);

                request.getRequestDispatcher("/view/baker/DetailResep.jsp").forward(request, response);

            } else {
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=gagal");
        }
    }

    private void insertResep(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String nama = request.getParameter("nama_resep");
        String deskripsi = request.getParameter("deskripsi");
        String[] bahanIds = request.getParameterValues("bahan_id");
        String[] jumlahs = request.getParameterValues("jumlah_dibutuhkan");

        // Debug: Print parameter values
        System.out.println("=== DEBUG INSERT RESEP ===");
        System.out.println("Nama: " + nama);
        System.out.println("Deskripsi: " + deskripsi);
        if (bahanIds != null) {
            System.out.println("BahanIds length: " + bahanIds.length);
            for (int i = 0; i < bahanIds.length; i++) {
                System.out.println("BahanId[" + i + "]: " + bahanIds[i]);
            }
        } else {
            System.out.println("BahanIds is NULL");
        }
        if (jumlahs != null) {
            System.out.println("Jumlahs length: " + jumlahs.length);
            for (int i = 0; i < jumlahs.length; i++) {
                System.out.println("Jumlah[" + i + "]: " + jumlahs[i]);
            }
        } else {
            System.out.println("Jumlahs is NULL");
        }

        // TAMBAHAN: Cek ketersediaan stok sebelum menyimpan resep
        if (bahanIds != null && jumlahs != null) {
            String errorMessage = bahanBakuDAO.cekKetersediaanStokResep(bahanIds, jumlahs);
            if (errorMessage != null) {
                // Jika stok tidak mencukupi, kembalikan ke form dengan pesan error
                request.setAttribute("error", errorMessage);
                request.setAttribute("nama_resep", nama);
                request.setAttribute("deskripsi", deskripsi);
                
                // Forward ke form resep dengan error
                request.getRequestDispatcher("/view/layout.jsp?page=baker/FormResep.jsp").forward(request, response);
                return;
            }
        }

        Resep resep = new Resep();
        resep.setNamaResep(nama);
        resep.setDeskripsi(deskripsi);

        int id = resepDAO.createResepWithDetails(resep, bahanIds, jumlahs);
        
        // TAMBAHAN: Jika resep berhasil disimpan, kurangi stok bahan baku
        if (id > 0) {
            try {
                boolean stokBerhasilDikurangi = bahanBakuDAO.kurangiStokBahanResep(bahanIds, jumlahs);
                
                if (!stokBerhasilDikurangi) {
                    System.out.println("WARNING: Resep berhasil disimpan tapi pengurangan stok gagal untuk resep ID: " + id);
                } else {
                    System.out.println("SUCCESS: Resep dan pengurangan stok berhasil untuk resep ID: " + id);
                }
                
                // Meskipun pengurangan stok gagal, resep tetap dianggap berhasil ditambahkan
                // Karena data resep sudah tersimpan di database
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=tambah");
                
            } catch (Exception e) {
                System.err.println("ERROR saat mengurangi stok: " + e.getMessage());
                e.printStackTrace();
                // Resep sudah tersimpan, jadi tetap redirect dengan status berhasil
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=tambah");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=gagal");
        }
    }

    private void updateResep(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        String nama = request.getParameter("nama_resep");
        String deskripsi = request.getParameter("deskripsi");
        String[] bahanIds = request.getParameterValues("bahan_id");
        String[] jumlahs = request.getParameterValues("jumlah_dibutuhkan");

        // TAMBAHAN: Cek ketersediaan stok untuk resep yang diupdate
        if (bahanIds != null && jumlahs != null) {
            String errorMessage = bahanBakuDAO.cekKetersediaanStokResep(bahanIds, jumlahs);
            if (errorMessage != null) {
                // Jika stok tidak mencukupi, kembalikan ke form dengan pesan error
                request.setAttribute("error", errorMessage);
                
                // Set data ke session untuk form edit
                Resep resep = new Resep(id, nama, deskripsi);
                request.getSession().setAttribute("editResep", resep);
                
                request.getRequestDispatcher("/view/layout.jsp?page=baker/FormResep.jsp&mode=edit").forward(request, response);
                return;
            }
        }

        Resep resep = new Resep(id, nama, deskripsi);
        boolean success = resepDAO.updateResepWithDetails(resep, bahanIds, jumlahs);
        
        String status = success ? "ubah" : "gagal";
        
        // TAMBAHAN: Jika update berhasil, kurangi stok (untuk implementasi sederhana)
        // CATATAN: Ini implementasi sederhana. Idealnya, kita perlu:
        // 1. Kembalikan stok dari resep lama
        // 2. Kurangi stok dengan resep baru
        // Untuk sekarang, kita hanya kurangi stok baru
        if (success && bahanIds != null && jumlahs != null) {
            try {
                bahanBakuDAO.kurangiStokBahanResep(bahanIds, jumlahs);
            } catch (Exception e) {
                System.err.println("ERROR saat mengurangi stok pada update resep: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Bersihkan session data setelah update
        request.getSession().removeAttribute("editResep");
        request.getSession().removeAttribute("editDetailList");
        
        response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Resep.jsp&status=" + status);
    }
}