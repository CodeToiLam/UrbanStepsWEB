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
  mo_ta NVARCHAR(500),
  gia_nhap DECIMAL(10,2) NOT NULL,
  gia_ban DECIMAL(10,2) NOT NULL,
  trang_thai BIT DEFAULT 0,
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
  ten_kich_co NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE MauSac (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ten_mau_sac NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE SanPhamChiTiet (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_san_pham INT,
  id_kich_co INT,
  id_mau_sac INT,
  so_luong INT,
  trang_thai BIT DEFAULT 0,
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_san_pham) REFERENCES SanPham(id),
  FOREIGN KEY (id_kich_co) REFERENCES KichCo(id),
  FOREIGN KEY (id_mau_sac) REFERENCES MauSac(id)
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
  ho_ten_khach_hang NVARCHAR(255) NOT NULL,
  sdt NVARCHAR(20),
  gioi_tinh BIT DEFAULT 0,
  dia_chi NVARCHAR(500),
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME
);
GO

CREATE TABLE phieu_giam_gia (
  id INT IDENTITY(1,1) PRIMARY KEY,
  ma_phieu_giam_gia VARCHAR(50) NOT NULL UNIQUE,
  ten_phieu_giam_gia NVARCHAR(255) NOT NULL,
  so_luong INT,
  ngay_bat_dau DATE,
  ngay_ket_thuc DATE,
  giam_theo_phan_tram BIT DEFAULT 0,
  gia_tri_giam DECIMAL(10,2),
  giam_toi_da DECIMAL(10,2),
  don_toi_thieu DECIMAL(10,2),
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME
);
GO

CREATE TABLE HoaDon (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_tai_khoan INT,
  id_khach_hang INT,
  id_phieu_giam_gia INT,
  ma_hoa_don VARCHAR(50) NOT NULL UNIQUE,
  tien_mat DECIMAL(10,2),
  tien_chuyen_khoan DECIMAL(10,2),
  tien_giam DECIMAL(10,2),
  phuong_thuc_thanh_toan TINYINT,
  trang_thai BIT DEFAULT 0,
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_tai_khoan) REFERENCES TaiKhoan(id),
  FOREIGN KEY (id_khach_hang) REFERENCES KhachHang(id),
  FOREIGN KEY (id_phieu_giam_gia) REFERENCES phieu_giam_gia(id)
);
GO

CREATE TABLE HoaDonChiTiet (
  id INT IDENTITY(1,1) PRIMARY KEY,
  id_san_pham_chi_tiet INT,
  id_hoa_don INT,
  ma_hoa_don_chi_tiet VARCHAR(50) NOT NULL UNIQUE,
  gia_nhap DECIMAL(10,2),
  gia_ban DECIMAL(10,2),
  so_luong INT,
  create_at DATETIME DEFAULT GETDATE(),
  update_at DATETIME,
  delete_at DATETIME,
  FOREIGN KEY (id_san_pham_chi_tiet) REFERENCES SanPhamChiTiet(id),
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
