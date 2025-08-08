CREATE TABLE StockNotification (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NULL,
    email NVARCHAR(255) NULL,
    san_pham_chi_tiet_id INT NOT NULL,
    created_at DATETIME NOT NULL,
    notified BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_StockNotification_TaiKhoan FOREIGN KEY (user_id) REFERENCES TaiKhoan(id),
    CONSTRAINT FK_StockNotification_SanPhamChiTiet FOREIGN KEY (san_pham_chi_tiet_id) REFERENCES SanPhamChiTiet(id)
);

-- thêm chỉ mục để tăng tốc độ truy vấn
CREATE INDEX IX_StockNotification_ProductNotified ON StockNotification(san_pham_chi_tiet_id, notified);
-- tạo bảng StockNotification để theo dõi yêu cầu thông báo khi sản phẩm hết hàng
-- Bảng này sẽ lưu thông tin người dùng, email và sản phẩm chi tiết
-- Bảng này được sử dụng để theo dõi các yêu cầu thông báo khi sản phẩm hết hàng trở lại có sẵn
