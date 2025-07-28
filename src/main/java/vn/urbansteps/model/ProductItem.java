package vn.urbansteps.model;

import java.math.BigDecimal;

public class ProductItem {
    private Integer sanPhamChiTietId;
    private Integer soLuong;
    private BigDecimal giaTaiThoidiem;

    // Getter/setter
    public Integer getSanPhamChiTietId() { return sanPhamChiTietId; }
    public void setSanPhamChiTietId(Integer sanPhamChiTietId) { this.sanPhamChiTietId = sanPhamChiTietId; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BigDecimal getGiaTaiThoidiem() { return giaTaiThoidiem; }
    public void setGiaTaiThoidiem(BigDecimal giaTaiThoidiem) { this.giaTaiThoidiem = giaTaiThoidiem; }
}
