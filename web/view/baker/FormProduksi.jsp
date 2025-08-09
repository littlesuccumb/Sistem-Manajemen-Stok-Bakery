<%@ page import="model.Produksi" %>
<%@ page import="dao.ProduksiDAO" %>
<%@ page import="dao.ResepDAO" %>
<%@ page import="model.Resep" %>
<%@ page import="java.util.*" %>

<%
    Produksi produksi = null;
    boolean isEdit = false;

    String idParam = request.getParameter("id");
    if (idParam != null && !idParam.isEmpty()) {
        try {
            int id = Integer.parseInt(idParam);
            ProduksiDAO dao = new ProduksiDAO();
            produksi = dao.getProduksiById(id);
            isEdit = (produksi != null);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Gagal mengambil data produksi: " + e.getMessage());
        }
    }

    String pageTitle = isEdit ? "Edit Produksi" : "Tambah Produksi";
    String formAction = isEdit
        ? request.getContextPath() + "/ProduksiServlet?action=update"
        : request.getContextPath() + "/ProduksiServlet?action=insert";

    ResepDAO resepDAO = new ResepDAO();
    List<Resep> resepList = null;
    try {
        resepList = resepDAO.getAllResep();
        if (resepList == null) {
            resepList = new ArrayList<>();
        }
    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("error", "Gagal memuat data resep: " + e.getMessage());
        resepList = new ArrayList<>();
    }
%>

<div class="form-wrapper">
    <h2><%= pageTitle %></h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="notif error">
            <i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <form action="<%= formAction %>" method="post" enctype="multipart/form-data" id="produksiForm">
        <% if (isEdit) { %>
            <input type="hidden" name="id" value="<%= produksi.getId() %>">
        <% } %>

        <div class="form-group">
            <label for="nama_produk">Nama Produk:</label>
            <input type="text" name="nama_produk" id="nama_produk"
                   value="<%= isEdit && produksi != null ? produksi.getNamaProduk() : "" %>" required>
        </div>

        <div class="form-group">
            <label for="resep_id">Pilih Resep:</label>
            <select name="resep_id" id="resep_id" required>
                <option value="">-- Pilih Resep --</option>
                <% for (Resep r : resepList) { %>
                    <option value="<%= r.getId() %>" 
                            <%= isEdit && produksi != null && r.getId() == produksi.getResepId() ? "selected" : "" %>>
                        <%= r.getNamaResep() %>
                    </option>
                <% } %>
            </select>
        </div>

        <div class="form-group">
            <label for="jumlah">Jumlah Produksi:</label>
            <input type="number" name="jumlah" id="jumlah"
                   value="<%= isEdit && produksi != null ? produksi.getJumlahProduksi() : "" %>" 
                   required min="1" step="1">
        </div>

        <div class="form-group">
            <label for="harga_jual">Harga Jual:</label>
            <input type="number" name="harga_jual" id="harga_jual"
                   value="<%= isEdit && produksi != null ? produksi.getHargaJual() : "" %>" 
                   required min="0" step="1">
        </div>

        <div class="form-group">
            <label for="tanggal">Tanggal Produksi:</label>
            <input type="date" name="tanggal" id="tanggal"
                   value="<%= isEdit && produksi != null && produksi.getTanggalProduksi() != null
                            ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(produksi.getTanggalProduksi())
                            : new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>" required>
        </div>

        <div class="form-group">
            <label for="status">Status:</label>
            <select name="status" id="status" required>
                <option value="">-- Pilih Status --</option>
                <option value="tersedia" <%= isEdit && produksi != null && "tersedia".equals(produksi.getStatus()) ? "selected" : "" %>>Tersedia</option>
                <option value="habis" <%= isEdit && produksi != null && "habis".equals(produksi.getStatus()) ? "selected" : "" %>>Habis</option>
            </select>
        </div>

        <div class="form-group">
            <label for="gambar">Gambar Produk:</label>
            <input type="file" name="gambar" id="gambar" accept="image/*" <%= isEdit ? "" : "required" %>>
            <% if (isEdit && produksi != null && produksi.getGambar() != null) { %>
                <br><small>Gambar saat ini:</small><br>
                <img src="<%= request.getContextPath() %>/uploads/<%= produksi.getGambar() %>" 
                     width="80" style="margin-top: 10px; border: 1px solid #ddd; border-radius: 4px;">
                <br><small>Kosongkan jika tidak ingin mengubah gambar</small>
            <% } %>
        </div>

        <div class="form-button-group">
            <button type="submit" class="btn simpan">
                <i class="fas fa-save"></i> <%= isEdit ? "Update Produksi" : "Simpan Produksi" %>
            </button>
            <a href="<%= request.getContextPath() %>/view/layout.jsp?page=baker/Produksi.jsp" class="btn batal">
                <i class="fas fa-times"></i> Batal
            </a>
        </div>
    </form>
</div>

<script>
    const form = document.getElementById('produksiForm');
    
    form.addEventListener('submit', function(e) {
        const nama = document.getElementById("nama_produk").value.trim();
        const resep = document.getElementById("resep_id").value;
        const jumlah = document.getElementById("jumlah").value;
        const harga = document.getElementById("harga_jual").value;
        const tanggal = document.getElementById("tanggal").value;
        const status = document.getElementById("status").value;
        const gambar = document.getElementById("gambar");

        if (!nama) {
            alert("Nama produk tidak boleh kosong!");
            e.preventDefault();
            document.getElementById("nama_produk").focus();
            return;
        }
        
        if (!resep) {
            alert("Pilih resep terlebih dahulu!");
            e.preventDefault();
            document.getElementById("resep_id").focus();
            return;
        }
        
        if (!jumlah || parseInt(jumlah) < 1) {
            alert("Jumlah produksi harus lebih dari 0!");
            e.preventDefault();
            document.getElementById("jumlah").focus();
            return;
        }
        
        if (!harga || parseInt(harga) < 0) {
            alert("Harga jual harus valid (minimal 0)!");
            e.preventDefault();
            document.getElementById("harga_jual").focus();
            return;
        }
        
        if (!tanggal) {
            alert("Tanggal produksi harus diisi!");
            e.preventDefault();
            document.getElementById("tanggal").focus();
            return;
        }
        
        if (!status) {
            alert("Status harus dipilih!");
            e.preventDefault();
            document.getElementById("status").focus();
            return;
        }

        // Validasi file gambar untuk insert (tidak edit)
        <% if (!isEdit) { %>
        if (!gambar.files || gambar.files.length === 0) {
            alert("Gambar produk harus dipilih!");
            e.preventDefault();
            gambar.focus();
            return;
        }
        <% } %>

        // Validasi tipe file jika ada file yang dipilih
        if (gambar.files && gambar.files.length > 0) {
            const file = gambar.files[0];
            const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
            if (!allowedTypes.includes(file.type)) {
                alert("Format gambar harus JPG, JPEG, PNG, atau GIF!");
                e.preventDefault();
                gambar.focus();
                return;
            }
            
            // Validasi ukuran file (max 10MB)
            if (file.size > 10 * 1024 * 1024) {
                alert("Ukuran gambar tidak boleh lebih dari 10MB!");
                e.preventDefault();
                gambar.focus();
                return;
            }
        }

        // Show loading state
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Menyimpan...';
        submitBtn.disabled = true;
        
        // Reset if form submission fails
        setTimeout(() => {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }, 10000);
    });

    // Auto fokus ke field pertama
    document.getElementById("nama_produk").focus();

    // Preview gambar yang dipilih
    document.getElementById("gambar").addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                let preview = document.getElementById('imagePreview');
                if (!preview) {
                    preview = document.createElement('img');
                    preview.id = 'imagePreview';
                    preview.style.maxWidth = '200px';
                    preview.style.marginTop = '10px';
                    preview.style.border = '1px solid #ddd';
                    preview.style.borderRadius = '4px';
                    e.target.parentNode.appendChild(document.createElement('br'));
                    e.target.parentNode.appendChild(preview);
                }
                preview.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });
</script>