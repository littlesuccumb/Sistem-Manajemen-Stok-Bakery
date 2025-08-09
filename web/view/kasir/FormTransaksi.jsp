<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, dao.ProduksiDAO, model.Produksi, model.User, model.Penjualan, model.DetailPenjualan" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"kasir".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/view/layout.jsp?page=auth/login.jsp");
        return;
    }

    List<Produksi> produkList = (List<Produksi>) request.getAttribute("produkList");
    String kodeTransaksi = (String) request.getAttribute("kodeTransaksi");
    Penjualan penjualan = (Penjualan) request.getAttribute("penjualan");
    List<DetailPenjualan> detailList = (List<DetailPenjualan>) request.getAttribute("detailList");

    // PERBAIKAN: Jika data tidak ada (akses langsung dari sidebar), ambil data dari database
    if (produkList == null) {
        try {
            ProduksiDAO produksiDAO = new ProduksiDAO();
            produkList = produksiDAO.getAllProduksi();
        } catch (Exception e) {
            produkList = new ArrayList<>();
            e.printStackTrace();
        }
    }

    // PERBAIKAN: Generate kode transaksi jika tidak ada
    if (kodeTransaksi == null) {
        kodeTransaksi = "BKR" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    boolean isEdit = (penjualan != null);
    if (produkList == null) produkList = new ArrayList<>();
%>

<div class="form-wrapper">
    <!-- Header -->
    <div class="section-title">
        <i class="fas fa-shopping-cart"></i>
        <span><%= isEdit ? "Edit Transaksi" : "Tambah Transaksi" %></span>
    </div>

    <!-- Notifikasi -->
    <% if (request.getAttribute("error") != null) { %>
        <div class="notif error">
            <i class="fas fa-exclamation-circle"></i>
            <%= request.getAttribute("error") %>
        </div>
    <% } %>
    
    <% if (produkList.isEmpty()) { %>
        <div class="notif error">
            <i class="fas fa-exclamation-triangle"></i>
            Tidak ada produk tersedia. Silahkan hubungi baker untuk membuat produk terlebih dahulu.
        </div>
    <% } %>

    <form action="<%= request.getContextPath() %>/PenjualanServlet" method="post" id="transaksiForm">
        <input type="hidden" name="action" value="<%= isEdit ? "update" : "create" %>">
        <% if (isEdit) { %>
            <input type="hidden" name="id" value="<%= penjualan.getId() %>">
        <% } %>

        <!-- Informasi Transaksi -->
        <div class="transaction-info-card">
            <h4><i class="fas fa-info-circle"></i> Informasi Transaksi</h4>
            
            <div class="info-grid">
                <div class="form-group">
                    <label><i class="fas fa-barcode"></i> Kode Transaksi:</label>
                    <input type="text" name="kodeTransaksi" value="<%= isEdit ? penjualan.getKodeTransaksi() : kodeTransaksi %>" readonly>
                </div>

                <div class="form-group">
                    <label><i class="fas fa-user"></i> Kasir:</label>
                    <input type="text" value="<%= user.getNama() %>" readonly>
                </div>
            </div>

            <div class="form-group">
                <label for="metodePembayaran"><i class="fas fa-credit-card"></i> Metode Pembayaran:</label>
                <select name="metodePembayaran" id="metodePembayaran" required>
                    <option value="">-- Pilih Metode Pembayaran --</option>
                    <option value="Tunai" <%= isEdit && "Tunai".equals(penjualan.getMetodePembayaran()) ? "selected" : "" %>>💵 Tunai</option>
<!--                    <option value="Kartu" <%= isEdit && "Kartu".equals(penjualan.getMetodePembayaran()) ? "selected" : "" %>>💳 Kartu</option>-->
                    <option value="Transfer" <%= isEdit && "Transfer".equals(penjualan.getMetodePembayaran()) ? "selected" : "" %>>🏦 Transfer</option>
                </select>
            </div>
        </div>

        <!-- Detail Produk -->
        <div class="product-section-card">
            <h4><i class="fas fa-list"></i> Detail Produk</h4>
            
            <div id="produkContainer">
                <%
                    List<DetailPenjualan> list = isEdit && detailList != null ? detailList : Arrays.asList(new DetailPenjualan());
                    for (int idx = 0; idx < list.size(); idx++) {
                        DetailPenjualan detail = list.get(idx);
                %>
                <div class="produk-item-card" data-index="<%= idx %>">
                    <div class="produk-header">
                        <span class="produk-number">Produk #<%= idx + 1 %></span>
                        <% if (idx > 0 || (isEdit && detailList != null && detailList.size() > 1)) { %>
                        <button type="button" class="btn icon-only delete-btn" onclick="hapusItem(this)" title="Hapus Produk">
                            <i class="fas fa-trash"></i>
                        </button>
                        <% } %>
                    </div>

                    <div class="produk-grid">
                        <div class="form-group">
                            <label><i class="fas fa-cookie-bite"></i> Pilih Produk:</label>
                            <select name="produkId" onchange="updateHarga(this)" <%= produkList.isEmpty() ? "disabled" : "" %> required>
                                <option value="">-- Pilih Produk --</option>
                                <% for (Produksi p : produkList) { %>
                                    <option value="<%= p.getId() %>"
                                            data-harga="<%= p.getHargaJual() %>"
                                            data-stok="<%= p.getJumlahProduksi() %>"
                                            <%= detail.getProduksiId() == p.getId() ? "selected" : "" %>>
                                        <%= p.getNamaProduk() %> - Rp <%= String.format("%,d", p.getHargaJual()) %> (Stok: <%= p.getJumlahProduksi() %>)
                                    </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-sort-numeric-up"></i> Jumlah:</label>
                            <input type="number" name="jumlah" min="1" placeholder="0" 
                                   value="<%= detail.getJumlah() > 0 ? detail.getJumlah() : "" %>" 
                                   onchange="hitungTotal()" <%= produkList.isEmpty() ? "disabled" : "" %> required>
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-tag"></i> Harga Satuan:</label>
                            <input type="text" name="harga" 
                                   value="<%= detail.getHargaSatuan() > 0 ? String.format("Rp %,d", detail.getHargaSatuan()) : "" %>" 
                                   readonly class="readonly-input">
                        </div>

                        <div class="form-group">
                            <label><i class="fas fa-calculator"></i> Subtotal:</label>
                            <input type="text" name="subtotal" class="subtotal readonly-input" 
                                   value="<%= detail.getSubtotal() > 0 ? String.format("Rp %,d", detail.getSubtotal()) : "" %>" 
                                   readonly>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>

            <% if (!produkList.isEmpty()) { %>
            <div class="add-product-section">
                <button type="button" class="btn tambah" onclick="tambahItem()">
                    <i class="fas fa-plus"></i> Tambah Produk Lain
                </button>
            </div>
            <% } %>
        </div>

        <!-- Total Pembayaran -->
        <div class="total-section-card">
            <div class="total-content">
                <h3><i class="fas fa-money-bill-wave"></i> Total Pembayaran</h3>
                <div class="total-amount">
                    Rp <span id="totalHarga">0</span>
                </div>
            </div>
        </div>

        <!-- Action Buttons -->
        <div class="form-button-group">
            <% if (!produkList.isEmpty()) { %>
            <button type="submit" class="btn simpan">
                <i class="fas fa-save"></i> <%= isEdit ? "Update Transaksi" : "Simpan Transaksi" %>
            </button>
            <% } %>
            <a href="<%= request.getContextPath() %>/view/layout.jsp?page=kasir/index.jsp" class="btn batal">
                <i class="fas fa-arrow-left"></i> Kembali
            </a>
        </div>
    </form>
</div>

<script>
    let produkCounter = 1;

    function updateHarga(selectElement) {
        const item = selectElement.closest('.produk-item-card');
        const option = selectElement.selectedOptions[0];
        const harga = option.getAttribute('data-harga') || 0;
        const stok = option.getAttribute('data-stok') || 0;
        
        const hargaInput = item.querySelector('input[name="harga"]');
        const jumlahInput = item.querySelector('input[name="jumlah"]');
        const subtotalInput = item.querySelector('.subtotal');
        
        if (harga > 0) {
            hargaInput.value = 'Rp ' + parseInt(harga).toLocaleString('id-ID');
            jumlahInput.max = stok;
            
            // Jika sudah ada jumlah, hitung ulang subtotal
            if (jumlahInput.value) {
                const subtotal = parseInt(jumlahInput.value) * parseInt(harga);
                subtotalInput.value = 'Rp ' + subtotal.toLocaleString('id-ID');
            } else {
                subtotalInput.value = '';
            }
        } else {
            hargaInput.value = '';
            jumlahInput.max = '';
            jumlahInput.value = '';
            subtotalInput.value = '';
        }
        
        hitungTotal();
    }

    function hitungTotal() {
        let total = 0;
        document.querySelectorAll('.produk-item-card').forEach(item => {
            const jumlah = parseInt(item.querySelector('input[name="jumlah"]').value) || 0;
            const selectElement = item.querySelector('select[name="produkId"]');
            const harga = parseInt(selectElement.selectedOptions[0]?.getAttribute('data-harga')) || 0;
            const subtotal = jumlah * harga;
            
            const subtotalInput = item.querySelector('.subtotal');
            if (subtotal > 0 && jumlah > 0 && harga > 0) {
                subtotalInput.value = 'Rp ' + subtotal.toLocaleString('id-ID');
                total += subtotal;
            } else {
                subtotalInput.value = '';
            }
        });
        
        document.getElementById('totalHarga').textContent = total.toLocaleString('id-ID');
    }

    function tambahItem() {
        const container = document.getElementById('produkContainer');
        const template = container.querySelector('.produk-item-card');
        const clone = template.cloneNode(true);

        produkCounter++;

        // Update produk number
        clone.querySelector('.produk-number').textContent = 'Produk #' + produkCounter;
        clone.setAttribute('data-index', produkCounter - 1);

        // Reset semua nilai
        clone.querySelector('select[name="produkId"]').value = '';
        clone.querySelector('input[name="jumlah"]').value = '';
        clone.querySelector('input[name="harga"]').value = '';
        clone.querySelector('.subtotal').value = '';
        
        // Pastikan ada tombol hapus
        let deleteBtn = clone.querySelector('.delete-btn');
        if (!deleteBtn) {
            deleteBtn = document.createElement('button');
            deleteBtn.type = 'button';
            deleteBtn.className = 'btn icon-only delete-btn';
            deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
            deleteBtn.title = 'Hapus Produk';
            deleteBtn.onclick = function() { hapusItem(this); };
            clone.querySelector('.produk-header').appendChild(deleteBtn);
        }

        container.appendChild(clone);
        updateProdukNumbers();
    }

    function hapusItem(button) {
        const container = document.getElementById('produkContainer');
        const items = container.querySelectorAll('.produk-item-card');
        if (items.length > 1) {
            button.closest('.produk-item-card').remove();
            hitungTotal();
            updateProdukNumbers();
        } else {
            alert('Minimal 1 produk harus ada.');
        }
    }

    function updateProdukNumbers() {
        const items = document.querySelectorAll('.produk-item-card');
        items.forEach((item, index) => {
            item.querySelector('.produk-number').textContent = 'Produk #' + (index + 1);
            item.setAttribute('data-index', index);
        });
        produkCounter = items.length;
    }

    // Hitung total saat halaman dimuat
    window.onload = function() {
        hitungTotal();
        updateProdukNumbers();
        
        // Validasi form
        document.getElementById('transaksiForm').addEventListener('submit', function(e) {
            let hasValidProduct = false;
            let totalAmount = 0;
            
            document.querySelectorAll('.produk-item-card').forEach(item => {
                const produkId = item.querySelector('select[name="produkId"]').value;
                const jumlah = parseInt(item.querySelector('input[name="jumlah"]').value) || 0;
                const selectElement = item.querySelector('select[name="produkId"]');
                const harga = parseInt(selectElement.selectedOptions[0]?.getAttribute('data-harga')) || 0;
                const stok = parseInt(selectElement.selectedOptions[0]?.getAttribute('data-stok')) || 0;
                
                if (produkId && jumlah > 0) {
                    if (jumlah > stok) {
                        e.preventDefault();
                        alert('Jumlah produk ' + selectElement.selectedOptions[0].text.split(' - ')[0] + ' melebihi stok yang tersedia (' + stok + ')!');
                        return false;
                    }
                    hasValidProduct = true;
                    totalAmount += (jumlah * harga);
                }
            });
            
            if (!hasValidProduct) {
                e.preventDefault();
                alert('Silahkan pilih minimal 1 produk dengan jumlah yang valid!');
                return false;
            }
            
            if (totalAmount <= 0) {
                e.preventDefault();
                alert('Total transaksi harus lebih dari 0!');
                return false;
            }
            
            const metodePembayaran = document.getElementById('metodePembayaran').value;
            if (!metodePembayaran) {
                e.preventDefault();
                alert('Silahkan pilih metode pembayaran!');
                return false;
            }
            
            return confirm('Yakin ingin menyimpan transaksi ini?');
        });
    };
</script>