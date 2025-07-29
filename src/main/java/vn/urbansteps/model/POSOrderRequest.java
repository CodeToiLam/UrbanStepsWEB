package vn.urbansteps.model;

import java.math.BigDecimal;
import java.util.List;

public class POSOrderRequest {
    private String hoTen;
    private String sdt;
    private String ghiChu;
    private List<ProductItem> products;
    private BigDecimal tienMat;
    private BigDecimal tienChuyenKhoan;
    private int phuongThucThanhToan;

    public List<GioHangItem> toGioHangItems() {
        List<GioHangItem> items = new java.util.ArrayList<>();
        if (products != null) {
            for (ProductItem p : products) {
                GioHangItem item = new GioHangItem();
                SanPhamChiTiet spct = new SanPhamChiTiet();
                spct.setId(p.getSanPhamChiTietId());
                item.setSanPhamChiTiet(spct);
                item.setSoLuong(p.getSoLuong());
                item.setGiaTaiThoidiem(p.getGiaTaiThoidiem());
                items.add(item);
            }
        }
        return items;
    }

    // Getter/setter
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public List<ProductItem> getProducts() { return products; }
    public void setProducts(List<ProductItem> products) { this.products = products; }
    public BigDecimal getTienMat() { return tienMat; }
    public void setTienMat(BigDecimal tienMat) { this.tienMat = tienMat; }
    public BigDecimal getTienChuyenKhoan() { return tienChuyenKhoan; }
    public void setTienChuyenKhoan(BigDecimal tienChuyenKhoan) { this.tienChuyenKhoan = tienChuyenKhoan; }
    public int getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(int phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }
}
