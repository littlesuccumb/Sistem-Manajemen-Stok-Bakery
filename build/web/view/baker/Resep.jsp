<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Resep, dao.ResepDAO" %>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp?pesan=belumlogin");
        return;
    }

    // Ambil data langsung dari DAO karena tidak lewat servlet
    ResepDAO dao = new ResepDAO();
    List<Resep> resepList = dao.getAllResep();

    String status = request.getParameter("status");
%>

<div class="container">
    <%-- Notifikasi --%>
    <% if ("tambah".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Resep berhasil ditambahkan.</div>
    <% } else if ("hapus".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Resep berhasil dihapus.</div>
    <% } else if ("ubah".equals(status)) { %>
        <div class="notif success"><i class="fas fa-check-circle"></i> Resep berhasil diperbarui.</div>
    <% } else if ("gagal".equals(status)) { %>
        <div class="notif error"><i class="fas fa-exclamation-circle"></i> Gagal memproses data.</div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="section-title"><i class="fas fa-book-open"></i> Data Resep</h4>
        <a href="layout.jsp?page=baker/FormResep.jsp" class="btn tambah">
            <i class="fas fa-plus"></i> Tambah Resep
        </a>
    </div>

    <div class="search-wrapper">
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Cari nama atau deskripsi resep...">
        </div>
    </div>

    <div class="table-wrapper">
        <table class="table" id="resepTable">
            <thead>
                <tr>
                    <th>No</th>
                    <th>Nama Resep</th>
                    <th>Deskripsi</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <%
                    int no = 1;
                    if (resepList != null && !resepList.isEmpty()) {
                        for (Resep r : resepList) {
                %>
                <tr>
                    <td><%= no++ %></td>
                    <td><%= r.getNamaResep() %></td>
                    <td title="<%= r.getDeskripsi() %>" style="max-width: 350px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                        <% if (r.getDeskripsi() != null && !r.getDeskripsi().isEmpty()) { %>
                            <%= r.getDeskripsi() %>
                        <% } else { %>
                            <span class="text-muted"><i>Belum ada deskripsi</i></span>
                        <% } %>
                    </td>
                    <td>
                        <!-- Tombol Detail/View -->
                        <a href="#" class="btn edit" onclick="loadModal(<%= r.getId() %>)">
                            <i class="fas fa-eye"></i>
                        </a>
                        <!-- Tombol Edit -->
                        <a href="<%= request.getContextPath() %>/ResepServlet?action=edit&id=<%= r.getId() %>" class="btn edit">
                            <i class="fas fa-edit"></i>
                        </a>
                        <!-- Tombol Delete - DIPERBAIKI -->
                        <a href="<%= request.getContextPath() %>/ResepServlet?action=delete&id=<%= r.getId() %>" class="btn delete"
                           onclick="return confirm('Yakin ingin menghapus resep ini?')">
                            <i class="fas fa-trash-alt"></i>
                        </a>
                    </td>
                </tr>
                <% 
                        }
                    } else {
                %>
                <tr>
                    <td colspan="4" class="text-center text-muted">
                        Belum ada data resep.
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <%-- Modal container --%>
    <div id="modalContainer"></div>
</div>

<script>
    // Pencarian client-side
    document.getElementById("searchInput").addEventListener("keyup", function () {
        const filter = this.value.toLowerCase();
        const rows = document.querySelectorAll("#resepTable tbody tr");
        rows.forEach(row => {
            const nama = row.cells[1].textContent.toLowerCase();
            const desk = row.cells[2].textContent.toLowerCase();
            row.style.display = (nama.includes(filter) || desk.includes(filter)) ? "" : "none";
        });
    });

    // DIPERBAIKI: Hapus parameter 'asal' yang tidak digunakan
    function loadModal(id) {
        fetch('<%= request.getContextPath() %>/ResepServlet?action=detail&id=' + id)
            .then(response => response.text())
            .then(html => {
                document.getElementById('modalContainer').innerHTML = html;
            })
            .catch(error => {
                console.error('Error loading modal:', error);
                alert('Gagal memuat detail resep');
            });
    }

    function closeModal() {
        document.getElementById('modalContainer').innerHTML = "";
    }
</script>