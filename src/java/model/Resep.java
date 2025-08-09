package model;

public class Resep {
    private int id;
    private String namaResep;
    private String deskripsi;
    
    // Constructor kosong
    public Resep() {}
    
    // Constructor tanpa id
    public Resep(String namaResep, String deskripsi) {
        this.namaResep = namaResep;
        this.deskripsi = deskripsi;
    }
    
    // Constructor lengkap
    public Resep(int id, String namaResep, String deskripsi) {
        this.id = id;
        this.namaResep = namaResep;
        this.deskripsi = deskripsi;
    }
    
    // Getter dan Setter
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNamaResep() {
        return namaResep;
    }
    
    public void setNamaResep(String namaResep) {
        this.namaResep = namaResep;
    }
    
    public String getDeskripsi() {
        return deskripsi;
    }
    
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}