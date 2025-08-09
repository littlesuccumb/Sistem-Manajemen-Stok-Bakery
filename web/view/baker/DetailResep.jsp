<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.DetailResep, model.Resep, model.Produksi" %>
<%@ page import="dao.DetailResepDAO, dao.ResepDAO, dao.ProduksiDAO" %>
<%@ page import="java.util.*" %>

<%
    String idStr = request.getParameter("id");
    int id = 0;

    String asal = request.getParameter("asal"); // asal=produksi atau resep
    String namaProduk = null;
    Resep resep = null;
    List<DetailResep> detailList = null;
    String error = null;

    try {
        id = Integer.parseInt(idStr);

        ResepDAO resepDAO = new ResepDAO();
        DetailResepDAO detailDAO = new DetailResepDAO();

        if ("produksi".equals(asal)) {
            ProduksiDAO produksiDAO = new ProduksiDAO();
            Produksi produksi = produksiDAO.getProduksiById(id);
            if (produksi != null) {
                namaProduk = produksi.getNamaProduk();
                resep = resepDAO.getResepById(produksi.getResepId());
                detailList = detailDAO.getDetailByResepId(produksi.getResepId());
            } else {
                error = "Data produksi tidak ditemukan.";
            }
        } else {
            resep = resepDAO.getResepById(id);
            detailList = detailDAO.getDetailByResepId(id);
        }
    } catch (Exception e) {
        error = "Terjadi kesalahan: " + e.getMessage();
    }
%>

<div class="modal-overlay" onclick="closeModal()"></div>
<div class="modal-card">
  <div class="modal-header">
    <h2>Detail Resep</h2>
    <button onclick="closeModal()" class="close-btn"><i class='fas fa-times'></i></button>
  </div>
  <hr>
  <div class="modal-body">
    <% if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } else if (resep != null) { %>
        <% if (namaProduk != null) { %>
            <p><strong>Nama Produk:</strong> <%= namaProduk %></p>
        <% } %>
        <p><strong>Nama Resep:</strong> <%= resep.getNamaResep() %></p>
        <p><strong>Deskripsi:</strong> 
            <% if (resep.getDeskripsi() != null && !resep.getDeskripsi().isEmpty()) { %>
                <%= resep.getDeskripsi() %>
            <% } else { %>
                <span style="color:gray;"><i>Belum ada deskripsi.</i></span>
            <% } %>
        </p>

        <p><strong>Bahan Baku Digunakan:</strong></p>
        <ul style="padding-left: 18px;">
            <% if (detailList != null && !detailList.isEmpty()) {
                for (DetailResep d : detailList) { %>
                    <li><%= d.getNamaBahan() %> - <%= d.getJumlahDibutuhkan() %> <%= d.getSatuan() %></li>
            <%  }
               } else { %>
                <li style="color:gray;"><i>Tidak ada data bahan baku.</i></li>
            <% } %>
        </ul>
    <% } else { %>
        <p style="color:red;">Data resep tidak ditemukan.</p>
    <% } %>
  </div>
</div>
