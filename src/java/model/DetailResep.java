package model;

public class DetailResep {
    private int id;
    private int resepId;
    private int bahanId;
    private int jumlahDibutuhkan;
    private String namaBahan;
    private String satuan;
    
    // Constructor
    public DetailResep() {}
    
    public DetailResep(int id, int resepId, int bahanId, int jumlahDibutuhkan, String namaBahan, String satuan) {
        this.id = id;
        this.resepId = resepId;
        this.bahanId = bahanId;
        this.jumlahDibutuhkan = jumlahDibutuhkan;
        this.namaBahan = namaBahan;
        this.satuan = satuan;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getResepId() {
        return resepId;
    }
    
    public void setResepId(int resepId) {
        this.resepId = resepId;
    }
    
    public int getBahanId() {
        return bahanId;
    }
    
    public void setBahanId(int bahanId) {
        this.bahanId = bahanId;
    }
    
    public int getJumlahDibutuhkan() {
        return jumlahDibutuhkan;
    }
    
    public void setJumlahDibutuhkan(int jumlahDibutuhkan) {
        this.jumlahDibutuhkan = jumlahDibutuhkan;
    }
    
    public String getNamaBahan() {
        return namaBahan;
    }
    
    public void setNamaBahan(String namaBahan) {
        this.namaBahan = namaBahan;
    }
    
    public String getSatuan() {
        return satuan;
    }
    
    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }
}