
-- D? LI?U M?U CHO URBANSTEPS DATABASE 
USE UrbanStepsDB;
GO

-- X�a d? li?u cu
DELETE FROM GioHangItem;
DELETE FROM GioHang;
DELETE FROM HinhAnh_SanPhamChiTiet;
DELETE FROM SanPhamChiTiet;
DELETE FROM SanPham;
DELETE FROM HinhAnh;
DELETE FROM phieu_giam_gia;
DELETE FROM KhachHang;
DELETE FROM TaiKhoan;
DELETE FROM MauSac;
DELETE FROM KichCo;
DELETE FROM ChatLieu;
DELETE FROM KieuDang;
DELETE FROM XuatXu;
DELETE FROM DanhMuc;
DELETE FROM LoaiSanPham;
DELETE FROM ThuongHieu;
GO

-- Reset IDENTITY
DBCC CHECKIDENT ('GioHangItem', RESEED, 0);
DBCC CHECKIDENT ('GioHang', RESEED, 0);
DBCC CHECKIDENT ('HinhAnh_SanPhamChiTiet', RESEED, 0);
DBCC CHECKIDENT ('SanPhamChiTiet', RESEED, 0);
DBCC CHECKIDENT ('SanPham', RESEED, 0);
DBCC CHECKIDENT ('HinhAnh', RESEED, 0);
DBCC CHECKIDENT ('phieu_giam_gia', RESEED, 0);
DBCC CHECKIDENT ('KhachHang', RESEED, 0);
DBCC CHECKIDENT ('TaiKhoan', RESEED, 0);
DBCC CHECKIDENT ('MauSac', RESEED, 0);
DBCC CHECKIDENT ('KichCo', RESEED, 0);
DBCC CHECKIDENT ('ChatLieu', RESEED, 0);
DBCC CHECKIDENT ('KieuDang', RESEED, 0);
DBCC CHECKIDENT ('XuatXu', RESEED, 0);
DBCC CHECKIDENT ('DanhMuc', RESEED, 0);
DBCC CHECKIDENT ('LoaiSanPham', RESEED, 0);
DBCC CHECKIDENT ('ThuongHieu', RESEED, 0);
GO

-- 1. Thuong hi?u
INSERT INTO ThuongHieu (ten_thuong_hieu) VALUES 
(N'Adidas'),
(N'Converse'),
(N'MLB'),
(N'Nike'),
(N'Vans');
GO

-- 2. Lo?i s?n ph?m
INSERT INTO LoaiSanPham (ten_loai_san_pham) VALUES 
(N'Giày thể thao'),
(N'Giày chạy bộ'),
(N'Giày tennis'),
(N'Giày lifestyle'),
(N'Giày cao cổ'),
(N'Giày thấp cổ'),
(N'Giày chunky'),
(N'Giày platform'),
(N'Giày slip-on'),
(N'Giày collaboration');
GO

-- 3. Danh m?c
-- 3. Danh mục
INSERT INTO DanhMuc (ten_danh_muc) VALUES 
(N'Nam'),
(N'Nữ'),
(N'Unisex');
GO

-- 4. Xuất xứ
INSERT INTO XuatXu (ten_xuat_xu) VALUES 
(N'Việt Nam'),
(N'Đức'),
(N'Mỹ'),
(N'Hàn Quốc'),
(N'Trung Quốc'),
(N'Indonesia'),
(N'Thái Lan');
GO

-- 5. Kiểu dáng
INSERT INTO KieuDang (ten_kieu_dang) VALUES 
(N'Thể thao'),
(N'Cao cổ'),
(N'Thấp cổ'),
(N'Platform'),
(N'Chunky'),
(N'Classic'),
(N'Retro'),
(N'Slip-on');
GO

-- 6. Chất liệu
INSERT INTO ChatLieu (ten_chat_lieu) VALUES 
(N'Da'),
(N'Canvas'),
(N'Synthetic'),
(N'Mesh'),
(N'Leather'),
(N'Suede'),
(N'Primeknit'),
(N'Flyknit'),
(N'Boost'),
(N'EVA'),
(N'Denim'),
(N'Saffiano');
GO

-- 7. K�ch c?
INSERT INTO KichCo (ten_kich_co) VALUES 
(N'35'), (N'36'), (N'37'), (N'38'), (N'39'), (N'40'), (N'41'), (N'42'), (N'43'), (N'44'), (N'45'), (N'46');
GO

-- 8. M�u s?c
INSERT INTO MauSac (ten_mau_sac) VALUES 
(N'�en'), (N'Tr?ng'), (N'X�m'), (N'Xanh Navy'), (N'�?'), (N'V�ng'), (N'N�u'), (N'H?ng'),
(N'Xanh Duong'), (N'Xanh L�'), (N'T�m'), (N'Cam'), (N'B�'), (N'X�m Nh?t'), (N'�en Tr?ng');
GO

-- 9. H�nh ?nh
INSERT INTO HinhAnh (duong_dan, mo_ta, thu_tu, la_anh_chinh) VALUES 
-- ?nh placeholder
(N'images/Home/placeholder.jpg', N'?nh placeholder', 1, 1),
-- ?nh Adidas (13 s?n ph?m x 4 ?nh = 52 ?nh)
(N'adidas/samba-og-white-black-gum/main.jpg', N'Adidas Samba OG White Black Gum', 1, 1),
(N'adidas/samba-og-white-black-gum/side.jpg', N'Adidas Samba OG - Side view', 2, 0),
(N'adidas/samba-og-white-black-gum/back.jpg', N'Adidas Samba OG - Back view', 3, 0),
(N'adidas/samba-og-white-black-gum/sole.jpg', N'Adidas Samba OG - Sole view', 4, 0),
(N'adidas/samba-og-wonder-white-maroon/main.jpg', N'Adidas Samba OG Wonder White Maroon', 1, 1),
(N'adidas/samba-og-wonder-white-maroon/side.jpg', N'Adidas Samba OG Maroon - Side view', 2, 0),
(N'adidas/samba-og-wonder-white-maroon/detail.jpg', N'Adidas Samba OG Maroon - Detail view', 3, 0),
(N'adidas/samba-og-wonder-white-maroon/back.jpg', N'Adidas Samba OG Maroon - Back view', 4, 0),
(N'adidas/samba-og-cloud-white-wonder-quartz-wmns/main.jpg', N'Adidas Samba OG Cloud White Wonder Quartz Wmns', 1, 1),
(N'adidas/samba-og-cloud-white-wonder-quartz-wmns/side.jpg', N'Adidas Samba OG Quartz - Side view', 2, 0),
(N'adidas/samba-og-cloud-white-wonder-quartz-wmns/detail.jpg', N'Adidas Samba OG Quartz - Detail view', 3, 0),
(N'adidas/samba-og-cloud-white-wonder-quartz-wmns/lifestyle.jpg', N'Adidas Samba OG Quartz - Lifestyle', 4, 0),
(N'adidas/gazelle-bold-year-of-the-snake/main.jpg', N'Adidas Gazelle Bold Year of the Snake', 1, 1),
(N'adidas/gazelle-bold-year-of-the-snake/side.jpg', N'Adidas Gazelle Bold Snake - Side view', 2, 0),
(N'adidas/gazelle-bold-year-of-the-snake/detail.jpg', N'Adidas Gazelle Bold Snake - Detail view', 3, 0),
(N'adidas/gazelle-bold-year-of-the-snake/platform.jpg', N'Adidas Gazelle Bold Snake - Platform view', 4, 0),
(N'adidas/yeezy-boost-350-v2-steel-grey/main.jpg', N'Adidas Yeezy Boost 350 V2 Steel Grey', 1, 1),
(N'adidas/yeezy-boost-350-v2-steel-grey/side.jpg', N'Adidas Yeezy 350 V2 - Side view', 2, 0),
(N'adidas/yeezy-boost-350-v2-steel-grey/primeknit.jpg', N'Adidas Yeezy 350 V2 - Primeknit detail', 3, 0),
(N'adidas/yeezy-boost-350-v2-steel-grey/boost.jpg', N'Adidas Yeezy 350 V2 - Boost sole', 4, 0),
(N'adidas/adifom-superstar-core-black/main.jpg', N'Adidas adiFOM Superstar Core Black', 1, 1),
(N'adidas/adifom-superstar-core-black/side.jpg', N'Adidas adiFOM Superstar Black - Side view', 2, 0),
(N'adidas/adifom-superstar-core-black/detail.jpg', N'Adidas adiFOM Superstar Black - Detail view', 3, 0),
(N'adidas/adifom-superstar-core-black/foam.jpg', N'Adidas adiFOM Superstar Black - Foam tech', 4, 0),
(N'adidas/adifom-superstar-core-white/main.jpg', N'Adidas adiFOM Superstar Core White', 1, 1),
(N'adidas/adifom-superstar-core-white/side.jpg', N'Adidas adiFOM Superstar White - Side view', 2, 0),
(N'adidas/adifom-superstar-core-white/detail.jpg', N'Adidas adiFOM Superstar White - Detail view', 3, 0),
(N'adidas/adifom-superstar-core-white/clean.jpg', N'Adidas adiFOM Superstar White - Clean look', 4, 0),
(N'adidas/adifom-supernova-triple-black/main.jpg', N'Adidas adiFOM Supernova Triple Black', 1, 1),
(N'adidas/adifom-supernova-triple-black/side.jpg', N'Adidas adiFOM Supernova - Side view', 2, 0),
(N'adidas/adifom-supernova-triple-black/running.jpg', N'Adidas adiFOM Supernova - Running view', 3, 0),
(N'adidas/adifom-supernova-triple-black/tech.jpg', N'Adidas adiFOM Supernova - Tech detail', 4, 0),
(N'adidas/ultraboost-21-cloud-white/main.jpg', N'Adidas UltraBoost 21 Cloud White', 1, 1),
(N'adidas/ultraboost-21-cloud-white/side.jpg', N'Adidas UltraBoost 21 - Side view', 2, 0),
(N'adidas/ultraboost-21-cloud-white/boost.jpg', N'Adidas UltraBoost 21 - Boost sole', 3, 0),
(N'adidas/ultraboost-21-cloud-white/primeknit.jpg', N'Adidas UltraBoost 21 - Primeknit upper', 4, 0),
(N'adidas/run-eq21-black/main.jpg', N'Adidas Run EQ21 Black', 1, 1),
(N'adidas/run-eq21-black/side.jpg', N'Adidas Run EQ21 - Side view', 2, 0),
(N'adidas/run-eq21-black/running.jpg', N'Adidas Run EQ21 - Running shot', 3, 0),
(N'adidas/run-eq21-black/comfort.jpg', N'Adidas Run EQ21 - Comfort detail', 4, 0),
(N'adidas/barricade-13-tennis-black/main.jpg', N'Adidas Barricade 13 Tennis Black', 1, 1),
(N'adidas/barricade-13-tennis-black/side.jpg', N'Adidas Barricade 13 - Side view', 2, 0),
(N'adidas/barricade-13-tennis-black/court.jpg', N'Adidas Barricade 13 - Court ready', 3, 0),
(N'adidas/barricade-13-tennis-black/durability.jpg', N'Adidas Barricade 13 - Durability view', 4, 0),
(N'adidas/adizero-ubersonic-4-crystal-white/main.jpg', N'Adidas Adizero Ubersonic 4 Crystal White', 1, 1),
(N'adidas/adizero-ubersonic-4-crystal-white/side.jpg', N'Adidas Adizero Ubersonic 4 - Side view', 2, 0),
(N'adidas/adizero-ubersonic-4-crystal-white/performance.jpg', N'Adidas Adizero Ubersonic 4 - Performance view', 3, 0),
(N'adidas/adizero-ubersonic-4-crystal-white/sole.jpg', N'Adidas Adizero Ubersonic 4 - Sole view', 4, 0),
(N'adidas/solematch-control-2-semi-flash-aqua/main.jpg', N'Adidas Solematch Control 2 Semi Flash Aqua', 1, 1),
(N'adidas/solematch-control-2-semi-flash-aqua/side.jpg', N'Adidas Solematch Control 2 - Side view', 2, 0),
(N'adidas/solematch-control-2-semi-flash-aqua/control.jpg', N'Adidas Solematch Control 2 - Control detail', 3, 0),
(N'adidas/solematch-control-2-semi-flash-aqua/sole.jpg', N'Adidas Solematch Control 2 - Sole view', 4, 0),
-- ?nh Converse (14 s?n ph?m x 4 ?nh = 56 ?nh)
(N'converse/chuck-taylor-all-star-low-flame/main.jpg', N'Converse Chuck Taylor All Star Low Flame', 1, 1),
(N'converse/chuck-taylor-all-star-low-flame/side.jpg', N'Converse Chuck Taylor Flame - Side view', 2, 0),
(N'converse/chuck-taylor-all-star-low-flame/pattern.jpg', N'Converse Chuck Taylor Flame - Pattern detail', 3, 0),
(N'converse/chuck-taylor-all-star-low-flame/style.jpg', N'Converse Chuck Taylor Flame - Style shot', 4, 0),
(N'converse/chuck-taylor-all-star-lift-ox-white-black/main.jpg', N'Converse Chuck Taylor All Star Lift Ox White Black', 1, 1),
(N'converse/chuck-taylor-all-star-lift-ox-white-black/side.jpg', N'Converse Chuck Taylor Lift Ox - Side view', 2, 0),
(N'converse/chuck-taylor-all-star-lift-ox-white-black/platform.jpg', N'Converse Chuck Taylor Lift Ox - Platform detail', 3, 0),
(N'converse/chuck-taylor-all-star-lift-ox-white-black/lifestyle.jpg', N'Converse Chuck Taylor Lift Ox - Lifestyle', 4, 0),
(N'converse/chuck-taylor-all-star-cruise-ox-black-white/main.jpg', N'Converse Chuck Taylor All Star Cruise OX Black White', 1, 1),
(N'converse/chuck-taylor-all-star-cruise-ox-black-white/side.jpg', N'Converse Chuck Taylor Cruise OX - Side view', 2, 0),
(N'converse/chuck-taylor-all-star-cruise-ox-black-white/sport.jpg', N'Converse Chuck Taylor Cruise OX - Sport detail', 3, 0),
(N'converse/chuck-taylor-all-star-cruise-ox-black-white/sole.jpg', N'Converse Chuck Taylor Cruise OX - Sole view', 4, 0),
(N'converse/chuck-taylor-all-star-eva-lift-platform-y2k-heart-high-top-black/main.jpg', N'Converse Chuck Taylor EVA Lift Platform Y2K Heart Black', 1, 1),
(N'converse/chuck-taylor-all-star-eva-lift-platform-y2k-heart-high-top-black/side.jpg', N'Converse Chuck Taylor EVA Lift - Side view', 2, 0),
(N'converse/chuck-taylor-all-star-eva-lift-platform-y2k-heart-high-top-black/heart.jpg', N'Converse Chuck Taylor EVA Lift - Heart detail', 3, 0),
(N'converse/chuck-taylor-all-star-eva-lift-platform-y2k-heart-high-top-black/y2k.jpg', N'Converse Chuck Taylor EVA Lift - Y2K style', 4, 0),
(N'converse/run-star-hike-low-black-gum/main.jpg', N'Converse Run Star Hike Low Black Gum', 1, 1),
(N'converse/run-star-hike-low-black-gum/side.jpg', N'Converse Run Star Hike - Side view', 2, 0),
(N'converse/run-star-hike-low-black-gum/chunky.jpg', N'Converse Run Star Hike - Chunky sole', 3, 0),
(N'converse/run-star-hike-low-black-gum/street.jpg', N'Converse Run Star Hike - Street style', 4, 0),
(N'converse/run-star-motion-low-white/main.jpg', N'Converse Run Star Motion Low White', 1, 1),
(N'converse/run-star-motion-low-white/side.jpg', N'Converse Run Star Motion - Side view', 2, 0),
(N'converse/run-star-motion-low-white/clean.jpg', N'Converse Run Star Motion - Clean look', 3, 0),
(N'converse/run-star-motion-low-white/modern.jpg', N'Converse Run Star Motion - Modern design', 4, 0),
(N'converse/aeon-active-cx-ox-egret/main.jpg', N'Converse Aeon Active CX OX Egret', 1, 1),
(N'converse/aeon-active-cx-ox-egret/side.jpg', N'Converse Aeon Active CX - Side view', 2, 0),
(N'converse/aeon-active-cx-ox-egret/cx.jpg', N'Converse Aeon Active CX - CX technology', 3, 0),
(N'converse/aeon-active-cx-ox-egret/sole.jpg', N'Converse Aeon Active CX - Sole view', 4, 0),
(N'converse/aeon-active-cx-himalayan-salt/main.jpg', N'Converse Aeon Active CX Himalayan Salt', 1, 1),
(N'converse/aeon-active-cx-himalayan-salt/side.jpg', N'Converse Aeon Active CX Salt - Side view', 2, 0),
(N'converse/aeon-active-cx-himalayan-salt/color.jpg', N'Converse Aeon Active CX Salt - Color detail', 3, 0),
(N'converse/aeon-active-cx-himalayan-salt/sole.jpg', N'Converse Aeon Active CX Salt - Sole view', 4, 0),
(N'converse/comme-des-garcons-play-x-converse-chuck-taylor-all-star-70-hi-black/main.jpg', N'CDG x Converse Chuck 70 Hi Black', 1, 1),
(N'converse/comme-des-garcons-play-x-converse-chuck-taylor-all-star-70-hi-black/side.jpg', N'CDG x Converse Chuck 70 Hi - Side view', 2, 0),
(N'converse/comme-des-garcons-play-x-converse-chuck-taylor-all-star-70-hi-black/heart.jpg', N'CDG x Converse Chuck 70 Hi - Heart logo', 3, 0),
(N'converse/comme-des-garcons-play-x-converse-chuck-taylor-all-star-70-hi-black/collaboration.jpg', N'CDG x Converse Chuck 70 Hi - Collaboration detail', 4, 0),
(N'converse/comme-des-garcons-play-x-chuck-70-low-black-white/main.jpg', N'CDG x Converse Chuck 70 Low Black White', 1, 1),
(N'converse/comme-des-garcons-play-x-chuck-70-low-black-white/side.jpg', N'CDG x Converse Chuck 70 Low - Side view', 2, 0),
(N'converse/comme-des-garcons-play-x-chuck-70-low-black-white/heart.jpg', N'CDG x Converse Chuck 70 Low - Heart detail', 3, 0),
(N'converse/comme-des-garcons-play-x-chuck-70-low-black-white/sole.jpg', N'CDG x Converse Chuck 70 Low - Sole view', 4, 0),
(N'converse/comme-des-garcons-x-chuck-taylor-all-star-hi-milk/main.jpg', N'CDG x Converse Chuck Taylor All Star Hi Milk', 1, 1),
(N'converse/comme-des-garcons-x-chuck-taylor-all-star-hi-milk/side.jpg', N'CDG x Converse Chuck Taylor Hi Milk - Side view', 2, 0),
(N'converse/comme-des-garcons-x-chuck-taylor-all-star-hi-milk/clean.jpg', N'CDG x Converse Chuck Taylor Hi Milk - Clean aesthetic', 3, 0),
(N'converse/comme-des-garcons-x-chuck-taylor-all-star-hi-milk/sole.jpg', N'CDG x Converse Chuck Taylor Hi Milk - Sole view', 4, 0),
(N'converse/kim-jones-x-converse-chuck-70-all-star-black/main.jpg', N'Kim Jones x Converse Chuck 70 All Star Black', 1, 1),
(N'converse/kim-jones-x-converse-chuck-70-all-star-black/side.jpg', N'Kim Jones x Converse Chuck 70 - Side view', 2, 0),
(N'converse/kim-jones-x-converse-chuck-70-all-star-black/designer.jpg', N'Kim Jones x Converse Chuck 70 - Designer detail', 3, 0),
(N'converse/kim-jones-x-converse-chuck-70-all-star-black/sole.jpg', N'Kim Jones x Converse Chuck 70 - Sole view', 4, 0),
(N'converse/x-rick-owens-drkshdw-weapon-beige-black/main.jpg', N'Converse x Rick Owens DRKSHDW Weapon Beige Black', 1, 1),
(N'converse/x-rick-owens-drkshdw-weapon-beige-black/side.jpg', N'Converse x Rick Owens DRKSHDW - Side view', 2, 0),
(N'converse/x-rick-owens-drkshdw-weapon-beige-black/avant-garde.jpg', N'Converse x Rick Owens DRKSHDW - Avant-garde design', 3, 0),
(N'converse/x-rick-owens-drkshdw-weapon-beige-black/fashion.jpg', N'Converse x Rick Owens DRKSHDW - Fashion forward', 4, 0),
(N'converse/back-to-the-future-converse-all-star-us-mt-hi-black/main.jpg', N'Back To The Future x Converse All Star US MT Hi Black', 1, 1),
(N'converse/back-to-the-future-converse-all-star-us-mt-hi-black/side.jpg', N'BTTF x Converse All Star - Side view', 2, 0),
(N'converse/back-to-the-future-converse-all-star-us-mt-hi-black/movie.jpg', N'BTTF x Converse All Star - Movie reference', 3, 0),
(N'converse/back-to-the-future-converse-all-star-us-mt-hi-black/scifi.jpg', N'BTTF x Converse All Star - Sci-fi style', 4, 0),
-- ?nh MLB (13 s?n ph?m x 4 ?nh = 52 ?nh)
(N'mlb/bigball-chunky-diamond-monogram-new-york-yankees-black/main.jpg', N'MLB BigBall Chunky Diamond Monogram NY Yankees Black', 1, 1),
(N'mlb/bigball-chunky-diamond-monogram-new-york-yankees-black/side.jpg', N'MLB BigBall Chunky NY Yankees - Side view', 2, 0),
(N'mlb/bigball-chunky-diamond-monogram-new-york-yankees-black/monogram.jpg', N'MLB BigBall Chunky NY Yankees - Monogram detail', 3, 0),
(N'mlb/bigball-chunky-diamond-monogram-new-york-yankees-black/team.jpg', N'MLB BigBall Chunky NY Yankees - Team spirit', 4, 0),
(N'mlb/bigball-chunky-diamond-monogram-boston-red-sox-beige/main.jpg', N'MLB BigBall Chunky Diamond Monogram Boston Red Sox Beige', 1, 1),
(N'mlb/bigball-chunky-diamond-monogram-boston-red-sox-beige/side.jpg', N'MLB BigBall Chunky Red Sox - Side view', 2, 0),
(N'mlb/bigball-chunky-diamond-monogram-boston-red-sox-beige/beige.jpg', N'MLB BigBall Chunky Red Sox - Beige colorway', 3, 0),
(N'mlb/bigball-chunky-diamond-monogram-boston-red-sox-beige/sole.jpg', N'MLB BigBall Chunky Red Sox - Sole view', 4, 0),
(N'mlb/bigball-chunky-a-new-york-yankees/main.jpg', N'MLB Bigball Chunky A New York Yankees', 1, 1),
(N'mlb/bigball-chunky-a-new-york-yankees/side.jpg', N'MLB Bigball Chunky A Yankees - Side view', 2, 0),
(N'mlb/bigball-chunky-a-new-york-yankees/classic.jpg', N'MLB Bigball Chunky A Yankees - Classic design', 3, 0),
(N'mlb/bigball-chunky-a-new-york-yankees/sole.jpg', N'MLB Bigball Chunky A Yankees - Sole view', 4, 0),
(N'mlb/bigball-chunky-p-boston-red-sox/main.jpg', N'MLB BigBall Chunky P Boston Red Sox', 1, 1),
(N'mlb/bigball-chunky-p-boston-red-sox/side.jpg', N'MLB BigBall Chunky P Red Sox - Side view', 2, 0),
(N'mlb/bigball-chunky-p-boston-red-sox/performance.jpg', N'MLB BigBall Chunky P Red Sox - Performance style', 3, 0),
(N'mlb/bigball-chunky-p-boston-red-sox/sole.jpg', N'MLB BigBall Chunky P Red Sox - Sole view', 4, 0),
(N'mlb/chunky-liner-new-york-yankees-grey/main.jpg', N'MLB Chunky Liner New York Yankees Grey', 1, 1),
(N'mlb/chunky-liner-new-york-yankees-grey/side.jpg', N'MLB Chunky Liner Yankees Grey - Side view', 2, 0),
(N'mlb/chunky-liner-new-york-yankees-grey/minimal.jpg', N'MLB Chunky Liner Yankees Grey - Minimal design', 3, 0),
(N'mlb/chunky-liner-new-york-yankees-grey/sole.jpg', N'MLB Chunky Liner Yankees Grey - Sole view', 4, 0),
(N'mlb/chunky-liner-low-boston-red-sox-beige/main.jpg', N'MLB Chunky Liner Low Boston Red Sox Beige', 1, 1),
(N'mlb/chunky-liner-low-boston-red-sox-beige/side.jpg', N'MLB Chunky Liner Low Red Sox - Side view', 2, 0),
(N'mlb/chunky-liner-low-boston-red-sox-beige/low.jpg', N'MLB Chunky Liner Low Red Sox - Low profile', 3, 0),
(N'mlb/chunky-liner-low-boston-red-sox-beige/sole.jpg', N'MLB Chunky Liner Low Red Sox - Sole view', 4, 0),
(N'mlb/chunky-liner-mid-denim-boston-red-sox-d-blue/main.jpg', N'MLB Chunky Liner Mid Denim Boston Red Sox D.Blue', 1, 1),
(N'mlb/chunky-liner-mid-denim-boston-red-sox-d-blue/side.jpg', N'MLB Chunky Liner Mid Denim Red Sox - Side view', 2, 0),
(N'mlb/chunky-liner-mid-denim-boston-red-sox-d-blue/denim.jpg', N'MLB Chunky Liner Mid Denim Red Sox - Denim texture', 3, 0),
(N'mlb/chunky-liner-mid-denim-boston-red-sox-d-blue/sole.jpg', N'MLB Chunky Liner Mid Denim Red Sox - Sole view', 4, 0),
(N'mlb/chunky-liner-mid-denim-new-york-yankees-d-navy/main.jpg', N'MLB Chunky Liner Mid Denim New York Yankees D.Navy', 1, 1),
(N'mlb/chunky-liner-mid-denim-new-york-yankees-d-navy/side.jpg', N'MLB Chunky Liner Mid Denim Yankees - Side view', 2, 0),
(N'mlb/chunky-liner-mid-denim-new-york-yankees-d-navy/navy.jpg', N'MLB Chunky Liner Mid Denim Yankees - Navy denim', 3, 0),
(N'mlb/chunky-liner-mid-denim-new-york-yankees-d-navy/sole.jpg', N'MLB Chunky Liner Mid Denim Yankees - Sole view', 4, 0),
(N'mlb/chunky-liner-sl-saffiano-boston-red-sox/main.jpg', N'MLB Chunky Liner SL Saffiano Boston Red Sox', 1, 1),
(N'mlb/chunky-liner-sl-saffiano-boston-red-sox/side.jpg', N'MLB Chunky Liner SL Saffiano - Side view', 2, 0),
(N'mlb/chunky-liner-sl-saffiano-boston-red-sox/leather.jpg', N'MLB Chunky Liner SL Saffiano - Leather texture', 3, 0),
(N'mlb/chunky-liner-sl-saffiano-boston-red-sox/sole.jpg', N'MLB Chunky Liner SL Saffiano - Sole view', 4, 0),
(N'mlb/liner-basic-new-york-yankees-black/main.jpg', N'MLB Liner Basic New York Yankees Black', 1, 1),
(N'mlb/liner-basic-new-york-yankees-black/side.jpg', N'MLB Liner Basic Yankees Black - Side view', 2, 0),
(N'mlb/liner-basic-new-york-yankees-black/simple.jpg', N'MLB Liner Basic Yankees Black - Simple design', 3, 0),
(N'mlb/liner-basic-new-york-yankees-black/sole.jpg', N'MLB Liner Basic Yankees Black - Sole view', 4, 0),
(N'mlb/liner-basic-new-york-yankees-green/main.jpg', N'MLB Liner Basic New York Yankees Green', 1, 1),
(N'mlb/liner-basic-new-york-yankees-green/side.jpg', N'MLB Liner Basic Yankees Green - Side view', 2, 0),
(N'mlb/liner-basic-new-york-yankees-green/unique.jpg', N'MLB Liner Basic Yankees Green - Unique colorway', 3, 0),
(N'mlb/liner-basic-new-york-yankees-green/sole.jpg', N'MLB Liner Basic Yankees Green - Sole view', 4, 0),
(N'mlb/playball-mule-dia-monogram-new-york-yankees-beige/main.jpg', N'MLB Playball Mule Dia Monogram New York Yankees Beige', 1, 1),
(N'mlb/playball-mule-dia-monogram-new-york-yankees-beige/side.jpg', N'MLB Playball Mule Yankees - Side view', 2, 0),
(N'mlb/playball-mule-dia-monogram-new-york-yankees-beige/slip-on.jpg', N'MLB Playball Mule Yankees - Slip-on style', 3, 0),
(N'mlb/playball-mule-dia-monogram-new-york-yankees-beige/sole.jpg', N'MLB Playball Mule Yankees - Sole view', 4, 0),
(N'mlb/playball-mule-mono-ny-new-york-yankees/main.jpg', N'MLB Playball Mule Mono NY New York Yankees', 1, 1),
(N'mlb/playball-mule-mono-ny-new-york-yankees/side.jpg', N'MLB Playball Mule Mono NY - Side view', 2, 0),
(N'mlb/playball-mule-mono-ny-new-york-yankees/monogram.jpg', N'MLB Playball Mule Mono NY - Monogram design', 3, 0),
(N'mlb/playball-mule-mono-ny-new-york-yankees/sole.jpg', N'MLB Playball Mule Mono NY - Sole view', 4, 0);
GO

-- 10. S?n ph?m (40 s?n ph?m)
-- ADIDAS (13 s?n ph?m)
INSERT INTO SanPham (id_loai_san_pham, id_danh_muc, id_thuong_hieu, id_xuat_xu, id_kieu_dang, id_chat_lieu, id_hinh_anh_dai_dien, ma_san_pham, ten_san_pham, mo_ta, gia_nhap, gia_ban, trang_thai) VALUES 
(4, 3, 1, 2, 6, 5, 2, N'ADI-SAMBA-001', N'Adidas Samba OG White Black Gum', N'Giày Adidas Samba OG phiên bản classic với phối màu trắng đen gum iconic.', 1800000, 2590000, 1),
(4, 3, 1, 2, 6, 5, 6, N'ADI-SAMBA-002', N'Adidas Samba OG Wonder White Maroon', N'Giày Adidas Samba OG với phối màu trắng và đỏ maroon sang trọng.', 1800000, 2590000, 1),
(4, 2, 1, 2, 6, 5, 10, N'ADI-SAMBA-003', N'Adidas Samba OG Cloud White Wonder Quartz Wmns', N'Phiên bản dành cho nữ với tone màu nhã nhặn và feminine.', 1800000, 2590000, 1),
(4, 3, 1, 2, 4, 5, 14, N'ADI-GAZELLE-001', N'Adidas Gazelle Bold Year of the Snake', N'Giày Adidas Gazelle Bold phiên bản đặc biệt Year of the Snake.', 2200000, 3190000, 1),
(1, 3, 1, 2, 1, 7, 18, N'ADI-YEEZY-001', N'Adidas Yeezy Boost 350 V2 Steel Grey', N'Giày Adidas Yeezy Boost 350 V2 với colorway Steel Grey huyền thoại.', 4500000, 6490000, 1),
(1, 3, 1, 2, 1, 10, 22, N'ADI-ADIFOAM-001', N'Adidas adiFOM Superstar Core Black', N'Giày Adidas adiFOM Superstar với công nghệ foam mới.', 2000000, 2890000, 1),
(1, 3, 1, 2, 1, 10, 26, N'ADI-ADIFOAM-002', N'Adidas adiFOM Superstar Core White', N'Phiên bản màu trắng của dòng adiFOM Superstar.', 2000000, 2890000, 1),
(1, 3, 1, 2, 1, 9, 30, N'ADI-ADIFOAM-003', N'Adidas adiFOM Supernova Triple Black', N'Giày Adidas adiFOM Supernova với colorway Triple Black.', 2200000, 3290000, 1),
(2, 1, 1, 2, 1, 7, 34, N'ADI-ULTRA-001', N'Adidas UltraBoost 21 Cloud White', N'Giày chạy bộ Adidas UltraBoost 21 với công nghệ Boost.', 3500000, 4990000, 1),
(2, 1, 1, 2, 1, 4, 38, N'ADI-RUN-001', N'Adidas Run EQ21 Black', N'Giày chạy bộ Adidas Run EQ21 màu đen.', 1200000, 1790000, 1),
(3, 1, 1, 2, 1, 3, 42, N'ADI-TENNIS-001', N'Adidas Barricade 13 Tennis Black', N'Giày tennis Adidas Barricade 13 chuyên nghiệp.', 2500000, 3590000, 1),
(3, 3, 1, 2, 1, 3, 46, N'ADI-TENNIS-002', N'Adidas Adizero Ubersonic 4 Crystal White', N'Giày tennis Adidas Adizero Ubersonic 4 lightweight.', 2800000, 3990000, 1),
(3, 3, 1, 2, 1, 3, 50, N'ADI-TENNIS-003', N'Adidas Solematch Control 2 Semi Flash Aqua', N'Giày tennis Adidas Solematch Control 2 control.', 2600000, 3790000, 1),
-- CONVERSE (14 sản phẩm)
(5, 3, 2, 3, 3, 2, 54, N'CVS-CHUCK-001', N'Converse Chuck Taylor All Star Low Flame', N'Giày Converse Chuck Taylor All Star Low với họa tiết flame độc đáo.', 1000000, 1490000, 1),
(8, 2, 2, 3, 4, 2, 58, N'CVS-LIFT-001', N'Converse Chuck Taylor All Star Lift Ox White Black', N'Giày Converse Chuck Taylor All Star Lift Ox với đế platform tăng chiều cao.', 1200000, 1790000, 1),
(5, 3, 2, 3, 3, 2, 62, N'CVS-CHUCK-002', N'Converse Chuck Taylor All Star Cruise OX Black White', N'Giày Converse Chuck Taylor All Star Cruise OX với thiết kế thể thao hiện đại.', 1100000, 1590000, 1),
(5, 2, 2, 3, 2, 2, 66, N'CVS-EVA-001', N'Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black', N'Giày Converse Chuck Taylor EVA Lift Platform với theme Y2K nostalgic.', 1400000, 2090000, 1),
(7, 3, 2, 3, 5, 3, 70, N'CVS-RUNSTAR-001', N'Converse Run Star Hike Low Black Gum', N'Giày Converse Run Star Hike Low với thiết kế chunky revolutionary.', 1500000, 2190000, 1),
(7, 3, 2, 3, 5, 3, 74, N'CVS-RUNSTAR-002', N'Converse Run Star Motion Low White', N'Phiên bản trắng của dòng Run Star Motion với thiết kế hiện đại.', 1500000, 2190000, 1),
(4, 3, 2, 3, 1, 3, 78, N'CVS-AEON-001', N'Converse Aeon Active CX OX Egret', N'Giày Converse Aeon Active CX OX với công nghệ CX innovative.', 1300000, 1890000, 1),
(4, 3, 2, 3, 1, 3, 82, N'CVS-AEON-002', N'Converse Aeon Active CX Himalayan Salt', N'Phiên bản Himalayan Salt của dòng Aeon Active CX.', 1300000, 1890000, 1),
(10, 3, 2, 3, 2, 2, 86, N'CVS-CDG-001', N'CDG x Converse Chuck 70 Hi Black', N'Sản phẩm collaboration iconic giữa Comme des Garçons và Converse.', 2500000, 3690000, 1),
(10, 3, 2, 3, 3, 2, 90, N'CVS-CDG-002', N'CDG x Converse Chuck 70 Low Black White', N'Phiên bản thấp cổ của collaboration CDG x Converse.', 2500000, 3690000, 1),
(10, 3, 2, 3, 2, 2, 94, N'CVS-CDG-003', N'CDG x Converse Chuck Taylor All Star Hi Milk', N'CDG x Converse Chuck Taylor All Star Hi với colorway Milk độc đáo.', 2500000, 3690000, 1),
(10, 3, 2, 3, 2, 2, 98, N'CVS-KIM-001', N'Kim Jones x Converse Chuck 70 All Star Black', N'Collaboration giữa Kim Jones và Converse mang high fashion aesthetic.', 2800000, 3990000, 1),
(10, 3, 2, 3, 2, 3, 102, N'CVS-RICK-001', N'Converse x Rick Owens DRKSHDW Weapon Beige Black', N'Converse x Rick Owens DRKSHDW Weapon với avant-garde design philosophy.', 3200000, 4590000, 1),
(10, 3, 2, 3, 2, 2, 106, N'CVS-BTTF-001', N'Back To The Future x Converse All Star US MT Hi Black', N'Collaboration Back To The Future x Converse với movie-inspired design.', 2200000, 3290000, 1),
-- MLB (13 sản phẩm)
(7, 3, 3, 4, 5, 3, 110, N'MLB-BIGBALL-001', N'MLB BigBall Chunky Diamond Monogram NY Yankees Black', N'Giày MLB BigBall Chunky với họa tiết Diamond Monogram luxury.', 1800000, 2690000, 1),
(7, 3, 3, 4, 5, 3, 114, N'MLB-BIGBALL-002', N'MLB BigBall Chunky Diamond Monogram Boston Red Sox Beige', N'Phiên bản Boston Red Sox với màu beige sophisticated.', 1800000, 2690000, 1),
(7, 3, 3, 4, 5, 3, 118, N'MLB-BIGBALL-003', N'MLB Bigball Chunky A New York Yankees', N'MLB Bigball Chunky A phiên bản New York Yankees với design classic.', 1700000, 2490000, 1),
(7, 3, 3, 4, 5, 3, 122, N'MLB-BIGBALL-004', N'MLB BigBall Chunky P Boston Red Sox', N'MLB BigBall Chunky P Boston Red Sox với performance-inspired design.', 1700000, 2490000, 1),
(7, 3, 3, 4, 5, 3, 126, N'MLB-CHUNKY-001', N'MLB Chunky Liner New York Yankees Grey', N'Giày MLB Chunky Liner của đội New York Yankees với màu xám neutral.', 1600000, 2390000, 1),
(7, 3, 3, 4, 5, 3, 130, N'MLB-CHUNKY-002', N'MLB Chunky Liner Low Boston Red Sox Beige', N'Phiên bản thấp cổ của dòng Chunky Liner với Red Sox branding.', 1600000, 2390000, 1),
(7, 3, 3, 4, 5, 11, 134, N'MLB-CHUNKY-003', N'MLB Chunky Liner Mid Denim Boston Red Sox D.Blue', N'MLB Chunky Liner Mid với chất liệu denim unique.', 1700000, 2590000, 1),
(7, 3, 3, 4, 5, 11, 138, N'MLB-CHUNKY-004', N'MLB Chunky Liner Mid Denim New York Yankees D.Navy', N'Phiên bản denim New York Yankees với D.Navy sophisticated.', 1700000, 2590000, 1),
(7, 3, 3, 4, 5, 12, 142, N'MLB-CHUNKY-005', N'MLB Chunky Liner SL Saffiano Boston Red Sox', N'MLB Chunky Liner SL với chất liệu Saffiano leather cao cấp.', 1900000, 2890000, 1),
(4, 1, 3, 4, 3, 2, 146, N'MLB-LINER-001', N'MLB Liner Basic New York Yankees Black', N'Giày MLB Liner Basic của đội New York Yankees.', 1200000, 1790000, 1),
(4, 1, 3, 4, 3, 2, 150, N'MLB-LINER-002', N'MLB Liner Basic New York Yankees Green', N'Phiên bản màu xanh unique của dòng Liner Basic.', 1200000, 1790000, 1),
(9, 3, 3, 4, 8, 5, 154, N'MLB-MULE-001', N'MLB Playball Mule Dia Monogram New York Yankees Beige', N'Giày MLB Playball Mule với Diamond Monogram luxury pattern.', 1500000, 2290000, 1),
(9, 3, 3, 4, 8, 3, 158, N'MLB-MULE-002', N'MLB Playball Mule Mono NY New York Yankees', N'MLB Playball Mule với Monogram NY design clean và minimal.', 1400000, 2090000, 1);
GO-- 11. Chi ti?t s?n ph?m v?i d?y d? size v� m�u cho 40 s?n ph?m
INSERT INTO SanPhamChiTiet (id_san_pham, id_kich_co, id_mau_sac, so_luong, trang_thai) VALUES 
-- Adidas Samba OG White Black Gum (ID: 1) - 8 bi?n th?
(1, 2, 2, 15, 1), (1, 3, 2, 20, 1), (1, 4, 2, 25, 1), (1, 5, 2, 30, 1), (1, 6, 2, 28, 1), (1, 7, 2, 22, 1), (1, 8, 2, 18, 1), (1, 9, 2, 12, 1),
-- Adidas Samba OG Wonder White Maroon (ID: 2) - 7 bi?n th?
(2, 2, 2, 12, 1), (2, 3, 2, 18, 1), (2, 4, 2, 22, 1), (2, 5, 2, 25, 1), (2, 6, 2, 20, 1), (2, 7, 2, 15, 1), (2, 8, 2, 10, 1),
-- Adidas Samba OG Cloud White Wonder Quartz Wmns (ID: 3) - 6 bi?n th?
(3, 1, 2, 8, 1), (3, 2, 2, 15, 1), (3, 3, 2, 20, 1), (3, 4, 2, 18, 1), (3, 5, 2, 12, 1), (3, 6, 2, 8, 1),
-- Adidas Gazelle Bold Year of the Snake (ID: 4) - 6 bi?n th?
(4, 3, 3, 10, 1), (4, 4, 3, 15, 1), (4, 5, 3, 18, 1), (4, 6, 3, 20, 1), (4, 7, 3, 15, 1), (4, 8, 3, 12, 1),
-- Adidas Yeezy Boost 350 V2 Steel Grey (ID: 5) - 6 bi?n th?
(5, 3, 3, 5, 1), (5, 4, 3, 8, 1), (5, 5, 3, 10, 1), (5, 6, 3, 12, 1), (5, 7, 3, 8, 1), (5, 8, 3, 5, 1),
-- Adidas adiFOM Superstar Core Black (ID: 6) - 6 bi?n th?
(6, 3, 1, 12, 1), (6, 4, 1, 18, 1), (6, 5, 1, 22, 1), (6, 6, 1, 25, 1), (6, 7, 1, 20, 1), (6, 8, 1, 15, 1),
-- Adidas adiFOM Superstar Core White (ID: 7) - 6 bi?n th?
(7, 3, 2, 15, 1), (7, 4, 2, 20, 1), (7, 5, 2, 25, 1), (7, 6, 2, 28, 1), (7, 7, 2, 22, 1), (7, 8, 2, 18, 1),
-- Adidas adiFOM Supernova Triple Black (ID: 8) - 6 bi?n th?
(8, 4, 1, 20, 1), (8, 5, 1, 25, 1), (8, 6, 1, 30, 1), (8, 7, 1, 28, 1), (8, 8, 1, 22, 1), (8, 9, 1, 18, 1),
-- Adidas UltraBoost 21 Cloud White (ID: 9) - 6 bi?n th?
(9, 4, 2, 8, 1), (9, 5, 2, 12, 1), (9, 6, 2, 15, 1), (9, 7, 2, 18, 1), (9, 8, 2, 15, 1), (9, 9, 2, 10, 1),
-- Adidas Run EQ21 Black (ID: 10) - 6 bi?n th?
(10, 4, 1, 20, 1), (10, 5, 1, 25, 1), (10, 6, 1, 30, 1), (10, 7, 1, 28, 1), (10, 8, 1, 22, 1), (10, 9, 1, 18, 1),
-- Adidas Barricade 13 Tennis Black (ID: 11) - 6 bi?n th?
(11, 4, 1, 8, 1), (11, 5, 1, 12, 1), (11, 6, 1, 15, 1), (11, 7, 1, 18, 1), (11, 8, 1, 15, 1), (11, 9, 1, 10, 1),
-- Adidas Adizero Ubersonic 4 Crystal White (ID: 12) - 6 bi?n th?
(12, 4, 2, 10, 1), (12, 5, 2, 15, 1), (12, 6, 2, 18, 1), (12, 7, 2, 20, 1), (12, 8, 2, 15, 1), (12, 9, 2, 12, 1),
-- Adidas Solematch Control 2 Semi Flash Aqua (ID: 13) - 6 bi?n th?
(13, 4, 9, 12, 1), (13, 5, 9, 18, 1), (13, 6, 9, 20, 1), (13, 7, 9, 18, 1), (13, 8, 9, 15, 1), (13, 9, 9, 10, 1),
-- Converse Chuck Taylor All Star Low Flame (ID: 14) - 6 bi?n th?
(14, 3, 5, 15, 1), (14, 4, 5, 20, 1), (14, 5, 5, 25, 1), (14, 6, 5, 22, 1), (14, 7, 5, 18, 1), (14, 8, 5, 12, 1),
-- Converse Chuck Taylor All Star Lift Ox White Black (ID: 15) - 6 bi?n th?
(15, 2, 2, 10, 1), (15, 3, 2, 15, 1), (15, 4, 2, 20, 1), (15, 5, 2, 18, 1), (15, 6, 2, 12, 1), (15, 7, 2, 8, 1),
-- Converse Chuck Taylor All Star Cruise OX Black White (ID: 16) - 5 bi?n th?
(16, 4, 15, 20, 1), (16, 5, 15, 25, 1), (16, 6, 15, 22, 1), (16, 7, 15, 18, 1), (16, 8, 15, 15, 1),
-- Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black (ID: 17) - 6 bi?n th?
(17, 3, 1, 12, 1), (17, 4, 1, 18, 1), (17, 5, 1, 22, 1), (17, 6, 1, 25, 1), (17, 7, 1, 20, 1), (17, 8, 1, 15, 1),
-- Converse Run Star Hike Low Black Gum (ID: 18) - 6 bi?n th?
(18, 3, 1, 12, 1), (18, 4, 1, 18, 1), (18, 5, 1, 22, 1), (18, 6, 1, 25, 1), (18, 7, 1, 20, 1), (18, 8, 1, 15, 1),
-- Converse Run Star Motion Low White (ID: 19) - 6 bi?n th?
(19, 3, 2, 18, 1), (19, 4, 2, 25, 1), (19, 5, 2, 28, 1), (19, 6, 2, 22, 1), (19, 7, 2, 18, 1), (19, 8, 2, 12, 1),
-- Converse Aeon Active CX OX Egret (ID: 20) - 5 bi?n th?
(20, 3, 13, 15, 1), (20, 4, 13, 20, 1), (20, 5, 13, 18, 1), (20, 6, 13, 15, 1), (20, 7, 13, 12, 1),
-- Converse Aeon Active CX Himalayan Salt (ID: 21) - 5 bi?n th?
(21, 3, 13, 12, 1), (21, 4, 13, 18, 1), (21, 5, 13, 20, 1), (21, 6, 13, 15, 1), (21, 7, 13, 10, 1),
-- CDG x Converse Chuck 70 Hi Black (ID: 22) - 6 bi?n th?
(22, 3, 1, 8, 1), (22, 4, 1, 12, 1), (22, 5, 1, 15, 1), (22, 6, 1, 18, 1), (22, 7, 1, 15, 1), (22, 8, 1, 10, 1),
-- CDG x Converse Chuck 70 Low Black White (ID: 23) - 6 bi?n th?
(23, 3, 15, 10, 1), (23, 4, 15, 15, 1), (23, 5, 15, 18, 1), (23, 6, 15, 20, 1), (23, 7, 15, 15, 1), (23, 8, 15, 12, 1),
-- CDG x Converse Chuck Taylor All Star Hi Milk (ID: 24) - 6 bi?n th?
(24, 3, 2, 8, 1), (24, 4, 2, 12, 1), (24, 5, 2, 15, 1), (24, 6, 2, 18, 1), (24, 7, 2, 15, 1), (24, 8, 2, 10, 1),
-- Kim Jones x Converse Chuck 70 All Star Black (ID: 25) - 6 bi?n th?
(25, 3, 1, 6, 1), (25, 4, 1, 10, 1), (25, 5, 1, 12, 1), (25, 6, 1, 15, 1), (25, 7, 1, 12, 1), (25, 8, 1, 8, 1),
-- Converse x Rick Owens DRKSHDW Weapon Beige Black (ID: 26) - 6 bi?n th?
(26, 4, 13, 5, 1), (26, 5, 13, 8, 1), (26, 6, 13, 10, 1), (26, 7, 13, 12, 1), (26, 8, 13, 10, 1), (26, 9, 13, 6, 1),
-- Back To The Future x Converse All Star US MT Hi Black (ID: 27) - 6 bi?n th?
(27, 4, 1, 8, 1), (27, 5, 1, 12, 1), (27, 6, 1, 15, 1), (27, 7, 1, 18, 1), (27, 8, 1, 15, 1), (27, 9, 1, 10, 1),
-- MLB BigBall Chunky Diamond Monogram NY Yankees Black (ID: 28) - 6 bi?n th?
(28, 4, 1, 10, 1), (28, 5, 1, 15, 1), (28, 6, 1, 18, 1), (28, 7, 1, 20, 1), (28, 8, 1, 15, 1), (28, 9, 1, 12, 1),
-- MLB BigBall Chunky Diamond Monogram Boston Red Sox Beige (ID: 29) - 5 bi?n th?
(29, 4, 13, 12, 1), (29, 5, 13, 18, 1), (29, 6, 13, 20, 1), (29, 7, 13, 18, 1), (29, 8, 13, 15, 1),
-- MLB Bigball Chunky A New York Yankees (ID: 30) - 5 bi?n th?
(30, 4, 3, 15, 1), (30, 5, 3, 20, 1), (30, 6, 3, 22, 1), (30, 7, 3, 18, 1), (30, 8, 3, 15, 1),
-- MLB BigBall Chunky P Boston Red Sox (ID: 31) - 5 bi?n th?
(31, 4, 13, 18, 1), (31, 5, 13, 22, 1), (31, 6, 13, 25, 1), (31, 7, 13, 20, 1), (31, 8, 13, 15, 1),
-- MLB Chunky Liner New York Yankees Grey (ID: 32) - 5 bi?n th?
(32, 4, 3, 15, 1), (32, 5, 3, 20, 1), (32, 6, 3, 22, 1), (32, 7, 3, 18, 1), (32, 8, 3, 15, 1),
-- MLB Chunky Liner Low Boston Red Sox Beige (ID: 33) - 5 bi?n th?
(33, 4, 13, 18, 1), (33, 5, 13, 22, 1), (33, 6, 13, 25, 1), (33, 7, 13, 20, 1), (33, 8, 13, 15, 1),
-- MLB Chunky Liner Mid Denim Boston Red Sox D.Blue (ID: 34) - 5 bi?n th?
(34, 4, 4, 12, 1), (34, 5, 4, 18, 1), (34, 6, 4, 20, 1), (34, 7, 4, 18, 1), (34, 8, 4, 15, 1),
-- MLB Chunky Liner Mid Denim New York Yankees D.Navy (ID: 35) - 5 bi?n th?
(35, 4, 4, 15, 1), (35, 5, 4, 20, 1), (35, 6, 4, 22, 1), (35, 7, 4, 18, 1), (35, 8, 4, 15, 1),
-- MLB Chunky Liner SL Saffiano Boston Red Sox (ID: 36) - 5 bi?n th?
(36, 4, 13, 10, 1), (36, 5, 13, 15, 1), (36, 6, 13, 18, 1), (36, 7, 13, 15, 1), (36, 8, 13, 12, 1),
-- MLB Liner Basic New York Yankees Black (ID: 37) - 6 bi?n th?
(37, 4, 1, 25, 1), (37, 5, 1, 30, 1), (37, 6, 1, 28, 1), (37, 7, 1, 25, 1), (37, 8, 1, 20, 1), (37, 9, 1, 15, 1),
-- MLB Liner Basic New York Yankees Green (ID: 38) - 5 bi?n th?
(38, 4, 10, 20, 1), (38, 5, 10, 25, 1), (38, 6, 10, 22, 1), (38, 7, 10, 18, 1), (38, 8, 10, 15, 1),
-- MLB Playball Mule Dia Monogram New York Yankees Beige (ID: 39) - 5 bi?n th?
(39, 4, 13, 12, 1), (39, 5, 13, 18, 1), (39, 6, 13, 20, 1), (39, 7, 13, 15, 1), (39, 8, 13, 10, 1),
-- MLB Playball Mule Mono NY New York Yankees (ID: 40) - 5 bi?n th?
(40, 4, 3, 15, 1), (40, 5, 3, 20, 1), (40, 6, 3, 18, 1), (40, 7, 3, 15, 1), (40, 8, 3, 12, 1);
GO

-- 11.1. Mapping ?nh cho t?ng bi?n th? s?n ph?m
INSERT INTO HinhAnh_SanPhamChiTiet (id_hinh_anh, id_san_pham_chi_tiet) VALUES 
-- Adidas Samba OG White Black Gum (ID: 1) - 8 bi?n th? (ID: 1-8)
(2, 1), (3, 1), (4, 1), (5, 1), (2, 2), (3, 2), (4, 2), (5, 2), (2, 3), (3, 3), (4, 3), (5, 3), (2, 4), (3, 4), (4, 4), (5, 4),
(2, 5), (3, 5), (4, 5), (5, 5), (2, 6), (3, 6), (4, 6), (5, 6), (2, 7), (3, 7), (4, 7), (5, 7), (2, 8), (3, 8), (4, 8), (5, 8),
-- Adidas Samba OG Wonder White Maroon (ID: 2) - 7 bi?n th? (ID: 9-15)
(6, 9), (7, 9), (8, 9), (9, 9), (6, 10), (7, 10), (8, 10), (9, 10), (6, 11), (7, 11), (8, 11), (9, 11), (6, 12), (7, 12), (8, 12), (9, 12),
(6, 13), (7, 13), (8, 13), (9, 13), (6, 14), (7, 14), (8, 14), (9, 14), (6, 15), (7, 15), (8, 15), (9, 15),
-- Adidas Samba OG Cloud White Wonder Quartz Wmns (ID: 3) - 6 bi?n th? (ID: 16-21)
(10, 16), (11, 16), (12, 16), (13, 16), (10, 17), (11, 17), (12, 17), (13, 17), (10, 18), (11, 18), (12, 18), (13, 18), 
(10, 19), (11, 19), (12, 19), (13, 19), (10, 20), (11, 20), (12, 20), (13, 20), (10, 21), (11, 21), (12, 21), (13, 21),
-- Adidas Gazelle Bold Year of the Snake (ID: 4) - 6 bi?n th? (ID: 22-27)
(14, 22), (15, 22), (16, 22), (17, 22), (14, 23), (15, 23), (16, 23), (17, 23), (14, 24), (15, 24), (16, 24), (17, 24), 
(14, 25), (15, 25), (16, 25), (17, 25), (14, 26), (15, 26), (16, 26), (17, 26), (14, 27), (15, 27), (16, 27), (17, 27),
-- Adidas Yeezy Boost 350 V2 Steel Grey (ID: 5) - 6 bi?n th? (ID: 28-33)
(18, 28), (19, 28), (20, 28), (21, 28), (18, 29), (19, 29), (20, 29), (21, 29), (18, 30), (19, 30), (20, 30), (21, 30), 
(18, 31), (19, 31), (20, 31), (21, 31), (18, 32), (19, 32), (20, 32), (21, 32), (18, 33), (19, 33), (20, 33), (21, 33),
-- Adidas adiFOM Superstar Core Black (ID: 6) - 6 bi?n th? (ID: 34-39)
(22, 34), (23, 34), (24, 34), (25, 34), (22, 35), (23, 35), (24, 35), (25, 35), (22, 36), (23, 36), (24, 36), (25, 36), 
(22, 37), (23, 37), (24, 37), (25, 37), (22, 38), (23, 38), (24, 38), (25, 38), (22, 39), (23, 39), (24, 39), (25, 39),
-- Adidas adiFOM Superstar Core White (ID: 7) - 6 bi?n th? (ID: 40-45)
(26, 40), (27, 40), (28, 40), (29, 40), (26, 41), (27, 41), (28, 41), (29, 41), (26, 42), (27, 42), (28, 42), (29, 42), 
(26, 43), (27, 43), (28, 43), (29, 43), (26, 44), (27, 44), (28, 44), (29, 44), (26, 45), (27, 45), (28, 45), (29, 45),
-- Adidas adiFOM Supernova Triple Black (ID: 8) - 6 bi?n th? (ID: 46-51)
(30, 46), (31, 46), (32, 46), (33, 46), (30, 47), (31, 47), (32, 47), (33, 47), (30, 48), (31, 48), (32, 48), (33, 48), 
(30, 49), (31, 49), (32, 49), (33, 49), (30, 50), (31, 50), (32, 50), (33, 50), (30, 51), (31, 51), (32, 51), (33, 51),
-- Ti?p t?c t? c�u l?nh INSERT INTO HinhAnh_SanPhamChiTiet
-- Adidas UltraBoost 21 Cloud White (ID: 9) - 6 bi?n th? (ID: 52-57)
(34, 52), (35, 52), (36, 52), (37, 52), 
(34, 53), (35, 53), (36, 53), (37, 53), 
(34, 54), (35, 54), (36, 54), (37, 54), 
(34, 55), (35, 55), (36, 55), (37, 55), 
(34, 56), (35, 56), (36, 56), (37, 56), 
(34, 57), (35, 57), (36, 57), (37, 57),
-- Adidas Run EQ21 Black (ID: 10) - 6 bi?n th? (ID: 58-63)
(38, 58), (39, 58), (40, 58), (41, 58), 
(38, 59), (39, 59), (40, 59), (41, 59), 
(38, 60), (39, 60), (40, 60), (41, 60), 
(38, 61), (39, 61), (40, 61), (41, 61), 
(38, 62), (39, 62), (40, 62), (41, 62), 
(38, 63), (39, 63), (40, 63), (41, 63),
-- Adidas Barricade 13 Tennis Black (ID: 11) - 6 bi?n th? (ID: 64-69)
(42, 64), (43, 64), (44, 64), (45, 64), 
(42, 65), (43, 65), (44, 65), (45, 65), 
(42, 66), (43, 66), (44, 66), (45, 66), 
(42, 67), (43, 67), (44, 67), (45, 67), 
(42, 68), (43, 68), (44, 68), (45, 68), 
(42, 69), (43, 69), (44, 69), (45, 69),
-- Adidas Adizero Ubersonic 4 Crystal White (ID: 12) - 6 bi?n th? (ID: 70-75)
(46, 70), (47, 70), (48, 70), (49, 70), 
(46, 71), (47, 71), (48, 71), (49, 71), 
(46, 72), (47, 72), (48, 72), (49, 72), 
(46, 73), (47, 73), (48, 73), (49, 73), 
(46, 74), (47, 74), (48, 74), (49, 74), 
(46, 75), (47, 75), (48, 75), (49, 75),
-- Adidas Solematch Control 2 Semi Flash Aqua (ID: 13) - 6 bi?n th? (ID: 76-81)
(50, 76), (51, 76), (52, 76), (53, 76), 
(50, 77), (51, 77), (52, 77), (53, 77), 
(50, 78), (51, 78), (52, 78), (53, 78), 
(50, 79), (51, 79), (52, 79), (53, 79), 
(50, 80), (51, 80), (52, 80), (53, 80), 
(50, 81), (51, 81), (52, 81), (53, 81),
-- Converse Chuck Taylor All Star Low Flame (ID: 14) - 6 bi?n th? (ID: 82-87)
(54, 82), (55, 82), (56, 82), (57, 82), 
(54, 83), (55, 83), (56, 83), (57, 83), 
(54, 84), (55, 84), (56, 84), (57, 84), 
(54, 85), (55, 85), (56, 85), (57, 85), 
(54, 86), (55, 86), (56, 86), (57, 86), 
(54, 87), (55, 87), (56, 87), (57, 87),
-- Converse Chuck Taylor All Star Lift Ox White Black (ID: 15) - 6 bi?n th? (ID: 88-93)
(58, 88), (59, 88), (60, 88), (61, 88), 
(58, 89), (59, 89), (60, 89), (61, 89), 
(58, 90), (59, 90), (60, 90), (61, 90), 
(58, 91), (59, 91), (60, 91), (61, 91), 
(58, 92), (59, 92), (60, 92), (61, 92), 
(58, 93), (59, 93), (60, 93), (61, 93),
-- Converse Chuck Taylor All Star Cruise OX Black White (ID: 16) - 5 bi?n th? (ID: 94-98)
(62, 94), (63, 94), (64, 94), (65, 94), 
(62, 95), (63, 95), (64, 95), (65, 95), 
(62, 96), (63, 96), (64, 96), (65, 96), 
(62, 97), (63, 97), (64, 97), (65, 97), 
(62, 98), (63, 98), (64, 98), (65, 98),
-- Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black (ID: 17) - 6 bi?n th? (ID: 99-104)
(66, 99), (67, 99), (68, 99), (69, 99), 
(66, 100), (67, 100), (68, 100), (69, 100), 
(66, 101), (67, 101), (68, 101), (69, 101), 
(66, 102), (67, 102), (68, 102), (69, 102), 
(66, 103), (67, 103), (68, 103), (69, 103), 
(66, 104), (67, 104), (68, 104), (69, 104),
-- Converse Run Star Hike Low Black Gum (ID: 18) - 6 bi?n th? (ID: 105-110)
(70, 105), (71, 105), (72, 105), (73, 105), 
(70, 106), (71, 106), (72, 106), (73, 106), 
(70, 107), (71, 107), (72, 107), (73, 107), 
(70, 108), (71, 108), (72, 108), (73, 108), 
(70, 109), (71, 109), (72, 109), (73, 109), 
(70, 110), (71, 110), (72, 110), (73, 110),
-- Converse Run Star Motion Low White (ID: 19) - 6 bi?n th? (ID: 111-116)
(74, 111), (75, 111), (76, 111), (77, 111), 
(74, 112), (75, 112), (76, 112), (77, 112), 
(74, 113), (75, 113), (76, 113), (77, 113), 
(74, 114), (75, 114), (76, 114), (77, 114), 
(74, 115), (75, 115), (76, 115), (77, 115), 
(74, 116), (75, 116), (76, 116), (77, 116),
-- Converse Aeon Active CX OX Egret (ID: 20) - 5 bi?n th? (ID: 117-121)
(78, 117), (79, 117), (80, 117), (81, 117), 
(78, 118), (79, 118), (80, 118), (81, 118), 
(78, 119), (79, 119), (80, 119), (81, 119), 
(78, 120), (79, 120), (80, 120), (81, 120), 
(78, 121), (79, 121), (80, 121), (81, 121),
-- Converse Aeon Active CX Himalayan Salt (ID: 21) - 5 bi?n th? (ID: 122-126)
(82, 122), (83, 122), (84, 122), (85, 122), 
(82, 123), (83, 123), (84, 123), (85, 123), 
(82, 124), (83, 124), (84, 124), (85, 124), 
(82, 125), (83, 125), (84, 125), (85, 125), 
(82, 126), (83, 126), (84, 126), (85, 126),
-- CDG x Converse Chuck 70 Hi Black (ID: 22) - 6 bi?n th? (ID: 127-132)
(86, 127), (87, 127), (88, 127), (89, 127), 
(86, 128), (87, 128), (88, 128), (89, 128), 
(86, 129), (87, 129), (88, 129), (89, 129), 
(86, 130), (87, 130), (88, 130), (89, 130), 
(86, 131), (87, 131), (88, 131), (89, 131), 
(86, 132), (87, 132), (88, 132), (89, 132),
-- CDG x Converse Chuck 70 Low Black White (ID: 23) - 6 bi?n th? (ID: 133-138)
(90, 133), (91, 133), (92, 133), (93, 133), 
(90, 134), (91, 134), (92, 134), (93, 134), 
(90, 135), (91, 135), (92, 135), (93, 135), 
(90, 136), (91, 136), (92, 136), (93, 136), 
(90, 137), (91, 137), (92, 137), (93, 137), 
(90, 138), (91, 138), (92, 138), (93, 138),
-- CDG x Converse Chuck Taylor All Star Hi Milk (ID: 24) - 6 bi?n th? (ID: 139-144)
(94, 139), (95, 139), (96, 139), (97, 139), 
(94, 140), (95, 140), (96, 140), (97, 140), 
(94, 141), (95, 141), (96, 141), (97, 141), 
(94, 142), (95, 142), (96, 142), (97, 142), 
(94, 143), (95, 143), (96, 143), (97, 143), 
(94, 144), (95, 144), (96, 144), (97, 144),
-- Kim Jones x Converse Chuck 70 All Star Black (ID: 25) - 6 bi?n th? (ID: 145-150)
(98, 145), (99, 145), (100, 145), (101, 145), 
(98, 146), (99, 146), (100, 146), (101, 146), 
(98, 147), (99, 147), (100, 147), (101, 147), 
(98, 148), (99, 148), (100, 148), (101, 148), 
(98, 149), (99, 149), (100, 149), (101, 149), 
(98, 150), (99, 150), (100, 150), (101, 150),
-- Converse x Rick Owens DRKSHDW Weapon Beige Black (ID: 26) - 6 bi?n th? (ID: 151-156)
(102, 151), (103, 151), (104, 151), (105, 151), 
(102, 152), (103, 152), (104, 152), (105, 152), 
(102, 153), (103, 153), (104, 153), (105, 153), 
(102, 154), (103, 154), (104, 154), (105, 154), 
(102, 155), (103, 155), (104, 155), (105, 155), 
(102, 156), (103, 156), (104, 156), (105, 156),
-- Back To The Future x Converse All Star US MT Hi Black (ID: 27) - 6 bi?n th? (ID: 157-162)
(106, 157), (107, 157), (108, 157), (109, 157), 
(106, 158), (107, 158), (108, 158), (109, 158), 
(106, 159), (107, 159), (108, 159), (109, 159), 
(106, 160), (107, 160), (108, 160), (109, 160), 
(106, 161), (107, 161), (108, 161), (109, 161), 
(106, 162), (107, 162), (108, 162), (109, 162),
-- MLB BigBall Chunky Diamond Monogram NY Yankees Black (ID: 28) - 6 bi?n th? (ID: 163-168)
(110, 163), (111, 163), (112, 163), (113, 163), 
(110, 164), (111, 164), (112, 164), (113, 164), 
(110, 165), (111, 165), (112, 165), (113, 165), 
(110, 166), (111, 166), (112, 166), (113, 166), 
(110, 167), (111, 167), (112, 167), (113, 167), 
(110, 168), (111, 168), (112, 168), (113, 168),
-- MLB BigBall Chunky Diamond Monogram Boston Red Sox Beige (ID: 29) - 5 bi?n th? (ID: 169-173)
(114, 169), (115, 169), (116, 169), (117, 169), 
(114, 170), (115, 170), (116, 170), (117, 170), 
(114, 171), (115, 171), (116, 171), (117, 171), 
(114, 172), (115, 172), (116, 172), (117, 172), 
(114, 173), (115, 173), (116, 173), (117, 173),
-- MLB Bigball Chunky A New York Yankees (ID: 30) - 5 bi?n th? (ID: 174-178)
(118, 174), (119, 174), (120, 174), (121, 174), 
(118, 175), (119, 175), (120, 175), (121, 175), 
(118, 176), (119, 176), (120, 176), (121, 176), 
(118, 177), (119, 177), (120, 177), (121, 177), 
(118, 178), (119, 178), (120, 178), (121, 178),
-- MLB BigBall Chunky P Boston Red Sox (ID: 31) - 5 bi?n th? (ID: 179-183)
(122, 179), (123, 179), (124, 179), (125, 179), 
(122, 180), (123, 180), (124, 180), (125, 180), 
(122, 181), (123, 181), (124, 181), (125, 181), 
(122, 182), (123, 182), (124, 182), (125, 182), 
(122, 183), (123, 183), (124, 183), (125, 183),
-- MLB Chunky Liner New York Yankees Grey (ID: 32) - 5 bi?n th? (ID: 184-188)
(126, 184), (127, 184), (128, 184), (129, 184), 
(126, 185), (127, 185), (128, 185), (129, 185), 
(126, 186), (127, 186), (128, 186), (129, 186), 
(126, 187), (127, 187), (128, 187), (129, 187), 
(126, 188), (127, 188), (128, 188), (129, 188),
-- MLB Chunky Liner Low Boston Red Sox Beige (ID: 33) - 5 bi?n th? (ID: 189-193)
(130, 189), (131, 189), (132, 189), (133, 189), 
(130, 190), (131, 190), (132, 190), (133, 190), 
(130, 191), (131, 191), (132, 191), (133, 191), 
(130, 192), (131, 192), (132, 192), (133, 192), 
(130, 193), (131, 193), (132, 193), (133, 193),
-- MLB Chunky Liner Mid Denim Boston Red Sox D.Blue (ID: 34) - 5 bi?n th? (ID: 194-198)
(134, 194), (135, 194), (136, 194), (137, 194), 
(134, 195), (135, 195), (136, 195), (137, 195), 
(134, 196), (135, 196), (136, 196), (137, 196), 
(134, 197), (135, 197), (136, 197), (137, 197), 
(134, 198), (135, 198), (136, 198), (137, 198),
-- MLB Chunky Liner Mid Denim New York Yankees D.Navy (ID: 35) - 5 bi?n th? (ID: 199-203)
(138, 199), (139, 199), (140, 199), (141, 199), 
(138, 200), (139, 200), (140, 200), (141, 200), 
(138, 201), (139, 201), (140, 201), (141, 201), 
(138, 202), (139, 202), (140, 202), (141, 202), 
(138, 203), (139, 203), (140, 203), (141, 203),
-- MLB Chunky Liner SL Saffiano Boston Red Sox (ID: 36) - 5 bi?n th? (ID: 204-208)
(142, 204), (143, 204), (144, 204), (145, 204), 
(142, 205), (143, 205), (144, 205), (145, 205), 
(142, 206), (143, 206), (144, 206), (145, 206), 
(142, 207), (143, 207), (144, 207), (145, 207), 
(142, 208), (143, 208), (144, 208), (145, 208),
-- MLB Liner Basic New York Yankees Black (ID: 37) - 6 bi?n th? (ID: 209-214)
(146, 209), (147, 209), (148, 209), (149, 209), 
(146, 210), (147, 210), (148, 210), (149, 210), 
(146, 211), (147, 211), (148, 211), (149, 211), 
(146, 212), (147, 212), (148, 212), (149, 212), 
(146, 213), (147, 213), (148, 213), (149, 213), 
(146, 214), (147, 214), (148, 214), (149, 214),
-- MLB Liner Basic New York Yankees Green (ID: 38) - 5 bi?n th? (ID: 215-219)
(150, 215), (151, 215), (152, 215), (153, 215), 
(150, 216), (151, 216), (152, 216), (153, 216), 
(150, 217), (151, 217), (152, 217), (153, 217), 
(150, 218), (151, 218), (152, 218), (153, 218), 
(150, 219), (151, 219), (152, 219), (153, 219),
-- MLB Playball Mule Dia Monogram New York Yankees Beige (ID: 39) - 5 bi?n th? (ID: 220-224)
(154, 220), (155, 220), (156, 220), (157, 220), 
(154, 221), (155, 221), (156, 221), (157, 221), 
(154, 222), (155, 222), (156, 222), (157, 222), 
(154, 223), (155, 223), (156, 223), (157, 223), 
(154, 224), (155, 224), (156, 224), (157, 224),
-- MLB Playball Mule Mono NY New York Yankees (ID: 40) - 5 bi?n th? (ID: 225-229)
(158, 225), (159, 225), (160, 225), (161, 225), 
(158, 226), (159, 226), (160, 226), (161, 226), 
(158, 227), (159, 227), (160, 227), (161, 227), 
(158, 228), (159, 228), (160, 228), (161, 228), 
(158, 229), (159, 229), (160, 229), (161, 229);
GO

-- =============================================
-- D? LI?U M?U CHO GI? H�NG 
-- =============================================

INSERT INTO TaiKhoan (tai_khoan, mat_khau, role) VALUES
( N'admin', 123,N'admin'),
( N'user1', 123,N'user'),
( N'user2', 123,N'user'),
( N'user3', 123,N'user')
GO
INSERT INTO GioHang (id_tai_khoan, session_id, create_at, update_at) VALUES
-- Giỏ hàng cho user1@gmail.com (giả sử id_tai_khoan = 1)
(1, NULL, GETDATE(), GETDATE()),
-- Giỏ hàng cho user2@gmail.com (giả sử id_tai_khoan = 2)
(2, NULL, GETDATE(), GETDATE()),
-- Giỏ hàng cho user3@gmail.com (giả sử id_tai_khoan = 3)
(3, NULL, GETDATE(), GETDATE()),
-- Giỏ hàng cho khách (chưa đăng nhập) - sử dụng session_id
(NULL, 'GUEST_SESSION_001', GETDATE(), GETDATE()),
(NULL, 'GUEST_SESSION_002', GETDATE(), GETDATE());
GO
-- Gi? h�ng m?u cho c�c t�i kho?n
INSERT INTO GioHangItem (id_gio_hang, id_san_pham_chi_tiet, so_luong, gia_tai_thoi_diem, create_at, update_at) VALUES
-- Giỏ hàng của user1@gmail.com (ID: 1)
(3, 1, 2, 3500000, GETDATE(), GETDATE()),   -- Adidas adiFOM Supernova Triple Black - Size 40 - 2 đôi
(3, 15, 1, 3200000, GETDATE(), GETDATE()),  -- Adidas adiFOM Superstar Core Black - Size 39 - 1 đôi
(3, 35, 1, 4200000, GETDATE(), GETDATE()),  -- Adidas Barricade 13 Tennis Black - Size 42 - 1 đôi

-- Giỏ hàng của user2@gmail.com (ID: 2)
(4, 50, 2, 3800000, GETDATE(), GETDATE()),  -- Adidas Gazelle Bold Year of the Snake - Size 40 - 2 đôi
(4, 75, 1, 3600000, GETDATE(), GETDATE()),  -- Converse All Star - Size 39 - 1 đôi
(4, 100, 3, 4500000, GETDATE(), GETDATE()), -- Converse Chuck Taylor - Size 41 - 3 đôi

-- Giỏ hàng của user3@gmail.com (ID: 3)
(5, 125, 1, 3900000, GETDATE(), GETDATE()), -- MLB Chunky Liner - Size 40 - 1 đôi
(5, 150, 2, 4100000, GETDATE(), GETDATE()), -- MLB Liner Basic - Size 41 - 2 đôi

-- Giỏ hàng của khách (chưa đăng nhập) - GUEST_SESSION_001 (ID: 4)
(5, 25, 1, 3300000, GETDATE(), GETDATE()),  -- Adidas adiFOM Superstar Core White - Size 40 - 1 đôi
(5, 85, 2, 3700000, GETDATE(), GETDATE()),  -- Converse High Top - Size 39 - 2 đôi

-- Giỏ hàng của khách (chưa đăng nhập) - GUEST_SESSION_002 (ID: 5)
(5, 175, 1, 4000000, GETDATE(), GETDATE()),  -- MLB Playball Mule - Size 42 - 1 đôi
(5, 200, 2, 3500000, GETDATE(), GETDATE());  -- Sản phẩm khác - Size 40 - 2 đôi
GO
UPDATE TaiKhoan
SET role = 'ADMIN'
WHERE tai_khoan = 'admin';
go
UPDATE TaiKhoan
SET role = 'USER'
WHERE tai_khoan = user;
