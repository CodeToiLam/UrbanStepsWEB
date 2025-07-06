------NẾU LỖI ẢNH THÌ CHẠY CÁI NÀY, CÓ THỂ SỬA ĐC ĐA SỐ
USE UrbanStepsDB;
GO

PRINT N'Bắt đầu cập nhật đường dẫn hình ảnh...';

PRINT N'Cập nhật banner images...';

-- Thêm banner images mới
INSERT INTO HinhAnh (duong_dan, mo_ta, thu_tu, la_anh_chinh) VALUES 
('images/Home/banner adidas.jpg', N'Banner Adidas', 1, 1),
('images/Home/banner converse.jpg', N'Banner Converse', 2, 1),
('images/Home/banner mlb.jpg', N'Banner MLB', 3, 1);
GO

PRINT N'Cập nhật hình ảnh Adidas...';

-- Cập nhật các sản phẩm Adidas còn thiếu đường dẫn đúng
UPDATE HinhAnh SET duong_dan = 'adidas/adifom-supernova-triple-black/main.jpg' WHERE duong_dan LIKE '%adifom-supernova-triple-black%';
UPDATE HinhAnh SET duong_dan = 'adidas/adifom-superstar-core-black/main.jpg' WHERE duong_dan LIKE '%adifom-superstar-core-black%';
UPDATE HinhAnh SET duong_dan = 'adidas/adifom-superstar-core-white/main.jpg' WHERE duong_dan LIKE '%adifom-superstar-core-white%';
UPDATE HinhAnh SET duong_dan = 'adidas/barricade-13-tennis-black/main.jpg' WHERE duong_dan LIKE '%barricade-13-tennis-black%';
UPDATE HinhAnh SET duong_dan = 'adidas/gazelle-bold-year-of-the-snake/main.jpg' WHERE duong_dan LIKE '%gazelle-bold-year-of-the-snake%';
UPDATE HinhAnh SET duong_dan = 'adidas/run-eq21-black/main.jpg' WHERE duong_dan LIKE '%run-eq21-black%';
UPDATE HinhAnh SET duong_dan = 'adidas/samba-og-cloud-white-wonder-quartz-wmns/main.jpg' WHERE duong_dan LIKE '%samba-og-cloud-white-wonder-quartz-wmns%';
UPDATE HinhAnh SET duong_dan = 'adidas/samba-og-white-black-gum/main.jpg' WHERE duong_dan LIKE '%samba-og-white-black-gum%';
UPDATE HinhAnh SET duong_dan = 'adidas/samba-og-wonder-white-maroon/main.jpg' WHERE duong_dan LIKE '%samba-og-wonder-white-maroon%';
UPDATE HinhAnh SET duong_dan = 'adidas/tennispickleball-adidas-adizero-ubersonic-4-crystal-white-semi-flash-aqua/main.jpg' WHERE duong_dan LIKE '%adizero-ubersonic-4-crystal-white%';
UPDATE HinhAnh SET duong_dan = 'adidas/tennispickleball-adidas-solematch-control-2-semi-flash-aqua/main.jpg' WHERE duong_dan LIKE '%solematch-control-2-semi-flash-aqua%';
UPDATE HinhAnh SET duong_dan = 'adidas/th-thao-adidas-ultraboost-21-cloud-white/main.jpg' WHERE duong_dan LIKE '%ultraboost-21-cloud-white%';
UPDATE HinhAnh SET duong_dan = 'adidas/yeezy-boost-350-v2-steel-grey/main.jpg' WHERE duong_dan LIKE '%yeezy-boost-350-v2-steel-grey%';
GO

PRINT N'Cập nhật hình ảnh Converse...';

-- Converse với extension .webp
UPDATE HinhAnh SET duong_dan = 'converse/chuck-taylor-all-star-low-flame/main.webp' WHERE duong_dan LIKE '%chuck-taylor-all-star-low-flame%';
UPDATE HinhAnh SET duong_dan = 'converse/comme-des-gar-ons-play-x-chuck-70-low-black-white/main.webp' WHERE duong_dan LIKE '%comme-des-garcons-play-x-chuck-70-low-black-white%';
UPDATE HinhAnh SET duong_dan = 'converse/comme-des-gar-ons-x-chuck-taylor-all-star-hi-milk/main.webp' WHERE duong_dan LIKE '%comme-des-garcons-x-chuck-taylor-all-star-hi-milk%';

-- Converse với extension .jpg
UPDATE HinhAnh SET duong_dan = 'converse/aeon-active-cx-himalayan-salt/main.jpg' WHERE duong_dan LIKE '%aeon-active-cx-himalayan-salt%';
UPDATE HinhAnh SET duong_dan = 'converse/aeon-active-cx-ox-egret/main.jpg' WHERE duong_dan LIKE '%aeon-active-cx-ox-egret%';
UPDATE HinhAnh SET duong_dan = 'converse/back-to-the-future-converse-all-star-us-mt-hi-black/main.jpg' WHERE duong_dan LIKE '%back-to-the-future-converse-all-star-us-mt-hi-black%';
UPDATE HinhAnh SET duong_dan = 'converse/chuck-taylor-all-star-cruise-ox-black-white/main.jpg' WHERE duong_dan LIKE '%chuck-taylor-all-star-cruise-ox-black-white%';
UPDATE HinhAnh SET duong_dan = 'converse/chuck-taylor-all-star-lift-ox-white-black/main.jpg' WHERE duong_dan LIKE '%chuck-taylor-all-star-lift-ox-white-black%';
UPDATE HinhAnh SET duong_dan = 'converse/kim-jones-x-converse-chuck-70-all-star-black/main.jpg' WHERE duong_dan LIKE '%kim-jones-x-converse-chuck-70-all-star-black%';
UPDATE HinhAnh SET duong_dan = 'converse/run-star-hike-low-black-gum/main.jpg' WHERE duong_dan LIKE '%run-star-hike-low-black-gum%';
UPDATE HinhAnh SET duong_dan = 'converse/run-star-motion-low-white/main.jpg' WHERE duong_dan LIKE '%run-star-motion-low-white%';
UPDATE HinhAnh SET duong_dan = 'converse/x-rick-owens-drkshdw-weapon-beige-black/main.jpg' WHERE duong_dan LIKE '%x-rick-owens-drkshdw-weapon-beige-black%';

-- Cập nhật tên thư mục đặc biệt
UPDATE HinhAnh SET duong_dan = 'converse/Shoes-Comme-des-Garcons-Play-x-Converse-Chuck-Taylor-All-Star-70-Hi-Black-150204C/main.jpg' WHERE duong_dan LIKE '%comme-des-garcons-play-x-converse-chuck-taylor-all-star-70-hi-black%';
UPDATE HinhAnh SET duong_dan = 'converse/Shoes-Converse-Chuck-Taylor-All-Star-EVA-Lift-Platform-Y2K-Heart-High-Top-Black-A09121C/main.jpg' WHERE duong_dan LIKE '%chuck-taylor-all-star-eva-lift-platform-y2k-heart-high-top-black%';
GO

PRINT N'Cập nhật hình ảnh MLB...';

-- Tất cả MLB đều dùng .webp
UPDATE HinhAnh SET duong_dan = 'mlb/bigball-chunky-a-new-york-yankees-3ashc101n/main.webp' WHERE duong_dan LIKE '%bigball-chunky-a-new-york-yankees%';
UPDATE HinhAnh SET duong_dan = 'mlb/bigball-chunky-diamond-monogram-boston-red-sox-d-beige-3ashcdm2n/main.webp' WHERE duong_dan LIKE '%bigball-chunky-diamond-monogram-boston-red-sox%';
UPDATE HinhAnh SET duong_dan = 'mlb/bigball-chunky-diamond-monogram-new-york-yankees-black-3ashcdm2n/main.webp' WHERE duong_dan LIKE '%bigball-chunky-diamond-monogram-new-york-yankees-black%';
UPDATE HinhAnh SET duong_dan = 'mlb/bigball-chunky-p-boston-red-sox-32shc2111-43i/main.webp' WHERE duong_dan LIKE '%bigball-chunky-p-boston-red-sox%';
UPDATE HinhAnh SET duong_dan = 'mlb/chunky-liner-low-boston-red-sox-beige-3asxca12n/main.webp' WHERE duong_dan LIKE '%chunky-liner-low-boston-red-sox-beige%';
UPDATE HinhAnh SET duong_dan = 'mlb/chunky-liner-mid-denim-boston-red-sox-d-blue-3asxcdn3n/main.webp' WHERE duong_dan LIKE '%chunky-liner-mid-denim-boston-red-sox%';
UPDATE HinhAnh SET duong_dan = 'mlb/chunky-liner-mid-denim-new-york-yankees-d-navy-3asxcdn3n/main.webp' WHERE duong_dan LIKE '%chunky-liner-mid-denim-new-york-yankees%';
UPDATE HinhAnh SET duong_dan = 'mlb/chunky-liner-new-york-yankees-grey-3asxca12n/main.webp' WHERE duong_dan LIKE '%chunky-liner-new-york-yankees-grey%';
UPDATE HinhAnh SET duong_dan = 'mlb/chunky-liner-sl-saffiano-boston-red-sox-3asxcls4n/main.webp' WHERE duong_dan LIKE '%chunky-liner-sl-saffiano-boston-red-sox%';
UPDATE HinhAnh SET duong_dan = 'mlb/liner-basic-new-york-yankees-black-3asxclb3n/main.webp' WHERE duong_dan LIKE '%liner-basic-new-york-yankees-black%';
UPDATE HinhAnh SET duong_dan = 'mlb/liner-basic-new-york-yankees-green-3asxclb3n/main.webp' WHERE duong_dan LIKE '%liner-basic-new-york-yankees-green%';
UPDATE HinhAnh SET duong_dan = 'mlb/playball-mule-dia-monogram-new-york-yankees-beige-3amumda2n/main.webp' WHERE duong_dan LIKE '%playball-mule-dia-monogram-new-york-yankees-beige%';
UPDATE HinhAnh SET duong_dan = 'mlb/playball-mule-mono-ny-new-york-yankees-32shsm111-50l/main.webp' WHERE duong_dan LIKE '%playball-mule-mono-ny-new-york-yankees%';
GO


PRINT N'Thêm sản phẩm mới nếu cần...';

-- Kiểm tra và thêm hình ảnh cho các sản phẩm có thể bị thiếu
INSERT INTO HinhAnh (duong_dan, mo_ta, thu_tu, la_anh_chinh)
SELECT 'adidas/adifom-supernova-triple-black/main.jpg', N'Adidas adiFOM Supernova Triple Black', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM HinhAnh WHERE duong_dan LIKE '%adifom-supernova-triple-black%');

INSERT INTO HinhAnh (duong_dan, mo_ta, thu_tu, la_anh_chinh)
SELECT 'adidas/gazelle-bold-year-of-the-snake/main.jpg', N'Adidas Gazelle Bold Year of the Snake', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM HinhAnh WHERE duong_dan LIKE '%gazelle-bold-year-of-the-snake%');

INSERT INTO HinhAnh (duong_dan, mo_ta, thu_tu, la_anh_chinh)
SELECT 'converse/run-star-hike-low-black-gum/main.jpg', N'Converse Run Star Hike Low Black Gum', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM HinhAnh WHERE duong_dan LIKE '%run-star-hike-low-black-gum%');

INSERT INTO HinhAnh (duong_dan, mo_ta, thu_tu, la_anh_chinh)
SELECT 'converse/run-star-motion-low-white/main.jpg', N'Converse Run Star Motion Low White', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM HinhAnh WHERE duong_dan LIKE '%run-star-motion-low-white%');
GO

PRINT N'Cập nhật liên kết sản phẩm với hình ảnh...';

-- Cập nhật id_hinh_anh_dai_dien cho các sản phẩm
UPDATE SanPham 
SET id_hinh_anh_dai_dien = (SELECT TOP 1 id FROM HinhAnh WHERE duong_dan LIKE '%adifom-supernova-triple-black%')
WHERE ten_san_pham LIKE '%adiFOM Supernova Triple Black%';

UPDATE SanPham 
SET id_hinh_anh_dai_dien = (SELECT TOP 1 id FROM HinhAnh WHERE duong_dan LIKE '%gazelle-bold-year-of-the-snake%')
WHERE ten_san_pham LIKE '%Gazelle Bold Year of the Snake%';

UPDATE SanPham 
SET id_hinh_anh_dai_dien = (SELECT TOP 1 id FROM HinhAnh WHERE duong_dan LIKE '%run-star-hike-low-black-gum%')
WHERE ten_san_pham LIKE '%Run Star Hike Low Black Gum%';

UPDATE SanPham 
SET id_hinh_anh_dai_dien = (SELECT TOP 1 id FROM HinhAnh WHERE duong_dan LIKE '%run-star-motion-low-white%')
WHERE ten_san_pham LIKE '%Run Star Motion Low White%';
GO

PRINT N'Kiểm tra kết quả...';

-- Đếm số lượng hình ảnh theo thương hiệu
SELECT 
    'Adidas' as Brand,
    COUNT(*) as ImageCount
FROM HinhAnh 
WHERE duong_dan LIKE 'adidas/%'

UNION ALL

SELECT 
    'Converse' as Brand,
    COUNT(*) as ImageCount
FROM HinhAnh 
WHERE duong_dan LIKE 'converse/%'

UNION ALL

SELECT 
    'MLB' as Brand,
    COUNT(*) as ImageCount
FROM HinhAnh 
WHERE duong_dan LIKE 'mlb/%'

UNION ALL

SELECT 
    'Banner' as Brand,
    COUNT(*) as ImageCount
FROM HinhAnh 
WHERE duong_dan LIKE 'images/Home/banner%';

-- Hiển thị tất cả đường dẫn hình ảnh
SELECT 
    h.id,
    h.duong_dan,
    h.mo_ta,
    s.ten_san_pham
FROM HinhAnh h
LEFT JOIN SanPham s ON s.id_hinh_anh_dai_dien = h.id
ORDER BY h.duong_dan;

PRINT N'Hoàn thành cập nhật đường dẫn hình ảnh!';
GO
