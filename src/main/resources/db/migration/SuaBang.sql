-- thêm giá cũ và ngày thay đổi giá vào bảng SanPhamChiTiet
-- Các cột này được sử dụng để theo dõi lịch sử giá và tính toán chiết khấu
ALTER TABLE SanPhamChiTiet ADD gia_cu DECIMAL(19,2) NULL;
ALTER TABLE SanPhamChiTiet ADD ngay_thay_doi_gia DATETIME NULL;
