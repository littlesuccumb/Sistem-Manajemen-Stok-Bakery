<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Resep, model.DetailResep, model.BahanBaku, dao.BahanBakuDAO" %>
<%
    // Cek mode edit dari parameter
    String mode = request.getParameter("mode");
    boolean isEdit = "edit".equals(mode);
    
    // Ambil data dari session jika mode edit, atau dari request attribute jika ada
    Resep resep = null;
    List<DetailResep> detailList = null;
    
    if (isEdit) {
        // Untuk mode edit, ambil dari session
        resep = (Resep) session.getAttribute("editResep");
        detailList = (List<DetailResep>) session.getAttribute("editDetailList");
    } else {
        // Untuk mode tambah atau forward dari servlet, cek request attribute dulu
        resep = (Resep) request.getAttribute("resep");
        detailList = (List<DetailResep>) request.getAttribute("detailList");
        if (resep != null) {
            isEdit = true; // Jika ada data di request, berarti mode edit
        }
    }
    
    String pageTitle = isEdit ? "Edit Resep" : "Tambah Resep";
    String formAction = isEdit
        ? request.getContextPath() + "/ResepServlet"
        : request.getContextPath() + "/ResepServlet";
    
    BahanBakuDAO bahanDAO = new BahanBakuDAO();
    List<BahanBaku> bahanList = bahanDAO.getAllBahan();
%>
<div class="form-wrapper">
    <h2><%= pageTitle %></h2>
    <% if (request.getAttribute("error") != null) { %>
        <div class="notif error"><i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %></div>
    <% } %>
    <form action="<%= formAction %>" method="post">
        <input type="hidden" name="action" value="<%= isEdit ? "update" : "insert" %>">
        <% if (isEdit && resep != null) { %>
            <input type="hidden" name="id" value="<%= resep.getId() %>">
        <% } %>
        
        <div class="form-group">
            <label for="nama_resep">Nama Resep:</label>
            <input type="text" name="nama_resep" id="nama_resep"
                   value="<%= (isEdit && resep != null) ? resep.getNamaResep() : "" %>" required>
        </div>
        
        <div class="form-group">
            <label for="deskripsi">Deskripsi:</label>
            <textarea name="deskripsi" id="deskripsi" rows="3" required><%= (isEdit && resep != null) ? (resep.getDeskripsi() != null ? resep.getDeskripsi() : "") : "" %></textarea>
        </div>
        
        <hr>
        <h4>Pilih Bahan Baku</h4>
        <div class="checkbox-wrapper">
            <% 
            for (BahanBaku b : bahanList) {
                int jumlah = 0;
                boolean selected = false;
                
                // Cek apakah bahan ini sudah dipilih dalam resep
                if (isEdit && detailList != null) {
                    for (DetailResep d : detailList) {
                        if (d.getBahanId() == b.getId()) {
                            jumlah = d.getJumlahDibutuhkan();
                            selected = true;
                            break;
                        }
                    }
                }
            %>
                <div class="checkbox-row">
                    <label>
                        <input type="checkbox" name="bahan_checkbox" value="<%= b.getId() %>" 
                               onchange="toggleJumlah(this, <%= b.getId() %>)"
                               <%= selected ? "checked" : "" %> >
                        <span><%= b.getNamaBahan() %> (<%= b.getSatuan() %>)</span>
                    </label>
                    <input type="hidden" name="bahan_id_<%= b.getId() %>" value="<%= b.getId() %>" disabled>
                    <input type="number" name="jumlah_<%= b.getId() %>" placeholder="Jumlah"
                           value="<%= jumlah > 0 ? jumlah : "" %>"
                           class="jumlah-input" min="1" 
                           <%= selected ? "" : "style='display:none' disabled" %> >
                </div>
            <% } %>
        </div>
        
        <div class="form-button-group">
            <button type="submit" class="btn simpan">
                <i class="fas fa-save"></i> <%= isEdit ? "Update" : "Simpan" %>
            </button>
            <a href="<%= request.getContextPath() %>/view/layout.jsp?page=baker/Resep.jsp" class="btn batal">
                <i class="fas fa-times"></i> Batal
            </a>
        </div>
    </form>
</div>

<script>
    function toggleJumlah(checkbox, bahanId) {
        const row = checkbox.closest(".checkbox-row");
        const jumlahInput = row.querySelector("[name='jumlah_" + bahanId + "']");
        const hiddenInput = row.querySelector("[name='bahan_id_" + bahanId + "']");
        
        if (checkbox.checked) {
            jumlahInput.style.display = "inline-block";
            jumlahInput.disabled = false;
            hiddenInput.disabled = false;
            jumlahInput.focus();
        } else {
            jumlahInput.style.display = "none";
            jumlahInput.disabled = true;
            jumlahInput.value = "";
            hiddenInput.disabled = true;
        }
    }
    
    // Form submission handler untuk memastikan data yang benar dikirim
    document.querySelector('form').addEventListener('submit', function(e) {
        // Hapus semua input bahan_id dan jumlah_dibutuhkan yang lama
        const oldInputs = document.querySelectorAll('input[name="bahan_id"], input[name="jumlah_dibutuhkan"]');
        oldInputs.forEach(input => input.remove());
        
        // Tambahkan input baru hanya untuk checkbox yang terpilih
        const checkboxes = document.querySelectorAll('input[name="bahan_checkbox"]:checked');
        checkboxes.forEach(checkbox => {
            const bahanId = checkbox.value;
            const jumlahInput = document.querySelector('[name="jumlah_' + bahanId + '"]');
            const jumlah = jumlahInput ? jumlahInput.value : '';
            
            if (jumlah && jumlah > 0) {
                // Tambah hidden input untuk bahan_id
                const hiddenBahan = document.createElement('input');
                hiddenBahan.type = 'hidden';
                hiddenBahan.name = 'bahan_id';
                hiddenBahan.value = bahanId;
                this.appendChild(hiddenBahan);
                
                // Tambah hidden input untuk jumlah_dibutuhkan
                const hiddenJumlah = document.createElement('input');
                hiddenJumlah.type = 'hidden';
                hiddenJumlah.name = 'jumlah_dibutuhkan';
                hiddenJumlah.value = jumlah;
                this.appendChild(hiddenJumlah);
            }
        });
    });
    
    document.getElementById("nama_resep").focus();
</script>