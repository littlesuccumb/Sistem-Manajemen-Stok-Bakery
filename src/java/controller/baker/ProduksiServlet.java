package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import dao.ProduksiDAO;
import model.Produksi;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/ProduksiServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class ProduksiServlet extends HttpServlet {
    private ProduksiDAO dao;

    @Override
    public void init() {
        dao = new ProduksiDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteProduksi(id);
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Produksi.jsp&status=hapus");

            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Produksi produksi = dao.getProduksiById(id);
                request.setAttribute("produksi", produksi);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/view/baker/FormProduksi.jsp");
                dispatcher.forward(request, response);

            } else {
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Produksi.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Improved error handling
            request.setAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Produksi.jsp&status=gagal");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("insert".equals(action)) {
                Produksi produksi = extractProduksiFromRequest(request, null);
                dao.createProduksi(produksi);
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Produksi.jsp&status=tambah");

            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Produksi existing = dao.getProduksiById(id);
                Produksi updated = extractProduksiFromRequest(request, existing.getGambar());
                updated.setId(id);
                dao.updateProduksi(updated);
                response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=baker/Produksi.jsp&status=ubah");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Better error handling - forward back to form with error message
            request.setAttribute("error", "Gagal menyimpan data: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/view/baker/FormProduksi.jsp");
            dispatcher.forward(request, response);
        }
    }

    private Produksi extractProduksiFromRequest(HttpServletRequest request, String oldImageName)
        throws Exception {

        // Debug logging - bisa dihapus setelah berhasil
        System.out.println("=== DEBUG PARAMETERS ===");
        System.out.println("resep_id: " + request.getParameter("resep_id"));
        System.out.println("nama_produk: " + request.getParameter("nama_produk"));
        System.out.println("jumlah: " + request.getParameter("jumlah"));
        System.out.println("harga_jual: " + request.getParameter("harga_jual"));
        System.out.println("tanggal: " + request.getParameter("tanggal"));
        System.out.println("status: " + request.getParameter("status"));

        // Validasi parameter tidak null
        String resepIdStr = request.getParameter("resep_id");
        String namaProduk = request.getParameter("nama_produk");
        String jumlahStr = request.getParameter("jumlah");
        String hargaJualStr = request.getParameter("harga_jual");
        String tanggalStr = request.getParameter("tanggal");
        String status = request.getParameter("status");

        if (resepIdStr == null || namaProduk == null || jumlahStr == null || 
            hargaJualStr == null || tanggalStr == null || status == null) {
            throw new Exception("Ada parameter yang kosong atau null");
        }

        // Parse parameter
        int resepId = Integer.parseInt(resepIdStr);
        int jumlah = Integer.parseInt(jumlahStr);
        int hargaJual = Integer.parseInt(hargaJualStr);
        Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(tanggalStr);

        // Handle file upload
        Part filePart = request.getPart("gambar");
        String fileName = extractFileName(filePart);
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        
        // DEBUG: Print path information
        System.out.println("=== UPLOAD DEBUG ===");
        System.out.println("Context Path: " + getServletContext().getRealPath(""));
        System.out.println("Upload Path: " + uploadPath);
        System.out.println("Upload Dir exists: " + new File(uploadPath).exists());
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("Creating directory: " + created);
            if (!created) {
                throw new Exception("Tidak dapat membuat direktori upload di: " + uploadPath);
            }
        }

        String gambar;
        if (fileName != null && !fileName.isEmpty()) {
            String savedName = System.currentTimeMillis() + "_" + fileName;
            String fullPath = uploadPath + File.separator + savedName;
            
            // DEBUG: Print file information
            System.out.println("Original filename: " + fileName);
            System.out.println("Saved filename: " + savedName);
            System.out.println("Full file path: " + fullPath);
            
            filePart.write(fullPath);
            
            // Verify file was written
            File savedFile = new File(fullPath);
            System.out.println("File exists after write: " + savedFile.exists());
            System.out.println("File size: " + savedFile.length() + " bytes");
            
            gambar = savedName;
        } else {
            gambar = oldImageName != null ? oldImageName : "default.jpg";
            System.out.println("No file uploaded, using: " + gambar);
        }

        // Gunakan constructor yang sesuai dengan model Produksi
        return new Produksi(resepId, namaProduk, jumlah, hargaJual, tanggal, status, gambar);
    }

    private String extractFileName(Part part) {
        if (part == null) return null;
        
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) return null;
        
        for (String cd : contentDisposition.split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.isEmpty() ? null : fileName;
            }
        }
        return null;
    }
}