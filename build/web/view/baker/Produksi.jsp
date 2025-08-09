<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Produksi, dao.ProduksiDAO" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp?pesan=belumlogin");
        return;
    }

    ProduksiDAO dao = new ProduksiDAO();
    List<Produksi> daftarProduksi = dao.getAllProduksi();
    String status = request.getParameter("status");
%>

<div class="container">
    <%-- Notifikasi --%>
    <% if ("tambah".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Produksi berhasil ditambahkan.</div>
    <% } else if ("hapus".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Produksi berhasil dihapus.</div>
    <% } else if ("ubah".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Produksi berhasil diperbarui.</div>
    <% } else if ("gagal".equals(status)) { %>
        <div class="notif error"><i class="fas fa-exclamation-circle"></i> Gagal memproses data.</div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="section-title"><i class="fas fa-cookie-bite"></i> Data Produksi</h4>
        <a href="layout.jsp?page=baker/FormProduksi.jsp" class="btn tambah">
            <i class="fas fa-plus"></i> Tambah Produksi
        </a>
    </div>

    <div class="search-wrapper">
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Cari nama produk atau resep...">
        </div>
    </div>

    <div class="table-wrapper">
        <table class="table" id="produksiTable">
            <thead>
                <tr>
                    <th>No</th>
                    <th>Produk</th>
                    <th>Jumlah</th>
                    <th>Harga Jual</th>
                    <th>Resep</th>
                    <th>Tanggal</th>
                    <th>Status</th>
                    <th>Gambar</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <%
                    int no = 1;
                    if (daftarProduksi != null && !daftarProduksi.isEmpty()) {
                        for (Produksi p : daftarProduksi) {
                %>
                <tr>
                    <td><%= no++ %></td>
                    <td><%= p.getNamaProduk() %></td>
                    <td><%= p.getJumlahProduksi() %></td>
                    <td>Rp <%= String.format("%,d", p.getHargaJual()).replace(",", ".") %></td>
                    <td><%= p.getNamaResep() %></td>
                    <td><%= p.getTanggalProduksi() %></td>
                    <td>
                        <span class="badge <%= p.getStatus().equals("tersedia") ? "badge-green" : "badge-red" %>">
                            <%= p.getStatus() %>
                        </span>
                    </td>
                    <td>
                        <img src="<%= request.getContextPath() %>/uploads/<%= p.getGambar() %>" width="80">
                    </td>
                    <td>
                       <a href="layout.jsp?page=baker/FormProduksi.jsp&id=<%= p.getId() %>" class="btn edit">
                         <i class="fas fa-edit"></i>
                        </a>
                       <a href="<%= request.getContextPath() %>/ProduksiServlet?action=delete&id=<%= p.getId() %>" class="btn delete"
                           onclick="return confirm('Yakin hapus data produksi ini?')">
                         <i class="fas fa-trash"></i>
                       </a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="9" class="text-center text-muted">Belum ada data produksi.</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <!-- Modal akan dimuat di sini -->
    <div id="modalContainer"></div>
</div>

<script>
    // Pencarian client-side
    document.getElementById("searchInput").addEventListener("keyup", function () {
        const filter = this.value.toLowerCase();
        const rows = document.querySelectorAll("#produksiTable tbody tr");
        rows.forEach(row => {
            const produk = row.cells[1].textContent.toLowerCase();
            const resep = row.cells[4].textContent.toLowerCase();
            row.style.display = (produk.includes(filter) || resep.includes(filter)) ? "" : "none";
        });
    });

</script>
