package model;

import java.util.Date;

public class BahanBaku {
    private int id;
    private String namaBahan;
    private int jumlah;
    private String satuan;
    private int hargaSatuan;
    private Date tanggalMasuk;
    
    // Constructor kosong
    public BahanBaku() {}
    
    // Constructor tanpa id
    public BahanBaku(String namaBahan, int jumlah, String satuan, int hargaSatuan, Date tanggalMasuk) {
        this.namaBahan = namaBahan;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.hargaSatuan = hargaSatuan;
        this.tanggalMasuk = tanggalMasuk;
    }
    
    // Constructor lengkap
    public BahanBaku(int id, String namaBahan, int jumlah, String satuan, int hargaSatuan, Date tanggalMasuk) {
        this.id = id;
        this.namaBahan = namaBahan;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.hargaSatuan = hargaSatuan;
        this.tanggalMasuk = tanggalMasuk;
    }
    
    // Getter dan Setter
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNamaBahan() {
        return namaBahan;
    }
    
    public void setNamaBahan(String namaBahan) {
        this.namaBahan = namaBahan;
    }
    
    public int getJumlah() {
        return jumlah;
    }
    
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }
    
    public String getSatuan() {
        return satuan;
    }
    
    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }
    
    public int getHargaSatuan() {
        return hargaSatuan;
    }
    
    public void setHargaSatuan(int hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }
    
    public Date getTanggalMasuk() {
        return tanggalMasuk;
    }
    
    public void setTanggalMasuk(Date tanggalMasuk) {
        this.tanggalMasuk = tanggalMasuk;
    }
    
    public String getKodeBahan() {
    return String.format("B%03d", this.id);
}
    
     @Override
    public String toString() {
        return "BahanBaku{" +
                "id=" + id +
                ", namaBahan='" + namaBahan + '\'' +
                ", stok=" + jumlah +
                ", satuan='" + satuan + '\'' +
                ", hargaSatuan=" + hargaSatuan +
                ", tanggalMasuk=" + tanggalMasuk +
                '}';
    }
}
