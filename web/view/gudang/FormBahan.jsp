<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.BahanBaku" %>
<%@ page import="dao.BahanBakuDAO" %>
<%@ page import="java.sql.SQLException" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"gudang".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/auth.jsp?action=login");
        return;
    }

    BahanBaku bahan = null;
    boolean isEdit = false;

    // Tangkap parameter ID dari URL jika ada
    String idParam = request.getParameter("id");
    if (idParam != null && !idParam.isEmpty()) {
        try {
            int idBahan = Integer.parseInt(idParam);
            BahanBakuDAO dao = new BahanBakuDAO();
            bahan = dao.getBahanById(idBahan);
            isEdit = (bahan != null);
        } catch (NumberFormatException | SQLException e) {
            request.setAttribute("error", "ID tidak valid atau gagal ambil data.");
        }
    }

    String pageTitle = isEdit ? "Edit Bahan Baku" : "Tambah Bahan Baku";
    String formAction = isEdit
        ? request.getContextPath() + "/gudang/edit-bahan"
        : request.getContextPath() + "/gudang/tambah-bahan";
%>



<div class="form-wrapper">
    <h2><%= pageTitle %></h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="notif error">
            <i class="fas fa-exclamation-circle"></i>
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <form action="<%= formAction %>" method="post">
        <% if (isEdit && bahan != null) { %>
            <input type="hidden" name="id" value="<%= bahan.getId() %>">
        <% } %>

        <div class="form-group">
            <label for="namaBahan">Nama Bahan:</label>
            <input type="text" name="namaBahan" id="namaBahan"
                   value="<%= isEdit && bahan != null ? bahan.getNamaBahan() : (request.getAttribute("namaBahan") != null ? request.getAttribute("namaBahan") : "") %>" 
                   required>
        </div>

        <div class="form-group">
            <label for="jumlah">Stok:</label>
            <input type="number" name="jumlah" id="jumlah"
                   value="<%= isEdit && bahan != null ? bahan.getJumlah() : (request.getAttribute("stok") != null ? request.getAttribute("stok") : "") %>"
                   required min="0">
            <small>Masukkan jumlah stok (angka positif)</small>
        </div>

        <div class="form-group">
            <label for="satuan">Satuan:</label>
            <select name="satuan" id="satuan" required>
                <option value="">-- Pilih Satuan --</option>
                <% 
                String currentSatuan = "";
                if (isEdit && bahan != null) {
                    currentSatuan = bahan.getSatuan();
                } else if (request.getAttribute("satuan") != null) {
                    currentSatuan = (String) request.getAttribute("satuan");
                }
                %>
                <option value="kg" <%= "kg".equals(currentSatuan) ? "selected" : "" %>>Kilogram (kg)</option>
                <option value="gram" <%= "gram".equals(currentSatuan) ? "selected" : "" %>>Gram</option>
                <option value="liter" <%= "liter".equals(currentSatuan) ? "selected" : "" %>>Liter</option>
                <option value="ml" <%= "ml".equals(currentSatuan) ? "selected" : "" %>>Mililiter (ml)</option>
                <option value="pcs" <%= "pcs".equals(currentSatuan) ? "selected" : "" %>>Pieces (pcs)</option>
                <option value="pack" <%= "pack".equals(currentSatuan) ? "selected" : "" %>>Pack</option>
                <option value="botol" <%= "botol".equals(currentSatuan) ? "selected" : "" %>>Botol</option>
                <option value="kaleng" <%= "kaleng".equals(currentSatuan) ? "selected" : "" %>>Kaleng</option>
            </select>
        </div>

        <div class="form-group">
            <label for="hargaSatuan">Harga Satuan:</label>
            <input type="number" name="hargaSatuan" id="hargaSatuan"
                   value="<%= isEdit && bahan != null ? bahan.getHargaSatuan() : (request.getAttribute("hargaSatuan") != null ? request.getAttribute("hargaSatuan") : "") %>"
                   required min="0">
            <small>Harga per satuan (Rupiah)</small>
        </div>

        <div class="form-group">
            <label for="tanggalMasuk">Tanggal Masuk:</label>
            <%
            String tanggalValue = "";
            if (isEdit && bahan != null && bahan.getTanggalMasuk() != null) {
                tanggalValue = new java.text.SimpleDateFormat("yyyy-MM-dd").format(bahan.getTanggalMasuk());
            } else if (request.getAttribute("tanggalMasuk") != null) {
                tanggalValue = (String) request.getAttribute("tanggalMasuk");
            } else {
                tanggalValue = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            }
            %>
            <input type="date" name="tanggalMasuk" id="tanggalMasuk" value="<%= tanggalValue %>" required>
        </div>

        <div class="form-button-group">
            <button type="submit" class="btn simpan"><i class="fas fa-save"></i> <%= isEdit ? "Update Bahan" : "Simpan Bahan" %></button>
            <a href= "<%= request.getContextPath() %>/view/layout.jsp?page=gudang/index.jsp"class="btn batal"><i class="fas fa-times"></i> Batal</a>
        </div>
    </form>
</div>

<script>
// Validasi form sebelum submit
const form = document.querySelector('form');
form.addEventListener('submit', function(e) {
    const namaBahan = document.querySelector('input[name="namaBahan"]').value.trim();
    const jumlah = document.querySelector('input[name="jumlah"]').value;
    const satuan = document.querySelector('select[name="satuan"]').value;
    const hargaSatuan = document.querySelector('input[name="hargaSatuan"]').value;
    const tanggalMasuk = document.querySelector('input[name="tanggalMasuk"]').value;

    if (!namaBahan) {
        alert('Nama bahan tidak boleh kosong!');
        e.preventDefault(); return;
    }
    if (!jumlah || parseInt(jumlah) < 0) {
        alert('Stok harus berupa angka positif!');
        e.preventDefault(); return;
    }
    if (!satuan) {
        alert('Silakan pilih satuan!');
        e.preventDefault(); return;
    }
    if (!hargaSatuan || parseInt(hargaSatuan) < 0) {
        alert('Harga satuan harus berupa angka positif!');
        e.preventDefault(); return;
    }
    if (!tanggalMasuk) {
        alert('Tanggal masuk harus diisi!');
        e.preventDefault(); return;
    }
});

// Auto focus ke field pertama
form.querySelector('input[name="namaBahan"]').focus();
</script>
