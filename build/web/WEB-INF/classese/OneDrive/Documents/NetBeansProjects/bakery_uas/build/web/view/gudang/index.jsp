<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.BahanBaku, dao.BahanBakuDAO" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp?pesan=belumlogin");
        return;
    }

    BahanBakuDAO dao = new BahanBakuDAO();
    List<BahanBaku> daftarBahan = dao.getAllBahan();
    String status = request.getParameter("status");
%>

<div class="container">
    <%-- Notifikasi --%>
    <% if ("tambah".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Bahan berhasil ditambahkan.</div>
    <% } else if ("hapus".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Bahan berhasil dihapus.</div>
    <% } else if ("ubah".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Bahan berhasil diperbarui.</div>
    <% } else if ("gagal".equals(status)) { %>
        <div class="notif error"><i class="fas fa-exclamation-circle"></i> Gagal memproses data.</div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="section-title">
            <i class="fas fa-warehouse"></i>
            Data Bahan Baku
        </h4>
        <a href="layout.jsp?page=gudang/FormBahan.jsp" class="btn tambah">
            <i class="fas fa-plus"></i> Tambah Bahan
        </a>
    </div>

    <div class="search-wrapper">
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Cari nama atau satuan bahan...">
        </div>
    </div>

    <div class="table-wrapper">
        <table class="table" id="bahanTable">
            <thead>
                <tr>
                    <th>No</th>
                    <th>Nama Bahan</th>
                    <th>Jumlah</th>
                    <th>Satuan</th>
                    <th>Harga Satuan</th>
                    <th>Tanggal Masuk</th>
                    <th>Status</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
            <%
                int no = 1;
                for (BahanBaku bahan : daftarBahan) {
            %>
                <tr>
                    <td><%= no++ %></td>
                    <td><%= bahan.getNamaBahan() %></td>
                    <td><%= String.format("%,.0f", (double)bahan.getJumlah()).replace(",", ".") %></td>
                    <td><%= bahan.getSatuan() %></td>
                    <td>Rp <%= String.format("%,.2f", (double)bahan.getHargaSatuan()).replace(",", ".") %></td>
                    <td><%= bahan.getTanggalMasuk() %></td>
                    <td>
                        <%
                            int jumlah = bahan.getJumlah();
                            if (jumlah <= 0) {
                        %>
                            <span class="badge badge-red">Habis</span>
                        <%
                            } else if (jumlah <= 2000) {
                        %>
                            <span class="badge badge-orange">Menipis</span>
                        <%
                            } else {
                        %>
                            <span class="badge badge-green">Aman</span>
                        <%
                            }
                        %>
                    </td>
                    <td>
                        <a href="layout.jsp?page=gudang/FormBahan.jsp&id=<%= bahan.getId() %>" class="btn edit">
                            <i class="fas fa-edit"></i>
                        </a>
                        <a href="<%= request.getContextPath() %>/gudang/hapus-bahan?id=<%= bahan.getId() %>"
                           class="btn delete"
                           onclick="return confirm('Yakin ingin menghapus bahan ini?')">
                            <i class="fas fa-trash-alt"></i>
                        </a>
                    </td>
                </tr>
            <%
                }
                if (daftarBahan.isEmpty()) {
            %>
                <tr>
                    <td colspan="8" class="text-center text-muted">Belum ada data bahan baku.</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>

<script>
    // Pencarian client-side
    document.getElementById("searchInput").addEventListener("keyup", function () {
        const filter = this.value.toLowerCase();
        const rows = document.querySelectorAll("#bahanTable tbody tr");
        rows.forEach(row => {
            const nama = row.cells[1].textContent.toLowerCase();
            const satuan = row.cells[3].textContent.toLowerCase();
            row.style.display = (nama.includes(filter) || satuan.includes(filter)) ? "" : "none";
        });
    });
</script>
