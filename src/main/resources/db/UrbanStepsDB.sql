-- XÓA VÀ TẠO LẠI DATABASE HOÀN TOÀN MỚI
USE master;
GO

-- Ngắt tất cả kết nối đến database
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'UrbanStepsDB')
BEGIN
    ALTER DATABASE UrbanStepsDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE UrbanStepsDB;
END
GO

-- Tạo database mới
CREATE DATABASE UrbanStepsDB;
GO

-- Sử dụng database mới
USE UrbanStepsDB;
GO

CREATE TABLE LoaiSanPham (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_loai_san_pham NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE ThuongHieu (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_thuong_hieu NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE ChatLieu (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_chat_lieu NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE DanhMuc (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_danh_muc NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE XuatXu (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_xuat_xu VARCHAR(255) NOT NULL
);
GO

CREATE TABLE KieuDang (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_kieu_dang NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE HinhAnh (
  id INT IDENTITY(1,1) PRIMARY KEY,
  duong_dan NVARCHAR(255) NOT NULL,
  mo_ta NVARCHAR(500),
  thu_tu INT DEFAULT 0,
  la_anh_chinh BIT DEFAULT 0,
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME
);
GO

CREATE TABLE SanPham (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_loai_san_pham INT,
  id_danh_muc INT,
  id_thuong_hieu INT,
  id_xuat_xu INT,
  id_kieu_dang INT,
  id_chat_lieu INT,
  id_hinh_anh_dai_dien INT,
  ma_san_pham VARCHAR(50) NOT NULL UNIQUE,
  ten_san_pham NVARCHAR(255) NOT NULL,
  mo_ta NVARCHAR(1000), -- Tăng độ dài mô tả
  gia_nhap DECIMAL(10,2) NOT NULL CHECK (gia_nhap >= 0),
  gia_ban DECIMAL(10,2) NOT NULL CHECK (gia_ban >= 0),
  la_hot BIT DEFAULT 0, -- Sản phẩm hot
  la_sale BIT DEFAULT 0, -- Sản phẩm sale
  phan_tram_giam DECIMAL(5,2) DEFAULT 0 CHECK (phan_tram_giam >= 0 AND phan_tram_giam <= 100),
  luot_xem INT DEFAULT 0, -- Số lượt xem
  luot_ban INT DEFAULT 0, -- Số lượt bán
  diem_danh_gia DECIMAL(3,2) DEFAULT 0 CHECK (diem_danh_gia >= 0 AND diem_danh_gia <= 5), -- Điểm đánh giá trung bình
  so_luong_danh_gia INT DEFAULT 0, -- Số lượng đánh giá
  trang_thai BIT DEFAULT 1, -- 1: Hoạt động, 0: Không hoạt động
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_thuong_hieu) REFERENCES ThuongHieu(id),
  FOREIGN KEY (id_chat_lieu) REFERENCES ChatLieu(id),
  FOREIGN KEY (id_danh_muc) REFERENCES DanhMuc(id),
  FOREIGN KEY (id_loai_san_pham) REFERENCES LoaiSanPham(id),
  FOREIGN KEY (id_xuat_xu) REFERENCES XuatXu(id),
  FOREIGN KEY (id_kieu_dang) REFERENCES KieuDang(id),
  FOREIGN KEY (id_hinh_anh_dai_dien) REFERENCES HinhAnh(id)
);
GO

CREATE TABLE KichCo (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_kich_co NVARCHAR(50) NOT NULL UNIQUE -- Tên kích cỡ phải unique
);
GO

CREATE TABLE MauSac (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_mau_sac NVARCHAR(50) NOT NULL UNIQUE, -- Tên màu sắc phải unique
  ma_mau VARCHAR(10) -- Mã màu hex (#FF0000)
);
GO

CREATE TABLE SanPhamChiTiet (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_san_pham INT NOT NULL,
  id_kich_co INT NOT NULL,
  id_mau_sac INT NOT NULL,
  so_luong INT NOT NULL CHECK (so_luong >= 0),
  gia_ban_le DECIMAL(10,2), -- Giá riêng cho variant này (nếu khác giá sản phẩm chính)
  trang_thai BIT DEFAULT 1, -- 1: Hoạt động, 0: Hết hàng/Ngừng bán
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_san_pham) REFERENCES SanPham(id),
  FOREIGN KEY (id_kich_co) REFERENCES KichCo(id),
  FOREIGN KEY (id_mau_sac) REFERENCES MauSac(id),
  UNIQUE (id_san_pham, id_kich_co, id_mau_sac) -- Mỗi sản phẩm chỉ có 1 record cho mỗi size+color
);
GO

CREATE TABLE TaiKhoan (
  id INT IDENTITY(1,1) PRIMARY KEY,
  tai_khoan NVARCHAR(100) NOT NULL,
  mat_khau NVARCHAR(255) NOT NULL,
  email NVARCHAR(100),
  ho_ten_tai_khoan NVARCHAR(255),
  sdt VARCHAR(20),
  gioi_tinh BIT DEFAULT 0,
  dia_chi NVARCHAR(500),
  hinh_anh NVARCHAR(255),
  otp NVARCHAR(50),
  role NVARCHAR(50) NOT NULL DEFAULT 'USER',
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME
);
GO

CREATE TABLE KhachHang (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_tai_khoan INT NULL, -- NULL nếu là khách vãng lai
  ho_ten_khach_hang NVARCHAR(255) NOT NULL,
  sdt NVARCHAR(20),
  email NVARCHAR(100),
  gioi_tinh BIT DEFAULT 0,
  dia_chi NVARCHAR(500),
  la_khach_vang_lai BIT DEFAULT 0, -- 1: khách vãng lai, 0: có tài khoản
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_tai_khoan) REFERENCES TaiKhoan(id)
);
GO

CREATE TABLE PhieuGiamGia (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ma_phieu_giam_gia VARCHAR(50) NOT NULL UNIQUE,
  ten_phieu_giam_gia NVARCHAR(255) NOT NULL,
  mo_ta NVARCHAR(500),
  so_luong INT CHECK (so_luong >= 0),
  so_luong_da_su_dung INT DEFAULT 0 CHECK (so_luong_da_su_dung >= 0),
  ngay_bat_dau DATETIME NOT NULL,
  ngay_ket_thuc DATETIME NOT NULL,
  giam_theo_phan_tram BIT DEFAULT 0,
  gia_tri_giam DECIMAL(10,2) NOT NULL CHECK (gia_tri_giam >= 0),
  giam_toi_da DECIMAL(10,2) CHECK (giam_toi_da >= 0),
  don_toi_thieu DECIMAL(10,2) DEFAULT 0 CHECK (don_toi_thieu >= 0),
  ap_dung_cho_tat_ca BIT DEFAULT 1, -- Áp dụng cho tất cả sản phẩm
  trang_thai BIT DEFAULT 1, -- 1: Hoạt động, 0: Không hoạt động
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  CHECK (ngay_ket_thuc > ngay_bat_dau),
  CHECK (so_luong_da_su_dung <= so_luong)
);
GO

CREATE TABLE HoaDon (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_khach_hang INT NOT NULL, -- Chỉ liên kết với KhachHang
  id_phieu_giam_gia INT,
  ma_hoa_don VARCHAR(50) NOT NULL UNIQUE,
  tong_tien DECIMAL(10,2) NOT NULL, -- Tổng tiền trước giảm giá
  tien_giam DECIMAL(10,2) DEFAULT 0, -- Số tiền được giảm
  tong_thanh_toan DECIMAL(10,2) NOT NULL, -- Tổng tiền phải thanh toán
  tien_mat DECIMAL(10,2) DEFAULT 0,
  tien_chuyen_khoan DECIMAL(10,2) DEFAULT 0,
  phuong_thuc_thanh_toan TINYINT NOT NULL, -- 1: Tiền mặt, 2: Chuyển khoản, 3: Cả hai
  trang_thai TINYINT DEFAULT 0, -- 0: Chờ xử lý, 1: Đã xác nhận, 2: Đang giao, 3: Hoàn thành, 4: Đã hủy, 5: Đã thanh toán
  ghi_chu NVARCHAR(500),
  dia_chi_giao_hang NVARCHAR(500),
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_khach_hang) REFERENCES KhachHang(id),
  FOREIGN KEY (id_phieu_giam_gia) REFERENCES PhieuGiamGia(id)
);
GO

CREATE TABLE HoaDonChiTiet (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_san_pham_chi_tiet INT NOT NULL,
  id_hoa_don INT NOT NULL,
  ma_hoa_don_chi_tiet VARCHAR(50) NOT NULL UNIQUE,
  gia_nhap DECIMAL(10,2) NOT NULL,
  gia_ban DECIMAL(10,2) NOT NULL,
  so_luong INT NOT NULL,
  thanh_tien DECIMAL(10,2) NOT NULL, -- gia_ban * so_luong
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_san_pham_chi_tiet) REFERENCES SanPhamChiTiet(id),
  FOREIGN KEY (id_hoa_don) REFERENCES HoaDon(id)
);
GO

-- Bảng theo dõi lịch sử trạng thái đơn hàng
CREATE TABLE HoaDonTrangThai (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_hoa_don INT NOT NULL,
  trang_thai_cu TINYINT,
  trang_thai_moi TINYINT NOT NULL,
  ghi_chu NVARCHAR(500),
  nguoi_thuc_hien NVARCHAR(255), -- Tên người thực hiện thay đổi
  create_at DATETIME DEFAULT GETDATE(),
  FOREIGN KEY (id_hoa_don) REFERENCES HoaDon(id)
);
GO

CREATE TABLE HinhAnh_SanPhamChiTiet (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_hinh_anh INT,
  id_san_pham_chi_tiet INT,
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_hinh_anh) REFERENCES HinhAnh(id),
  FOREIGN KEY (id_san_pham_chi_tiet) REFERENCES SanPhamChiTiet(id)
);
GO

CREATE TABLE GioHang (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_tai_khoan INT,
  session_id NVARCHAR(255),
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_tai_khoan) REFERENCES TaiKhoan(id)
);
GO

CREATE TABLE GioHangItem (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_gio_hang INT,
  id_san_pham_chi_tiet INT,
  so_luong INT NOT NULL,
  gia_tai_thoi_diem DECIMAL(10,2) NOT NULL,
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_gio_hang) REFERENCES GioHang(id),
  FOREIGN KEY (id_san_pham_chi_tiet) REFERENCES SanPhamChiTiet(id)
);
GO


-- Bảng đánh giá sản phẩm
CREATE TABLE DanhGiaSanPham (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_san_pham INT NOT NULL,
  id_tai_khoan INT NOT NULL,
  id_hoa_don_chi_tiet INT, -- Chỉ cho phép đánh giá khi đã mua
  diem_danh_gia TINYINT NOT NULL CHECK (diem_danh_gia >= 1 AND diem_danh_gia <= 5),
  tieu_de NVARCHAR(255),
  noi_dung NVARCHAR(1000),
  hinh_anh_1 NVARCHAR(255),
  hinh_anh_2 NVARCHAR(255),
  hinh_anh_3 NVARCHAR(255),
  trang_thai BIT DEFAULT 1, -- 1: Hiển thị, 0: Ẩn
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_san_pham) REFERENCES SanPham(id),
  FOREIGN KEY (id_tai_khoan) REFERENCES TaiKhoan(id),
  FOREIGN KEY (id_hoa_don_chi_tiet) REFERENCES HoaDonChiTiet(id),
  UNIQUE (id_tai_khoan, id_hoa_don_chi_tiet) -- Mỗi sản phẩm trong đơn hàng chỉ đánh giá 1 lần
);
GO

-- Bảng sản phẩm yêu thích
CREATE TABLE SanPhamYeuThich (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_tai_khoan INT NOT NULL,
  id_san_pham INT NOT NULL,
  create_at DATETIME DEFAULT GETDATE(),
  FOREIGN KEY (id_tai_khoan) REFERENCES TaiKhoan(id),
  FOREIGN KEY (id_san_pham) REFERENCES SanPham(id),
  UNIQUE (id_tai_khoan, id_san_pham) -- Mỗi tài khoản chỉ thích 1 lần
);
GO

-- Bảng địa chỉ giao hàng của khách hàng
CREATE TABLE DiaChiGiaoHang (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_tai_khoan INT NOT NULL,
  ten_nguoi_nhan NVARCHAR(255) NOT NULL,
  sdt_nguoi_nhan NVARCHAR(20) NOT NULL,
  dia_chi_chi_tiet NVARCHAR(500) NOT NULL,
  phuong_xa NVARCHAR(100),
  quan_huyen NVARCHAR(100),
  tinh_thanh_pho NVARCHAR(100),
  la_dia_chi_mac_dinh BIT DEFAULT 0,
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_tai_khoan) REFERENCES TaiKhoan(id)
);
GO

-- Bảng flash sale events
CREATE TABLE FlashSale (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_flash_sale NVARCHAR(255) NOT NULL,
  mo_ta NVARCHAR(500),
  ngay_bat_dau DATETIME NOT NULL,
  ngay_ket_thuc DATETIME NOT NULL,
  trang_thai BIT DEFAULT 1, -- 1: Hoạt động, 0: Không hoạt động
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  CHECK (ngay_ket_thuc > ngay_bat_dau)
);
GO

-- Bảng sản phẩm trong flash sale
CREATE TABLE FlashSaleSanPham (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_flash_sale INT NOT NULL,
  id_san_pham INT NOT NULL,
  gia_flash_sale DECIMAL(10,2) NOT NULL CHECK (gia_flash_sale >= 0),
  so_luong_han_che INT NOT NULL CHECK (so_luong_han_che > 0),
  so_luong_da_ban INT DEFAULT 0 CHECK (so_luong_da_ban >= 0),
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_flash_sale) REFERENCES FlashSale(id),
  FOREIGN KEY (id_san_pham) REFERENCES SanPham(id),
  UNIQUE (id_flash_sale, id_san_pham),
  CHECK (so_luong_da_ban <= so_luong_han_che)
);
GO

-- Bảng lịch sử xem sản phẩm
CREATE TABLE LichSuXemSanPham (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_tai_khoan INT,
  id_san_pham INT NOT NULL,
  session_id NVARCHAR(255), -- Cho khách chưa đăng nhập
  ip_address NVARCHAR(50),
  user_agent NVARCHAR(500),
  create_at DATETIME DEFAULT GETDATE(),
  FOREIGN KEY (id_tai_khoan) REFERENCES TaiKhoan(id),
  FOREIGN KEY (id_san_pham) REFERENCES SanPham(id)
);
GO

-- Index cho tìm kiếm sản phẩm
CREATE INDEX IX_SanPham_TenSanPham ON SanPham(ten_san_pham);
CREATE INDEX IX_SanPham_ThuongHieu ON SanPham(id_thuong_hieu);
CREATE INDEX IX_SanPham_DanhMuc ON SanPham(id_danh_muc);
CREATE INDEX IX_SanPham_TrangThai ON SanPham(trang_thai);
CREATE INDEX IX_SanPham_GiaBan ON SanPham(gia_ban);
CREATE INDEX IX_SanPham_Hot_Sale ON SanPham(la_hot, la_sale);

-- Index cho giỏ hàng
CREATE INDEX IX_GioHang_TaiKhoan ON GioHang(id_tai_khoan);
CREATE INDEX IX_GioHang_Session ON GioHang(session_id);

-- Index cho hóa đơn
CREATE INDEX IX_HoaDon_KhachHang ON HoaDon(id_khach_hang);
CREATE INDEX IX_HoaDon_TrangThai ON HoaDon(trang_thai);
CREATE INDEX IX_HoaDon_NgayTao ON HoaDon(create_at);

-- Index cho đánh giá
CREATE INDEX IX_DanhGia_SanPham ON DanhGiaSanPham(id_san_pham);
CREATE INDEX IX_DanhGia_TaiKhoan ON DanhGiaSanPham(id_tai_khoan);

-- ========================================
-- STORED PROCEDURES CHO WEB BÁN GIÀY
-- ========================================
GO

-- 1. Lấy danh sách sản phẩm có phân trang và filter
CREATE PROCEDURE sp_GetSanPhamList
    @PageNumber INT = 1,
    @PageSize INT = 12,
    @ThuongHieu NVARCHAR(255) = NULL,
    @DanhMuc NVARCHAR(255) = NULL,
    @GiaMin DECIMAL(10,2) = NULL,
    @GiaMax DECIMAL(10,2) = NULL,
    @KeyWord NVARCHAR(255) = NULL,
    @SapXep NVARCHAR(50) = 'moi_nhat', -- moi_nhat, gia_tang, gia_giam, ban_chay, danh_gia
    @LaHot BIT = NULL,
    @LaSale BIT = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Offset INT = (@PageNumber - 1) * @PageSize;
    
    WITH SanPhamCTE AS (
        SELECT 
            sp.id,
            sp.ma_san_pham,
            sp.ten_san_pham,
            sp.mo_ta,
            sp.gia_ban,
            sp.la_hot,
            sp.la_sale,
            sp.phan_tram_giam,
            sp.luot_xem,
            sp.luot_ban,
            sp.diem_danh_gia,
            sp.so_luong_danh_gia,
            th.ten_thuong_hieu,
            dm.ten_danh_muc,
            ha.duong_dan as hinh_anh_chinh,
            sp.create_at,
            -- Tính tổng số lượng tồn kho
            (SELECT SUM(spct.so_luong) FROM SanPhamChiTiet spct WHERE spct.id_san_pham = sp.id AND spct.trang_thai = 1) as tong_ton_kho
        FROM SanPham sp
        LEFT JOIN ThuongHieu th ON sp.id_thuong_hieu = th.id
        LEFT JOIN DanhMuc dm ON sp.id_danh_muc = dm.id
        LEFT JOIN HinhAnh ha ON sp.id_hinh_anh_dai_dien = ha.id
        WHERE sp.trang_thai = 1 
            AND sp.delete_at IS NULL
            AND (@ThuongHieu IS NULL OR th.ten_thuong_hieu = @ThuongHieu)
            AND (@DanhMuc IS NULL OR dm.ten_danh_muc = @DanhMuc)
            AND (@GiaMin IS NULL OR sp.gia_ban >= @GiaMin)
            AND (@GiaMax IS NULL OR sp.gia_ban <= @GiaMax)
            AND (@KeyWord IS NULL OR sp.ten_san_pham LIKE '%' + @KeyWord + '%')
            AND (@LaHot IS NULL OR sp.la_hot = @LaHot)
            AND (@LaSale IS NULL OR sp.la_sale = @LaSale)
    )
    SELECT *,
           COUNT(*) OVER() as TotalCount
    FROM SanPhamCTE
    ORDER BY 
        CASE WHEN @SapXep = 'moi_nhat' THEN create_at END DESC,
        CASE WHEN @SapXep = 'gia_tang' THEN gia_ban END ASC,
        CASE WHEN @SapXep = 'gia_giam' THEN gia_ban END DESC,
        CASE WHEN @SapXep = 'ban_chay' THEN luot_ban END DESC,
        CASE WHEN @SapXep = 'danh_gia' THEN diem_danh_gia END DESC
    OFFSET @Offset ROWS FETCH NEXT @PageSize ROWS ONLY;
END
GO

-- 2. Lấy chi tiết sản phẩm với tất cả thông tin
CREATE PROCEDURE sp_GetSanPhamChiTiet
    @SanPhamId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Thông tin sản phẩm chính
    SELECT 
        sp.id,
        sp.ma_san_pham,
        sp.ten_san_pham,
        sp.mo_ta,
        sp.gia_ban,
        sp.la_hot,
        sp.la_sale,
        sp.phan_tram_giam,
        sp.luot_xem,
        sp.luot_ban,
        sp.diem_danh_gia,
        sp.so_luong_danh_gia,
        th.ten_thuong_hieu,
        dm.ten_danh_muc,
        cl.ten_chat_lieu,
        xx.ten_xuat_xu,
        kd.ten_kieu_dang,
        lsp.ten_loai_san_pham
    FROM SanPham sp
    LEFT JOIN ThuongHieu th ON sp.id_thuong_hieu = th.id
    LEFT JOIN DanhMuc dm ON sp.id_danh_muc = dm.id
    LEFT JOIN ChatLieu cl ON sp.id_chat_lieu = cl.id
    LEFT JOIN XuatXu xx ON sp.id_xuat_xu = xx.id
    LEFT JOIN KieuDang kd ON sp.id_kieu_dang = kd.id
    LEFT JOIN LoaiSanPham lsp ON sp.id_loai_san_pham = lsp.id
    WHERE sp.id = @SanPhamId AND sp.trang_thai = 1 AND sp.delete_at IS NULL;
    
    -- Các size và màu có sẵn
    SELECT DISTINCT
        kc.id as kich_co_id,
        kc.ten_kich_co,
        ms.id as mau_sac_id,
        ms.ten_mau_sac,
        ms.ma_mau,
        spct.so_luong,
        spct.gia_ban_le,
        spct.id as san_pham_chi_tiet_id
    FROM SanPhamChiTiet spct
    LEFT JOIN KichCo kc ON spct.id_kich_co = kc.id
    LEFT JOIN MauSac ms ON spct.id_mau_sac = ms.id
    WHERE spct.id_san_pham = @SanPhamId 
        AND spct.trang_thai = 1 
        AND spct.delete_at IS NULL
    ORDER BY kc.ten_kich_co, ms.ten_mau_sac;
    
    -- Hình ảnh sản phẩm
    SELECT 
        ha.id,
        ha.duong_dan,
        ha.mo_ta,
        ha.thu_tu,
        ha.la_anh_chinh
    FROM HinhAnh ha
    INNER JOIN HinhAnh_SanPhamChiTiet hasp ON ha.id = hasp.id_hinh_anh
    INNER JOIN SanPhamChiTiet spct ON hasp.id_san_pham_chi_tiet = spct.id
    WHERE spct.id_san_pham = @SanPhamId 
        AND ha.delete_at IS NULL
        AND spct.delete_at IS NULL
    ORDER BY ha.thu_tu, ha.la_anh_chinh DESC;
    
    -- Cập nhật lượt xem
    UPDATE SanPham 
    SET luot_xem = luot_xem + 1,
        update_at = GETDATE()
    WHERE id = @SanPhamId;
END
GO

-- 3. Thêm sản phẩm vào giỏ hàng
CREATE PROCEDURE sp_ThemVaoGioHang
    @TaiKhoanId INT = NULL,
    @SessionId NVARCHAR(255) = NULL,
    @SanPhamChiTietId INT,
    @SoLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        
        DECLARE @GioHangId INT;
        DECLARE @SoLuongTonKho INT;
        DECLARE @GiaHienTai DECIMAL(10,2);
        
        -- Kiểm tra tồn kho
        SELECT @SoLuongTonKho = so_luong, 
               @GiaHienTai = ISNULL(gia_ban_le, (SELECT gia_ban FROM SanPham WHERE id = spct.id_san_pham))
        FROM SanPhamChiTiet spct 
        WHERE id = @SanPhamChiTietId AND trang_thai = 1;
        
        IF @SoLuongTonKho < @SoLuong
        BEGIN
            RAISERROR('Không đủ hàng trong kho', 16, 1);
            RETURN;
        END
        
        -- Tìm hoặc tạo giỏ hàng
        IF @TaiKhoanId IS NOT NULL
        BEGIN
            SELECT @GioHangId = id FROM GioHang WHERE id_tai_khoan = @TaiKhoanId AND delete_at IS NULL;
            IF @GioHangId IS NULL
            BEGIN
                INSERT INTO GioHang (id_tai_khoan) VALUES (@TaiKhoanId);
                SET @GioHangId = SCOPE_IDENTITY();
            END
        END
        ELSE
        BEGIN
            SELECT @GioHangId = id FROM GioHang WHERE session_id = @SessionId AND delete_at IS NULL;
            IF @GioHangId IS NULL
            BEGIN
                INSERT INTO GioHang (session_id) VALUES (@SessionId);
                SET @GioHangId = SCOPE_IDENTITY();
            END
        END
        
        -- Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        DECLARE @GioHangItemId INT;
        SELECT @GioHangItemId = id FROM GioHangItem 
        WHERE id_gio_hang = @GioHangId AND id_san_pham_chi_tiet = @SanPhamChiTietId AND delete_at IS NULL;
        
        IF @GioHangItemId IS NOT NULL
        BEGIN
            -- Cập nhật số lượng
            UPDATE GioHangItem 
            SET so_luong = so_luong + @SoLuong,
                gia_tai_thoi_diem = @GiaHienTai,
                update_at = GETDATE()
            WHERE id = @GioHangItemId;
        END
        ELSE
        BEGIN
            -- Thêm mới
            INSERT INTO GioHangItem (id_gio_hang, id_san_pham_chi_tiet, so_luong, gia_tai_thoi_diem)
            VALUES (@GioHangId, @SanPhamChiTietId, @SoLuong, @GiaHienTai);
        END
        
        COMMIT TRANSACTION;
        SELECT 'SUCCESS' as Result, 'Đã thêm vào giỏ hàng' as Message;
        
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SELECT 'ERROR' as Result, ERROR_MESSAGE() as Message;
    END CATCH
END
GO

-- 4. Lấy giỏ hàng
CREATE PROCEDURE sp_GetGioHang
    @TaiKhoanId INT = NULL,
    @SessionId NVARCHAR(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        ghi.id as gio_hang_item_id,
        ghi.so_luong,
        ghi.gia_tai_thoi_diem,
        sp.id as san_pham_id,
        sp.ten_san_pham,
        sp.ma_san_pham,
        th.ten_thuong_hieu,
        kc.ten_kich_co,
        ms.ten_mau_sac,
        ha.duong_dan as hinh_anh,
        spct.so_luong as ton_kho,
        (ghi.so_luong * ghi.gia_tai_thoi_diem) as thanh_tien
    FROM GioHangItem ghi
    INNER JOIN GioHang gh ON ghi.id_gio_hang = gh.id
    INNER JOIN SanPhamChiTiet spct ON ghi.id_san_pham_chi_tiet = spct.id
    INNER JOIN SanPham sp ON spct.id_san_pham = sp.id
    INNER JOIN ThuongHieu th ON sp.id_thuong_hieu = th.id
    INNER JOIN KichCo kc ON spct.id_kich_co = kc.id
    INNER JOIN MauSac ms ON spct.id_mau_sac = ms.id
    LEFT JOIN HinhAnh ha ON sp.id_hinh_anh_dai_dien = ha.id
    WHERE ghi.delete_at IS NULL 
        AND gh.delete_at IS NULL
        AND ((@TaiKhoanId IS NOT NULL AND gh.id_tai_khoan = @TaiKhoanId)
             OR (@SessionId IS NOT NULL AND gh.session_id = @SessionId))
    ORDER BY ghi.create_at DESC;
END
GO

-- 5. Tìm kiếm sản phẩm (Full-text search)
CREATE PROCEDURE sp_TimKiemSanPham
    @KeyWord NVARCHAR(255),
    @PageNumber INT = 1,
    @PageSize INT = 12
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Offset INT = (@PageNumber - 1) * @PageSize;
    
    WITH TimKiemCTE AS (
        SELECT 
            sp.id,
            sp.ma_san_pham,
            sp.ten_san_pham,
            sp.mo_ta,
            sp.gia_ban,
            sp.la_hot,
            sp.la_sale,
            sp.phan_tram_giam,
            th.ten_thuong_hieu,
            dm.ten_danh_muc,
            ha.duong_dan as hinh_anh_chinh,
            -- Tính điểm relevance
            CASE 
                WHEN sp.ten_san_pham LIKE @KeyWord + '%' THEN 100
                WHEN sp.ten_san_pham LIKE '%' + @KeyWord + '%' THEN 50
                WHEN sp.mo_ta LIKE '%' + @KeyWord + '%' THEN 25
                WHEN th.ten_thuong_hieu LIKE '%' + @KeyWord + '%' THEN 10
                ELSE 1
            END as relevance_score
        FROM SanPham sp
        LEFT JOIN ThuongHieu th ON sp.id_thuong_hieu = th.id
        LEFT JOIN DanhMuc dm ON sp.id_danh_muc = dm.id
        LEFT JOIN HinhAnh ha ON sp.id_hinh_anh_dai_dien = ha.id
        WHERE sp.trang_thai = 1 
            AND sp.delete_at IS NULL
            AND (sp.ten_san_pham LIKE '%' + @KeyWord + '%'
                 OR sp.mo_ta LIKE '%' + @KeyWord + '%'
                 OR th.ten_thuong_hieu LIKE '%' + @KeyWord + '%')
    )
    SELECT *,
           COUNT(*) OVER() as TotalCount
    FROM TimKiemCTE
    ORDER BY relevance_score DESC, ten_san_pham
    OFFSET @Offset ROWS FETCH NEXT @PageSize ROWS ONLY;
END
GO

-- 6. Lấy sản phẩm hot và sale
CREATE PROCEDURE sp_GetSanPhamHotSale
    @Loai NVARCHAR(10), -- 'hot' hoặc 'sale'
    @SoLuong INT = 8
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@SoLuong)
        sp.id,
        sp.ma_san_pham,
        sp.ten_san_pham,
        sp.gia_ban,
        sp.la_hot,
        sp.la_sale,
        sp.phan_tram_giam,
        sp.luot_xem,
        sp.luot_ban,
        th.ten_thuong_hieu,
        ha.duong_dan as hinh_anh_chinh
    FROM SanPham sp
    LEFT JOIN ThuongHieu th ON sp.id_thuong_hieu = th.id
    LEFT JOIN HinhAnh ha ON sp.id_hinh_anh_dai_dien = ha.id
    WHERE sp.trang_thai = 1 
        AND sp.delete_at IS NULL
        AND ((@Loai = 'hot' AND sp.la_hot = 1)
             OR (@Loai = 'sale' AND sp.la_sale = 1))
    ORDER BY 
        CASE WHEN @Loai = 'hot' THEN sp.luot_ban END DESC,
        CASE WHEN @Loai = 'sale' THEN sp.phan_tram_giam END DESC,
        sp.create_at DESC;
END
GO

-- 7. Tạo hóa đơn đơn giản
CREATE PROCEDURE sp_TaoHoaDon
    @TaiKhoanId INT = NULL,
    @SessionId NVARCHAR(255) = NULL,
    @HoTenKhachHang NVARCHAR(255),
    @SdtKhachHang NVARCHAR(20),
    @EmailKhachHang NVARCHAR(100) = NULL,
    @DiaChiGiaoHang NVARCHAR(500),
    @PhuongThucThanhToan TINYINT = 1, -- 1: Tiền mặt, 2: Chuyển khoản
    @GhiChu NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        
        DECLARE @KhachHangId INT;
        DECLARE @HoaDonId INT;
        DECLARE @MaHoaDon VARCHAR(50);
        DECLARE @TongTien DECIMAL(10,2) = 0;
        
        -- Tạo mã hóa đơn
        SET @MaHoaDon = 'HD' + FORMAT(GETDATE(), 'yyyyMMdd') + RIGHT('000000' + CAST(ABS(CHECKSUM(NEWID())) % 1000000 AS VARCHAR), 6);
        
        -- Tìm hoặc tạo khách hàng
        IF @TaiKhoanId IS NOT NULL
        BEGIN
            SELECT @KhachHangId = id FROM KhachHang WHERE id_tai_khoan = @TaiKhoanId;
            IF @KhachHangId IS NULL
            BEGIN
                INSERT INTO KhachHang (id_tai_khoan, ho_ten_khach_hang, sdt, email, la_khach_vang_lai)
                VALUES (@TaiKhoanId, @HoTenKhachHang, @SdtKhachHang, @EmailKhachHang, 0);
                SET @KhachHangId = SCOPE_IDENTITY();
            END
        END
        ELSE
        BEGIN
            INSERT INTO KhachHang (ho_ten_khach_hang, sdt, email, la_khach_vang_lai)
            VALUES (@HoTenKhachHang, @SdtKhachHang, @EmailKhachHang, 1);
            SET @KhachHangId = SCOPE_IDENTITY();
        END
        
        -- Tính tổng tiền từ giỏ hàng
        SELECT @TongTien = SUM(ghi.so_luong * ghi.gia_tai_thoi_diem)
        FROM GioHangItem ghi
        INNER JOIN GioHang gh ON ghi.id_gio_hang = gh.id
        WHERE ghi.delete_at IS NULL 
            AND gh.delete_at IS NULL
            AND ((@TaiKhoanId IS NOT NULL AND gh.id_tai_khoan = @TaiKhoanId)
                 OR (@SessionId IS NOT NULL AND gh.session_id = @SessionId));
        
        IF @TongTien <= 0
        BEGIN
            RAISERROR('Giỏ hàng trống', 16, 1);
            RETURN;
        END
        
        -- Tạo hóa đơn
        INSERT INTO HoaDon (id_khach_hang, ma_hoa_don, tong_tien, tong_thanh_toan, 
                           phuong_thuc_thanh_toan, dia_chi_giao_hang, ghi_chu)
        VALUES (@KhachHangId, @MaHoaDon, @TongTien, @TongTien, 
                @PhuongThucThanhToan, @DiaChiGiaoHang, @GhiChu);
        
        SET @HoaDonId = SCOPE_IDENTITY();
        
        -- Tạo chi tiết hóa đơn từ giỏ hàng
        INSERT INTO HoaDonChiTiet (id_san_pham_chi_tiet, id_hoa_don, ma_hoa_don_chi_tiet, 
                                  gia_nhap, gia_ban, so_luong, thanh_tien)
        SELECT 
            ghi.id_san_pham_chi_tiet,
            @HoaDonId,
            @MaHoaDon + '_' + CAST(ROW_NUMBER() OVER (ORDER BY ghi.id) AS VARCHAR),
            sp.gia_nhap,
            ghi.gia_tai_thoi_diem,
            ghi.so_luong,
            ghi.so_luong * ghi.gia_tai_thoi_diem
        FROM GioHangItem ghi
        INNER JOIN GioHang gh ON ghi.id_gio_hang = gh.id
        INNER JOIN SanPhamChiTiet spct ON ghi.id_san_pham_chi_tiet = spct.id
        INNER JOIN SanPham sp ON spct.id_san_pham = sp.id
        WHERE ghi.delete_at IS NULL 
            AND gh.delete_at IS NULL
            AND ((@TaiKhoanId IS NOT NULL AND gh.id_tai_khoan = @TaiKhoanId)
                 OR (@SessionId IS NOT NULL AND gh.session_id = @SessionId));
        
        -- Cập nhật tồn kho
        UPDATE SanPhamChiTiet 
        SET so_luong = spct.so_luong - ghi.so_luong,
            update_at = GETDATE()
        FROM SanPhamChiTiet spct
        INNER JOIN GioHangItem ghi ON spct.id = ghi.id_san_pham_chi_tiet
        INNER JOIN GioHang gh ON ghi.id_gio_hang = gh.id
        WHERE ghi.delete_at IS NULL 
            AND gh.delete_at IS NULL
            AND ((@TaiKhoanId IS NOT NULL AND gh.id_tai_khoan = @TaiKhoanId)
                 OR (@SessionId IS NOT NULL AND gh.session_id = @SessionId));
        
        -- Cập nhật lượt bán sản phẩm
        UPDATE SanPham 
        SET luot_ban = luot_ban + ghi.so_luong,
            update_at = GETDATE()
        FROM SanPham sp
        INNER JOIN SanPhamChiTiet spct ON sp.id = spct.id_san_pham
        INNER JOIN GioHangItem ghi ON spct.id = ghi.id_san_pham_chi_tiet
        INNER JOIN GioHang gh ON ghi.id_gio_hang = gh.id
        WHERE ghi.delete_at IS NULL 
            AND gh.delete_at IS NULL
            AND ((@TaiKhoanId IS NOT NULL AND gh.id_tai_khoan = @TaiKhoanId)
                 OR (@SessionId IS NOT NULL AND gh.session_id = @SessionId));
        
        -- Xóa giỏ hàng
        UPDATE GioHangItem 
        SET delete_at = GETDATE()
        FROM GioHangItem ghi
        INNER JOIN GioHang gh ON ghi.id_gio_hang = gh.id
        WHERE ((@TaiKhoanId IS NOT NULL AND gh.id_tai_khoan = @TaiKhoanId)
               OR (@SessionId IS NOT NULL AND gh.session_id = @SessionId));
        
        -- Ghi lịch sử trạng thái đơn hàng
        INSERT INTO HoaDonTrangThai (id_hoa_don, trang_thai_moi, ghi_chu, nguoi_thuc_hien)
        VALUES (@HoaDonId, 0, 'Đơn hàng được tạo', @HoTenKhachHang);
        
        COMMIT TRANSACTION;
        
        SELECT 'SUCCESS' as Result, @MaHoaDon as MaHoaDon, @HoaDonId as HoaDonId;
        
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SELECT 'ERROR' as Result, ERROR_MESSAGE() as Message;
    END CATCH
END
GO

-- 8. Thống kê doanh thu đơn giản
CREATE PROCEDURE sp_ThongKeDoanhThu
    @TuNgay DATE = NULL,
    @DenNgay DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    IF @TuNgay IS NULL SET @TuNgay = DATEADD(day, -30, GETDATE());
    IF @DenNgay IS NULL SET @DenNgay = GETDATE();
    
    SELECT 
        COUNT(*) as tong_don_hang,
        SUM(tong_thanh_toan) as tong_doanh_thu,
        AVG(tong_thanh_toan) as gia_tri_don_hang_trung_binh,
        COUNT(CASE WHEN trang_thai = 3 THEN 1 END) as don_hang_hoan_thanh,
        COUNT(CASE WHEN trang_thai = 4 THEN 1 END) as don_hang_huy
    FROM HoaDon
    WHERE CAST(create_at AS DATE) BETWEEN @TuNgay AND @DenNgay;
    
    -- Doanh thu theo ngày
    SELECT 
        CAST(create_at AS DATE) as ngay,
        COUNT(*) as so_don_hang,
        SUM(tong_thanh_toan) as doanh_thu
    FROM HoaDon
    WHERE CAST(create_at AS DATE) BETWEEN @TuNgay AND @DenNgay
    GROUP BY CAST(create_at AS DATE)
    ORDER BY ngay DESC;
END
GO
