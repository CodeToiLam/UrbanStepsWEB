USE UrbanStepsDB;
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

-- 8. Màu sắc (Cập nhật theo tên file ảnh)
INSERT INTO MauSac (ten_mau_sac) VALUES 
(N'Đen'),                    -- ID: 1 (Black, Core Black, Triple Black)
(N'Trắng'),                  -- ID: 2 (White, Core White, Cloud White, Wonder White)
(N'Xám'),                    -- ID: 3 (Grey, Steel Grey)
(N'Xanh Navy'),              -- ID: 4
(N'Đỏ'),                     -- ID: 5 (Red, Maroon)
(N'Vàng'),                   -- ID: 6
(N'Nâu'),                    -- ID: 7 (Brown, Gum)
(N'Hồng'),                   -- ID: 8 (Pink, Wonder Quartz)
(N'Xanh Dương'),             -- ID: 9 (Blue, Semi Flash Aqua, Crystal White)
(N'Xanh Lá'),                -- ID: 10 (Green)
(N'Tím'),                    -- ID: 11 (Purple, Denim)
(N'Cam'),                    -- ID: 12 (Orange, Flame)
(N'Be'),                    -- ID: 13 (Silver, Egret, Himalayan Salt, Beige)
(N'Xám Nhạt'),               -- ID: 14 (Light Grey)
(N'Đen Trắng'),              -- ID: 15 (Black White combination)
(N'Đỏ Maroon'),              -- ID: 16 (Maroon specific)
(N'Snake Pattern'),          -- ID: 17 (Year of the Snake special)
(N'Semi Flash Aqua'),        -- ID: 18 (Tennis specific color)
(N'Milk White'),             -- ID: 19 (CDG Milk color)
(N'D.Navy'),                 -- ID: 20 (Denim Navy)
(N'D.Blue');                 -- ID: 21 (Denim Blue)
GO
-- 9. Hình ảnh (Dựa trên cấu trúc thật trong thư mục) - Sắp xếp lại theo thứ tự sản phẩm
INSERT INTO HinhAnh (duong_dan, mo_ta, thu_tu, la_anh_chinh) VALUES 

-- Banner images cho trang chủ (ID: 1-3)
('/images/Home/banner adidas.jpg', N'Banner Adidas', 1, 1),
('/images/Home/banner converse.jpg', N'Banner Converse', 1, 1),
('/images/Home/banner mlb.jpg', N'Banner MLB', 1, 1),

-- ========================================
-- ADIDAS PRODUCTS (13 sản phẩm) - ID: 4-55
-- ========================================

-- 1. Samba OG White Black Gum (ID: 4-7)
('/images/adidas/samba-og-white-black-gum/main.jpg', N'Adidas Samba OG White Black Gum - Main', 1, 1),
('/images/adidas/samba-og-white-black-gum/2.jpg', N'Adidas Samba OG White Black Gum - Side', 2, 0),
('/images/adidas/samba-og-white-black-gum/3.jpg', N'Adidas Samba OG White Black Gum - Detail', 3, 0),
('/images/adidas/samba-og-white-black-gum/4.jpg', N'Adidas Samba OG White Black Gum - Back', 4, 0),

-- 2. Samba OG Wonder White Maroon (ID: 8-11)
('/images/adidas/samba-og-wonder-white-maroon/main.jpg', N'Adidas Samba OG Wonder White Maroon - Main', 1, 1),
('/images/adidas/samba-og-wonder-white-maroon/2.jpg', N'Adidas Samba OG Wonder White Maroon - Side', 2, 0),
('/images/adidas/samba-og-wonder-white-maroon/3.jpg', N'Adidas Samba OG Wonder White Maroon - Detail', 3, 0),
('/images/adidas/samba-og-wonder-white-maroon/4.jpg', N'Adidas Samba OG Wonder White Maroon - Back', 4, 0),

-- 3. Samba OG Cloud White Wonder Quartz Wmns (ID: 12-15)
('/images/adidas/samba-og-cloud-white-wonder-quartz-wmns/main.jpg', N'Adidas Samba OG Cloud White Wonder Quartz Wmns - Main', 1, 1),
('/images/adidas/samba-og-cloud-white-wonder-quartz-wmns/2.jpg', N'Adidas Samba OG Cloud White Wonder Quartz Wmns - Side', 2, 0),
('/images/adidas/samba-og-cloud-white-wonder-quartz-wmns/3.jpg', N'Adidas Samba OG Cloud White Wonder Quartz Wmns - Detail', 3, 0),
('/images/adidas/samba-og-cloud-white-wonder-quartz-wmns/4.jpg', N'Adidas Samba OG Cloud White Wonder Quartz Wmns - Back', 4, 0),

-- 4. Gazelle Bold Year of the Snake (ID: 16-19)
('/images/adidas/gazelle-bold-year-of-the-snake/main.jpg', N'Adidas Gazelle Bold Year of the Snake - Main', 1, 1),
('/images/adidas/gazelle-bold-year-of-the-snake/2.jpg', N'Adidas Gazelle Bold Year of the Snake - Side', 2, 0),
('/images/adidas/gazelle-bold-year-of-the-snake/3.jpg', N'Adidas Gazelle Bold Year of the Snake - Detail', 3, 0),
('/images/adidas/gazelle-bold-year-of-the-snake/4.jpg', N'Adidas Gazelle Bold Year of the Snake - Back', 4, 0),

-- 5. Yeezy Boost 350 V2 Steel Grey (ID: 20-23)
('/images/adidas/yeezy-boost-350-v2-steel-grey/main.jpg', N'Adidas Yeezy Boost 350 V2 Steel Grey - Main', 1, 1),
('/images/adidas/yeezy-boost-350-v2-steel-grey/2.jpg', N'Adidas Yeezy Boost 350 V2 Steel Grey - Side', 2, 0),
('/images/adidas/yeezy-boost-350-v2-steel-grey/3.jpg', N'Adidas Yeezy Boost 350 V2 Steel Grey - Detail', 3, 0),
('/images/adidas/yeezy-boost-350-v2-steel-grey/4.jpg', N'Adidas Yeezy Boost 350 V2 Steel Grey - Back', 4, 0),

-- 6. Adifom Superstar Core Black (ID: 24-27)
('/images/adidas/adifom-superstar-core-black/main.jpg', N'Adidas Adifom Superstar Core Black - Main', 1, 1),
('/images/adidas/adifom-superstar-core-black/2.jpg', N'Adidas Adifom Superstar Core Black - Side', 2, 0),
('/images/adidas/adifom-superstar-core-black/3.jpg', N'Adidas Adifom Superstar Core Black - Detail', 3, 0),
('/images/adidas/adifom-superstar-core-black/4.jpg', N'Adidas Adifom Superstar Core Black - Back', 4, 0),

-- 7. Adifom Superstar Core White (ID: 28-31)
('/images/adidas/adifom-superstar-core-white/main.jpg', N'Adidas Adifom Superstar Core White - Main', 1, 1),
('/images/adidas/adifom-superstar-core-white/2.jpg', N'Adidas Adifom Superstar Core White - Side', 2, 0),
('/images/adidas/adifom-superstar-core-white/3.jpg', N'Adidas Adifom Superstar Core White - Detail', 3, 0),
('/images/adidas/adifom-superstar-core-white/4.jpg', N'Adidas Adifom Superstar Core White - Back', 4, 0),

-- 8. Adifom Supernova Triple Black (ID: 32-35)
('/images/adidas/adifom-supernova-triple-black/main.jpg', N'Adidas Adifom Supernova Triple Black - Main', 1, 1),
('/images/adidas/adifom-supernova-triple-black/2.jpg', N'Adidas Adifom Supernova Triple Black - Side', 2, 0),
('/images/adidas/adifom-supernova-triple-black/3.jpg', N'Adidas Adifom Supernova Triple Black - Detail', 3, 0),
('/images/adidas/adifom-supernova-triple-black/4.jpg', N'Adidas Adifom Supernova Triple Black - Back', 4, 0),

-- 9. UltraBoost 21 Cloud White (ID: 36-39)
('/images/adidas/th-thao-adidas-ultraboost-21-cloud-white/main.jpg', N'Adidas UltraBoost 21 Cloud White - Main', 1, 1),
('/images/adidas/th-thao-adidas-ultraboost-21-cloud-white/2.jpg', N'Adidas UltraBoost 21 Cloud White - Side', 2, 0),
('/images/adidas/th-thao-adidas-ultraboost-21-cloud-white/3.jpg', N'Adidas UltraBoost 21 Cloud White - Detail', 3, 0),
('/images/adidas/th-thao-adidas-ultraboost-21-cloud-white/4.jpg', N'Adidas UltraBoost 21 Cloud White - Back', 4, 0),

-- 10. Run EQ21 Black (ID: 40-45)
('/images/adidas/run-eq21-black/main.jpg', N'Adidas Run EQ21 Black - Main', 1, 1),
('/images/adidas/run-eq21-black/2.jpg', N'Adidas Run EQ21 Black - Side', 2, 0),
('/images/adidas/run-eq21-black/3.jpg', N'Adidas Run EQ21 Black - Detail', 3, 0),
('/images/adidas/run-eq21-black/4.jpg', N'Adidas Run EQ21 Black - Back', 4, 0),
('/images/adidas/run-eq21-black/5.jpg', N'Adidas Run EQ21 Black - Sole', 5, 0),
('/images/adidas/run-eq21-black/6.jpg', N'Adidas Run EQ21 Black - Lifestyle', 6, 0),

-- 11. Barricade 13 Tennis Black (ID: 46-49)
('/images/adidas/barricade-13-tennis-black/main.jpg', N'Adidas Barricade 13 Tennis Black - Main', 1, 1),
('/images/adidas/barricade-13-tennis-black/2.jpg', N'Adidas Barricade 13 Tennis Black - Side', 2, 0),
('/images/adidas/barricade-13-tennis-black/3.jpg', N'Adidas Barricade 13 Tennis Black - Detail', 3, 0),
('/images/adidas/barricade-13-tennis-black/4.jpg', N'Adidas Barricade 13 Tennis Black - Back', 4, 0),

-- 12. Adizero Ubersonic 4 Crystal White (ID: 50-53)
('/images/adidas/tennispickleball-adidas-adizero-ubersonic-4-crystal-white-semi-flash-aqua/main.jpg', N'Adidas Adizero Ubersonic 4 Crystal White - Main', 1, 1),
('/images/adidas/tennispickleball-adidas-adizero-ubersonic-4-crystal-white-semi-flash-aqua/2.jpg', N'Adidas Adizero Ubersonic 4 Crystal White - Side', 2, 0),
('/images/adidas/tennispickleball-adidas-adizero-ubersonic-4-crystal-white-semi-flash-aqua/3.jpg', N'Adidas Adizero Ubersonic 4 Crystal White - Detail', 3, 0),
('/images/adidas/tennispickleball-adidas-adizero-ubersonic-4-crystal-white-semi-flash-aqua/4.jpg', N'Adidas Adizero Ubersonic 4 Crystal White - Back', 4, 0),

-- 13. Solematch Control 2 Semi Flash Aqua (ID: 54-57)
('/images/adidas/tennispickleball-adidas-solematch-control-2-semi-flash-aqua/main.jpg', N'Adidas Solematch Control 2 Semi Flash Aqua - Main', 1, 1),
('/images/adidas/tennispickleball-adidas-solematch-control-2-semi-flash-aqua/2.jpg', N'Adidas Solematch Control 2 Semi Flash Aqua - Side', 2, 0),
('/images/adidas/tennispickleball-adidas-solematch-control-2-semi-flash-aqua/3.jpg', N'Adidas Solematch Control 2 Semi Flash Aqua - Detail', 3, 0),
('/images/adidas/tennispickleball-adidas-solematch-control-2-semi-flash-aqua/4.jpg', N'Adidas Solematch Control 2 Semi Flash Aqua - Back', 4, 0),

-- ========================================
-- CONVERSE PRODUCTS (14 sản phẩm)
-- ========================================

-- 1. Aeon Active CX Himalayan Salt
('/images/converse/aeon-active-cx-himalayan-salt/main.jpg', N'Converse Aeon Active CX Himalayan Salt - Main', 1, 1),
('/images/converse/aeon-active-cx-himalayan-salt/2.jpg', N'Converse Aeon Active CX Himalayan Salt - Side', 2, 0),
('/images/converse/aeon-active-cx-himalayan-salt/3.jpg', N'Converse Aeon Active CX Himalayan Salt - Detail', 3, 0),
('/images/converse/aeon-active-cx-himalayan-salt/4.jpg', N'Converse Aeon Active CX Himalayan Salt - Back', 4, 0),

-- 2. Aeon Active CX OX Egret
('/images/converse/aeon-active-cx-ox-egret/main.jpg', N'Converse Aeon Active CX OX Egret - Main', 1, 1),
('/images/converse/aeon-active-cx-ox-egret/2.jpg', N'Converse Aeon Active CX OX Egret - Side', 2, 0),
('/images/converse/aeon-active-cx-ox-egret/3.jpg', N'Converse Aeon Active CX OX Egret - Detail', 3, 0),
('/images/converse/aeon-active-cx-ox-egret/4.jpg', N'Converse Aeon Active CX OX Egret - Back', 4, 0),

-- 3. Back to the Future All Star US MT Hi Black
('/images/converse/back-to-the-future-converse-all-star-us-mt-hi-black/main.jpg', N'Back To The Future x Converse All Star US MT Hi Black - Main', 1, 1),
('/images/converse/back-to-the-future-converse-all-star-us-mt-hi-black/2.jpg', N'Back To The Future x Converse All Star US MT Hi Black - Side', 2, 0),
('/images/converse/back-to-the-future-converse-all-star-us-mt-hi-black/3.jpg', N'Back To The Future x Converse All Star US MT Hi Black - Detail', 3, 0),
('/images/converse/back-to-the-future-converse-all-star-us-mt-hi-black/4.jpg', N'Back To The Future x Converse All Star US MT Hi Black - Back', 4, 0),

-- 4. Chuck Taylor All Star Cruise OX Black White
('/images/converse/chuck-taylor-all-star-cruise-ox-black-white/main.jpg', N'Converse Chuck Taylor All Star Cruise OX Black White - Main', 1, 1),
('/images/converse/chuck-taylor-all-star-cruise-ox-black-white/2.jpg', N'Converse Chuck Taylor All Star Cruise OX Black White - Side', 2, 0),
('/images/converse/chuck-taylor-all-star-cruise-ox-black-white/3.jpg', N'Converse Chuck Taylor All Star Cruise OX Black White - Detail', 3, 0),
('/images/converse/chuck-taylor-all-star-cruise-ox-black-white/4.jpg', N'Converse Chuck Taylor All Star Cruise OX Black White - Back', 4, 0),

-- 5. Chuck Taylor All Star Lift OX White Black
('/images/converse/chuck-taylor-all-star-lift-ox-white-black/main.jpg', N'Converse Chuck Taylor All Star Lift OX White Black - Main', 1, 1),
('/images/converse/chuck-taylor-all-star-lift-ox-white-black/2.jpg', N'Converse Chuck Taylor All Star Lift OX White Black - Side', 2, 0),
('/images/converse/chuck-taylor-all-star-lift-ox-white-black/3.jpg', N'Converse Chuck Taylor All Star Lift OX White Black - Detail', 3, 0),
('/images/converse/chuck-taylor-all-star-lift-ox-white-black/4.jpg', N'Converse Chuck Taylor All Star Lift OX White Black - Back', 4, 0),

-- 6. Chuck Taylor All Star Low Flame
('/images/converse/chuck-taylor-all-star-low-flame/main.webp', N'Converse Chuck Taylor All Star Low Flame - Main', 1, 1),
('/images/converse/chuck-taylor-all-star-low-flame/2.webp', N'Converse Chuck Taylor All Star Low Flame - Side', 2, 0),
('/images/converse/chuck-taylor-all-star-low-flame/3.webp', N'Converse Chuck Taylor All Star Low Flame - Detail', 3, 0),
('/images/converse/chuck-taylor-all-star-low-flame/4.webp', N'Converse Chuck Taylor All Star Low Flame - Back', 4, 0),

-- 7. CDG Play x Chuck 70 Low Black White
('/images/converse/comme-des-gar-ons-play-x-chuck-70-low-black-white/main.webp', N'CDG Play x Converse Chuck 70 Low Black White - Main', 1, 1),
('/images/converse/comme-des-gar-ons-play-x-chuck-70-low-black-white/2.webp', N'CDG Play x Converse Chuck 70 Low Black White - Side', 2, 0),
('/images/converse/comme-des-gar-ons-play-x-chuck-70-low-black-white/3.webp', N'CDG Play x Converse Chuck 70 Low Black White - Detail', 3, 0),
('/images/converse/comme-des-gar-ons-play-x-chuck-70-low-black-white/4.webp', N'CDG Play x Converse Chuck 70 Low Black White - Back', 4, 0),

-- 8. CDG x Chuck Taylor All Star Hi Milk  
('/images/converse/comme-des-gar-ons-x-chuck-taylor-all-star-hi-milk/main.webp', N'CDG x Converse Chuck Taylor All Star Hi Milk - Main', 1, 1),
('/images/converse/comme-des-gar-ons-x-chuck-taylor-all-star-hi-milk/2.webp', N'CDG x Converse Chuck Taylor All Star Hi Milk - Side', 2, 0),
('/images/converse/comme-des-gar-ons-x-chuck-taylor-all-star-hi-milk/3.webp', N'CDG x Converse Chuck Taylor All Star Hi Milk - Detail', 3, 0),
('/images/converse/comme-des-gar-ons-x-chuck-taylor-all-star-hi-milk/4.webp', N'CDG x Converse Chuck Taylor All Star Hi Milk - Back', 4, 0),

-- 9. Kim Jones x Chuck 70 All Star Black
('/images/converse/kim-jones-x-converse-chuck-70-all-star-black/main.jpg', N'Kim Jones x Converse Chuck 70 All Star Black - Main', 1, 1),
('/images/converse/kim-jones-x-converse-chuck-70-all-star-black/2.jpg', N'Kim Jones x Converse Chuck 70 All Star Black - Side', 2, 0),
('/images/converse/kim-jones-x-converse-chuck-70-all-star-black/3.jpg', N'Kim Jones x Converse Chuck 70 All Star Black - Detail', 3, 0),
('/images/converse/kim-jones-x-converse-chuck-70-all-star-black/4.jpg', N'Kim Jones x Converse Chuck 70 All Star Black - Back', 4, 0),

-- 10. Run Star Hike Low Black Gum
('/images/converse/run-star-hike-low-black-gum/main.jpg', N'Converse Run Star Hike Low Black Gum - Main', 1, 1),
('/images/converse/run-star-hike-low-black-gum/2.jpg', N'Converse Run Star Hike Low Black Gum - Side', 2, 0),
('/images/converse/run-star-hike-low-black-gum/3.jpg', N'Converse Run Star Hike Low Black Gum - Detail', 3, 0),
('/images/converse/run-star-hike-low-black-gum/4.jpg', N'Converse Run Star Hike Low Black Gum - Back', 4, 0),

-- 11. Run Star Motion Low White
('/images/converse/run-star-motion-low-white/main.jpg', N'Converse Run Star Motion Low White - Main', 1, 1),
('/images/converse/run-star-motion-low-white/2.jpg', N'Converse Run Star Motion Low White - Side', 2, 0),
('/images/converse/run-star-motion-low-white/3.jpg', N'Converse Run Star Motion Low White - Detail', 3, 0),
('/images/converse/run-star-motion-low-white/4.jpg', N'Converse Run Star Motion Low White - Back', 4, 0),

-- 12. CDG Play x Chuck Taylor All Star 70 Hi Black (Thư mục đặc biệt)
('/images/converse/Shoes-Comme-des-Garcons-Play-x-Converse-Chuck-Taylor-All-Star-70-Hi-Black-150204C/main.jpg', N'CDG Play x Converse Chuck Taylor All Star 70 Hi Black - Main', 1, 1),
('/images/converse/Shoes-Comme-des-Garcons-Play-x-Converse-Chuck-Taylor-All-Star-70-Hi-Black-150204C/2.jpg', N'CDG Play x Converse Chuck Taylor All Star 70 Hi Black - Side', 2, 0),
('/images/converse/Shoes-Comme-des-Garcons-Play-x-Converse-Chuck-Taylor-All-Star-70-Hi-Black-150204C/3.jpg', N'CDG Play x Converse Chuck Taylor All Star 70 Hi Black - Detail', 3, 0),
('/images/converse/Shoes-Comme-des-Garcons-Play-x-Converse-Chuck-Taylor-All-Star-70-Hi-Black-150204C/4.jpg', N'CDG Play x Converse Chuck Taylor All Star 70 Hi Black - Back', 4, 0),

-- 13. Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black (Thư mục đặc biệt)
('/images/converse/Shoes-Converse-Chuck-Taylor-All-Star-EVA-Lift-Platform-Y2K-Heart-High-Top-Black-A09121C/main.jpg', N'Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black - Main', 1, 1),
('/images/converse/Shoes-Converse-Chuck-Taylor-All-Star-EVA-Lift-Platform-Y2K-Heart-High-Top-Black-A09121C/2.jpg', N'Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black - Side', 2, 0),
('/images/converse/Shoes-Converse-Chuck-Taylor-All-Star-EVA-Lift-Platform-Y2K-Heart-High-Top-Black-A09121C/3.jpg', N'Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black - Detail', 3, 0),
('/images/converse/Shoes-Converse-Chuck-Taylor-All-Star-EVA-Lift-Platform-Y2K-Heart-High-Top-Black-A09121C/4.jpg', N'Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black - Back', 4, 0),

-- 14. x Rick Owens DRKSHDW Weapon Beige Black
('/images/converse/x-rick-owens-drkshdw-weapon-beige-black/main.jpg', N'Converse x Rick Owens DRKSHDW Weapon Beige Black - Main', 1, 1),
('/images/converse/x-rick-owens-drkshdw-weapon-beige-black/2.jpg', N'Converse x Rick Owens DRKSHDW Weapon Beige Black - Side', 2, 0),
('/images/converse/x-rick-owens-drkshdw-weapon-beige-black/3.jpg', N'Converse x Rick Owens DRKSHDW Weapon Beige Black - Detail', 3, 0),
('/images/converse/x-rick-owens-drkshdw-weapon-beige-black/4.jpg', N'Converse x Rick Owens DRKSHDW Weapon Beige Black - Back', 4, 0),

-- ========================================
-- MLB PRODUCTS (13 sản phẩm)
-- ========================================

-- 1. Bigball Chunky A New York Yankees  
('/images/mlb/bigball-chunky-a-new-york-yankees-3ashc101n/main.webp', N'MLB Bigball Chunky A New York Yankees - Main', 1, 1),
('/images/mlb/bigball-chunky-a-new-york-yankees-3ashc101n/2.webp', N'MLB Bigball Chunky A New York Yankees - Side', 2, 0),
('/images/mlb/bigball-chunky-a-new-york-yankees-3ashc101n/3.webp', N'MLB Bigball Chunky A New York Yankees - Detail', 3, 0),
('/images/mlb/bigball-chunky-a-new-york-yankees-3ashc101n/4.webp', N'MLB Bigball Chunky A New York Yankees - Back', 4, 0),

-- 2. Bigball Chunky Diamond Monogram Boston Red Sox D Beige
('/images/mlb/bigball-chunky-diamond-monogram-boston-red-sox-d-beige-3ashcdm2n/main.webp', N'MLB Bigball Chunky Diamond Monogram Boston Red Sox D Beige - Main', 1, 1),
('/images/mlb/bigball-chunky-diamond-monogram-boston-red-sox-d-beige-3ashcdm2n/2.webp', N'MLB Bigball Chunky Diamond Monogram Boston Red Sox D Beige - Side', 2, 0),
('/images/mlb/bigball-chunky-diamond-monogram-boston-red-sox-d-beige-3ashcdm2n/3.webp', N'MLB Bigball Chunky Diamond Monogram Boston Red Sox D Beige - Detail', 3, 0),
('/images/mlb/bigball-chunky-diamond-monogram-boston-red-sox-d-beige-3ashcdm2n/4.webp', N'MLB Bigball Chunky Diamond Monogram Boston Red Sox D Beige - Back', 4, 0),

-- 3. Bigball Chunky Diamond Monogram New York Yankees Black
('/images/mlb/bigball-chunky-diamond-monogram-new-york-yankees-black-3ashcdm2n/main.webp', N'MLB Bigball Chunky Diamond Monogram New York Yankees Black - Main', 1, 1),
('/images/mlb/bigball-chunky-diamond-monogram-new-york-yankees-black-3ashcdm2n/2.webp', N'MLB Bigball Chunky Diamond Monogram New York Yankees Black - Side', 2, 0),
('/images/mlb/bigball-chunky-diamond-monogram-new-york-yankees-black-3ashcdm2n/3.webp', N'MLB Bigball Chunky Diamond Monogram New York Yankees Black - Detail', 3, 0),
('/images/mlb/bigball-chunky-diamond-monogram-new-york-yankees-black-3ashcdm2n/4.webp', N'MLB Bigball Chunky Diamond Monogram New York Yankees Black - Back', 4, 0),

-- 4. Bigball Chunky P Boston Red Sox
('/images/mlb/bigball-chunky-p-boston-red-sox-32shc2111-43i/main.webp', N'MLB Bigball Chunky P Boston Red Sox - Main', 1, 1),
('/images/mlb/bigball-chunky-p-boston-red-sox-32shc2111-43i/2.webp', N'MLB Bigball Chunky P Boston Red Sox - Side', 2, 0),
('/images/mlb/bigball-chunky-p-boston-red-sox-32shc2111-43i/3.webp', N'MLB Bigball Chunky P Boston Red Sox - Detail', 3, 0),
('/images/mlb/bigball-chunky-p-boston-red-sox-32shc2111-43i/4.webp', N'MLB Bigball Chunky P Boston Red Sox - Back', 4, 0),

-- 5. Chunky Liner Low Boston Red Sox Beige
('/images/mlb/chunky-liner-low-boston-red-sox-beige-3asxca12n/main.webp', N'MLB Chunky Liner Low Boston Red Sox Beige - Main', 1, 1),
('/images/mlb/chunky-liner-low-boston-red-sox-beige-3asxca12n/2.webp', N'MLB Chunky Liner Low Boston Red Sox Beige - Side', 2, 0),
('/images/mlb/chunky-liner-low-boston-red-sox-beige-3asxca12n/3.webp', N'MLB Chunky Liner Low Boston Red Sox Beige - Detail', 3, 0),
('/images/mlb/chunky-liner-low-boston-red-sox-beige-3asxca12n/4.webp', N'MLB Chunky Liner Low Boston Red Sox Beige - Back', 4, 0),

-- 6. Chunky Liner Mid Denim Boston Red Sox D Blue
('/images/mlb/chunky-liner-mid-denim-boston-red-sox-d-blue-3asxcdn3n/main.webp', N'MLB Chunky Liner Mid Denim Boston Red Sox D Blue - Main', 1, 1),
('/images/mlb/chunky-liner-mid-denim-boston-red-sox-d-blue-3asxcdn3n/2.webp', N'MLB Chunky Liner Mid Denim Boston Red Sox D Blue - Side', 2, 0),
('/images/mlb/chunky-liner-mid-denim-boston-red-sox-d-blue-3asxcdn3n/3.webp', N'MLB Chunky Liner Mid Denim Boston Red Sox D Blue - Detail', 3, 0),
('/images/mlb/chunky-liner-mid-denim-boston-red-sox-d-blue-3asxcdn3n/4.webp', N'MLB Chunky Liner Mid Denim Boston Red Sox D Blue - Back', 4, 0),

-- 7. Chunky Liner Mid Denim New York Yankees D Navy
('/images/mlb/chunky-liner-mid-denim-new-york-yankees-d-navy-3asxcdn3n/main.webp', N'MLB Chunky Liner Mid Denim New York Yankees D Navy - Main', 1, 1),
('/images/mlb/chunky-liner-mid-denim-new-york-yankees-d-navy-3asxcdn3n/2.webp', N'MLB Chunky Liner Mid Denim New York Yankees D Navy - Side', 2, 0),
('/images/mlb/chunky-liner-mid-denim-new-york-yankees-d-navy-3asxcdn3n/3.webp', N'MLB Chunky Liner Mid Denim New York Yankees D Navy - Detail', 3, 0),
('/images/mlb/chunky-liner-mid-denim-new-york-yankees-d-navy-3asxcdn3n/4.webp', N'MLB Chunky Liner Mid Denim New York Yankees D Navy - Back', 4, 0),

-- 8. Chunky Liner New York Yankees Grey  
('/images/mlb/chunky-liner-new-york-yankees-grey-3asxca12n/main.webp', N'MLB Chunky Liner New York Yankees Grey - Main', 1, 1),
('/images/mlb/chunky-liner-new-york-yankees-grey-3asxca12n/2.webp', N'MLB Chunky Liner New York Yankees Grey - Side', 2, 0),
('/images/mlb/chunky-liner-new-york-yankees-grey-3asxca12n/3.webp', N'MLB Chunky Liner New York Yankees Grey - Detail', 3, 0),
('/images/mlb/chunky-liner-new-york-yankees-grey-3asxca12n/4.webp', N'MLB Chunky Liner New York Yankees Grey - Back', 4, 0),

-- 9. Chunky Liner SL Saffiano Boston Red Sox
('/images/mlb/chunky-liner-sl-saffiano-boston-red-sox-3asxcls4n/main.webp', N'MLB Chunky Liner SL Saffiano Boston Red Sox - Main', 1, 1),
('/images/mlb/chunky-liner-sl-saffiano-boston-red-sox-3asxcls4n/2.webp', N'MLB Chunky Liner SL Saffiano Boston Red Sox - Side', 2, 0),
('/images/mlb/chunky-liner-sl-saffiano-boston-red-sox-3asxcls4n/3.webp', N'MLB Chunky Liner SL Saffiano Boston Red Sox - Detail', 3, 0),
('/images/mlb/chunky-liner-sl-saffiano-boston-red-sox-3asxcls4n/4.webp', N'MLB Chunky Liner SL Saffiano Boston Red Sox - Back', 4, 0),

-- 10. Liner Basic New York Yankees Black
('/images/mlb/liner-basic-new-york-yankees-black-3asxclb3n/main.webp', N'MLB Liner Basic New York Yankees Black - Main', 1, 1),
('/images/mlb/liner-basic-new-york-yankees-black-3asxclb3n/2.webp', N'MLB Liner Basic New York Yankees Black - Side', 2, 0),
('/images/mlb/liner-basic-new-york-yankees-black-3asxclb3n/3.webp', N'MLB Liner Basic New York Yankees Black - Detail', 3, 0),
('/images/mlb/liner-basic-new-york-yankees-black-3asxclb3n/4.webp', N'MLB Liner Basic New York Yankees Black - Back', 4, 0),

-- 11. Liner Basic New York Yankees Green
('/images/mlb/liner-basic-new-york-yankees-green-3asxclb3n/main.webp', N'MLB Liner Basic New York Yankees Green - Main', 1, 1),
('/images/mlb/liner-basic-new-york-yankees-green-3asxclb3n/2.webp', N'MLB Liner Basic New York Yankees Green - Side', 2, 0),
('/images/mlb/liner-basic-new-york-yankees-green-3asxclb3n/3.webp', N'MLB Liner Basic New York Yankees Green - Detail', 3, 0),
('/images/mlb/liner-basic-new-york-yankees-green-3asxclb3n/4.webp', N'MLB Liner Basic New York Yankees Green - Back', 4, 0),

-- 12. Playball Mule Dia Monogram New York Yankees Beige
('/images/mlb/playball-mule-dia-monogram-new-york-yankees-beige-3amumda2n/main.webp', N'MLB Playball Mule Dia Monogram New York Yankees Beige - Main', 1, 1),
('/images/mlb/playball-mule-dia-monogram-new-york-yankees-beige-3amumda2n/2.webp', N'MLB Playball Mule Dia Monogram New York Yankees Beige - Side', 2, 0),
('/images/mlb/playball-mule-dia-monogram-new-york-yankees-beige-3amumda2n/3.webp', N'MLB Playball Mule Dia Monogram New York Yankees Beige - Detail', 3, 0),
('/images/mlb/playball-mule-dia-monogram-new-york-yankees-beige-3amumda2n/4.webp', N'MLB Playball Mule Dia Monogram New York Yankees Beige - Back', 4, 0),

-- 13. Playball Mule Mono NY New York Yankees  
('/images/mlb/playball-mule-mono-ny-new-york-yankees-32shsm111-50l/main.webp', N'MLB Playball Mule Mono NY New York Yankees - Main', 1, 1),
('/images/mlb/playball-mule-mono-ny-new-york-yankees-32shsm111-50l/2.webp', N'MLB Playball Mule Mono NY New York Yankees - Side', 2, 0),
('/images/mlb/playball-mule-mono-ny-new-york-yankees-32shsm111-50l/3.webp', N'MLB Playball Mule Mono NY New York Yankees - Detail', 3, 0),
('/images/mlb/playball-mule-mono-ny-new-york-yankees-32shsm111-50l/4.webp', N'MLB Playball Mule Mono NY New York Yankees - Back', 4, 0);
GO

-- 10. Sản phẩm (40 sản phẩm)
-- ADIDAS (13 sản phẩm) - Cập nhật ID ảnh đại diện cho đúng theo thứ tự mới
INSERT INTO SanPham (id_loai_san_pham, id_danh_muc, id_thuong_hieu, id_xuat_xu, id_kieu_dang, id_chat_lieu, id_hinh_anh_dai_dien, ma_san_pham, ten_san_pham, mo_ta, gia_nhap, gia_ban, la_hot, la_sale, phan_tram_giam, trang_thai) VALUES 
-- 1. Samba OG White Black Gum map với ảnh ID 4 (samba-og-white-black-gum/main.jpg)
(2, 3, 1, 2, 6, 5, 4, N'ADI-SAMBA-001', N'Adidas Samba OG White Black Gum', N'Giày Adidas Samba OG phiên bản classic với phối màu trắng đen gum iconic.', 1800000, 2590000, 1, 0, 0, 1),
-- 2. Samba OG Wonder White Maroon map với ảnh ID 8 (samba-og-wonder-white-maroon/main.jpg)  
(3, 3, 1, 2, 6, 5, 8, N'ADI-SAMBA-002', N'Adidas Samba OG Wonder White Maroon', N'Giày Adidas Samba OG với phối màu trắng và đỏ maroon sang trọng.', 1800000, 2590000, 1, 0, 0, 1),
-- 3. Samba OG Cloud White Wonder Quartz Wmns map với ảnh ID 12 (samba-og-cloud-white-wonder-quartz-wmns/main.jpg)
(3, 2, 1, 2, 6, 5, 12, N'ADI-SAMBA-003', N'Adidas Samba OG Cloud White Wonder Quartz Wmns', N'Phiên bản dành cho nữ với tone màu nhã nhặn và feminine.', 1800000, 2590000, 0, 1, 10, 1),
-- 4. Gazelle Bold Year of the Snake map với ảnh ID 16 (gazelle-bold-year-of-the-snake/main.jpg)
(3, 3, 1, 2, 4, 5, 16, N'ADI-GAZELLE-001', N'Adidas Gazelle Bold Year of the Snake', N'Giày Adidas Gazelle Bold phiên bản đặc biệt Year of the Snake.', 2200000, 3190000, 1, 0, 0, 1),
-- 5. Yeezy Boost 350 V2 Steel Grey map với ảnh ID 20 (yeezy-boost-350-v2-steel-grey/main.jpg)
(1, 3, 1, 2, 1, 7, 20, N'ADI-YEEZY-001', N'Adidas Yeezy Boost 350 V2 Steel Grey', N'Giày Adidas Yeezy Boost 350 V2 với colorway Steel Grey huyền thoại.', 4500000, 6490000, 1, 0, 0, 1),
-- 6. adiFOM Superstar Core Black map với ảnh ID 24 (adifom-superstar-core-black/main.jpg)
(1, 3, 1, 2, 1, 10, 24, N'ADI-ADIFOAM-001', N'Adidas adiFOM Superstar Core Black', N'Giày Adidas adiFOM Superstar với công nghệ foam mới.', 2000000, 2890000, 0, 1, 15, 1),
-- 7. adiFOM Superstar Core White map với ảnh ID 28 (adifom-superstar-core-white/main.jpg)
(1, 3, 1, 2, 1, 10, 28, N'ADI-ADIFOAM-002', N'Adidas adiFOM Superstar Core White', N'Phiên bản màu trắng của dòng adiFOM Superstar.', 2000000, 2890000, 0, 1, 15, 1),
-- 8. adiFOM Supernova Triple Black map với ảnh ID 32 (adifom-supernova-triple-black/main.jpg)
(1, 3, 1, 2, 1, 9, 32, N'ADI-ADIFOAM-003', N'Adidas adiFOM Supernova Triple Black', N'Giày Adidas adiFOM Supernova với colorway Triple Black.', 2200000, 3290000, 1, 0, 0, 1),
-- 9. UltraBoost 21 Cloud White map với ảnh ID 36 (th-thao-adidas-ultraboost-21-cloud-white/main.jpg)
(2, 1, 1, 2, 1, 7, 36, N'ADI-ULTRA-001', N'Adidas UltraBoost 21 Cloud White', N'Giày chạy bộ Adidas UltraBoost 21 với công nghệ Boost.', 3500000, 4990000, 1, 0, 0, 1),
-- 10. Run EQ21 Black map với ảnh ID 40 (run-eq21-black/main.jpg)
(2, 1, 1, 2, 1, 4, 40, N'ADI-RUN-001', N'Adidas Run EQ21 Black', N'Giày chạy bộ Adidas Run EQ21 màu đen.', 1200000, 1790000, 0, 1, 20, 1),
-- 11. Barricade 13 Tennis Black map với ảnh ID 46 (barricade-13-tennis-black/main.jpg)
(3, 1, 1, 2, 1, 3, 46, N'ADI-TENNIS-001', N'Adidas Barricade 13 Tennis Black', N'Giày tennis Adidas Barricade 13 chuyên nghiệp.', 2500000, 3590000, 0, 0, 0, 1),
-- 12. Adizero Ubersonic 4 Crystal White map với ảnh ID 50 (tennispickleball-adidas-adizero-ubersonic-4-crystal-white-semi-flash-aqua/main.jpg)
(3, 3, 1, 2, 1, 3, 50, N'ADI-TENNIS-002', N'Adidas Adizero Ubersonic 4 Crystal White', N'Giày tennis Adidas Adizero Ubersonic 4 lightweight.', 2800000, 3990000, 0, 1, 5, 1),
-- 13. Solematch Control 2 Semi Flash Aqua map với ảnh ID 54 (tennispickleball-adidas-solematch-control-2-semi-flash-aqua/main.jpg)
(3, 3, 1, 2, 1, 3, 54, N'ADI-TENNIS-003', N'Adidas Solematch Control 2 Semi Flash Aqua', N'Giày tennis Adidas Solematch Control 2 control.', 2600000, 3790000, 0, 0, 0, 1),
-- CONVERSE (14 sản phẩm) - Sửa lại mapping ảnh đại diện cho đúng thứ tự
-- 14. Aeon Active CX Himalayan Salt map với ảnh ID 58 (aeon-active-cx-himalayan-salt/main.jpg)
(4, 3, 2, 3, 1, 3, 58, N'CVS-AEON-001', N'Converse Aeon Active CX Himalayan Salt', N'Giày Converse Aeon Active CX với tone màu Himalayan Salt độc đáo.', 1300000, 1890000, 1, 0, 0, 1),
-- 15. Aeon Active CX OX Egret map với ảnh ID 62 (aeon-active-cx-ox-egret/main.jpg)
(4, 3, 2, 3, 1, 3, 62, N'CVS-AEON-002', N'Converse Aeon Active CX OX Egret', N'Giày Converse Aeon Active CX OX với công nghệ CX innovative.', 1300000, 1890000, 1, 0, 0, 1),
-- 16. Back To The Future x Converse All Star US MT Hi Black map với ảnh ID 66 (back-to-the-future-converse-all-star-us-mt-hi-black/main.jpg)
(10, 3, 2, 3, 2, 2, 66, N'CVS-BTTF-001', N'Back To The Future x Converse All Star US MT Hi Black', N'Collaboration Back To The Future x Converse với movie-inspired design.', 2200000, 3290000, 1, 0, 0, 1),
-- 17. Chuck Taylor All Star Cruise OX Black White map với ảnh ID 70 (chuck-taylor-all-star-cruise-ox-black-white/main.jpg)
(5, 3, 2, 3, 3, 2, 70, N'CVS-CHUCK-001', N'Converse Chuck Taylor All Star Cruise OX Black White', N'Giày Converse Chuck Taylor All Star Cruise OX với thiết kế thể thao hiện đại.', 1100000, 1590000, 1, 0, 0, 1),
-- 18. Chuck Taylor All Star Lift OX White Black map với ảnh ID 74 (chuck-taylor-all-star-lift-ox-white-black/main.jpg)
(8, 2, 2, 3, 4, 2, 74, N'CVS-LIFT-001', N'Converse Chuck Taylor All Star Lift OX White Black', N'Giày Converse Chuck Taylor All Star Lift OX với đế platform tăng chiều cao.', 1200000, 1790000, 1, 0, 0, 1),
-- 19. Chuck Taylor All Star Low Flame map với ảnh ID 78 (chuck-taylor-all-star-low-flame/main.webp)
(5, 3, 2, 3, 3, 2, 78, N'CVS-CHUCK-002', N'Converse Chuck Taylor All Star Low Flame', N'Giày Converse Chuck Taylor All Star Low với họa tiết flame độc đáo.', 1000000, 1490000, 1, 0, 0, 1),
-- 20. CDG Play x Chuck 70 Low Black White map với ảnh ID 82 (comme-des-gar-ons-play-x-chuck-70-low-black-white/main.webp)
(10, 3, 2, 3, 3, 2, 82, N'CVS-CDG-001', N'CDG Play x Converse Chuck 70 Low Black White', N'Phiên bản thấp cổ của collaboration CDG x Converse.', 2500000, 3690000, 1, 0, 0, 1),
-- 21. CDG x Converse Chuck Taylor All Star Hi Milk map với ảnh ID 86 (comme-des-gar-ons-x-chuck-taylor-all-star-hi-milk/main.webp)
(10, 3, 2, 3, 2, 2, 86, N'CVS-CDG-002', N'CDG x Converse Chuck Taylor All Star Hi Milk', N'CDG x Converse Chuck Taylor All Star Hi với colorway Milk độc đáo.', 2500000, 3690000, 1, 0, 0, 1),
-- 22. Kim Jones x Converse Chuck 70 All Star Black map với ảnh ID 90 (kim-jones-x-converse-chuck-70-all-star-black/main.jpg)
(10, 3, 2, 3, 2, 2, 90, N'CVS-KIM-001', N'Kim Jones x Converse Chuck 70 All Star Black', N'Collaboration giữa Kim Jones và Converse mang high fashion aesthetic.', 2800000, 3990000, 1, 0, 0, 1),
-- 23. Run Star Hike Low Black Gum map với ảnh ID 94 (run-star-hike-low-black-gum/main.jpg)
(7, 3, 2, 3, 5, 3, 94, N'CVS-RUNSTAR-001', N'Converse Run Star Hike Low Black Gum', N'Giày Converse Run Star Hike Low với thiết kế chunky revolutionary.', 1500000, 2190000, 1, 0, 0, 1),
-- 24. Run Star Motion Low White map với ảnh ID 98 (run-star-motion-low-white/main.jpg)
(7, 3, 2, 3, 5, 3, 98, N'CVS-RUNSTAR-002', N'Converse Run Star Motion Low White', N'Phiên bản trắng của dòng Run Star Motion với thiết kế hiện đại.', 1500000, 2190000, 1, 0, 0, 1),
-- 25. CDG Play x Chuck Taylor All Star 70 Hi Black map với ảnh ID 102 (Shoes-Comme-des-Garcons-Play-x-Converse-Chuck-Taylor-All-Star-70-Hi-Black-150204C/main.jpg)
(10, 3, 2, 3, 2, 2, 102, N'CVS-CDG-003', N'CDG Play x Converse Chuck Taylor All Star 70 Hi Black', N'Sản phẩm collaboration iconic giữa Comme des Garçons và Converse.', 2500000, 3690000, 1, 0, 0, 1),
-- 26. Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black map với ảnh ID 106 (Shoes-Converse-Chuck-Taylor-All-Star-EVA-Lift-Platform-Y2K-Heart-High-Top-Black-A09121C/main.jpg)
(5, 2, 2, 3, 2, 2, 106, N'CVS-EVA-001', N'Converse Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black', N'Giày Converse Chuck Taylor EVA Lift Platform với theme Y2K nostalgic.', 1400000, 2090000, 1, 0, 0, 1),
-- 27. Converse x Rick Owens DRKSHDW Weapon Beige Black map với ảnh ID 110 (x-rick-owens-drkshdw-weapon-beige-black/main.jpg)
(10, 3, 2, 3, 2, 3, 110, N'CVS-RICK-001', N'Converse x Rick Owens DRKSHDW Weapon Beige Black', N'Converse x Rick Owens DRKSHDW Weapon với avant-garde design philosophy.', 3200000, 4590000, 1, 0, 0, 1),
-- MLB (13 sản phẩm) - Sửa lại mapping ảnh đại diện cho đúng thứ tự
-- 28. MLB Bigball Chunky A New York Yankees map với ảnh ID 114 (bigball-chunky-a-new-york-yankees-3ashc101n/main.webp)
(7, 3, 3, 4, 5, 3, 114, N'MLB-BIGBALL-001', N'MLB Bigball Chunky A New York Yankees', N'MLB Bigball Chunky A phiên bản New York Yankees với design classic.', 1700000, 2490000, 1, 0, 0, 1),
-- 29. MLB BigBall Chunky Diamond Monogram Boston Red Sox D Beige map với ảnh ID 118 (bigball-chunky-diamond-monogram-boston-red-sox-d-beige-3ashcdm2n/main.webp)
(7, 3, 3, 4, 5, 3, 118, N'MLB-BIGBALL-002', N'MLB BigBall Chunky Diamond Monogram Boston Red Sox D Beige', N'Phiên bản Boston Red Sox với màu beige sophisticated.', 1800000, 2690000, 1, 0, 0, 1),
-- 30. MLB BigBall Chunky Diamond Monogram New York Yankees Black map với ảnh ID 122 (bigball-chunky-diamond-monogram-new-york-yankees-black-3ashcdm2n/main.webp)
(7, 3, 3, 4, 5, 3, 122, N'MLB-BIGBALL-003', N'MLB BigBall Chunky Diamond Monogram New York Yankees Black', N'Giày MLB BigBall Chunky với họa tiết Diamond Monogram luxury.', 1800000, 2690000, 1, 0, 0, 1),
-- 31. MLB BigBall Chunky P Boston Red Sox map với ảnh ID 126 (bigball-chunky-p-boston-red-sox-32shc2111-43i/main.webp)
(7, 3, 3, 4, 5, 3, 126, N'MLB-BIGBALL-004', N'MLB BigBall Chunky P Boston Red Sox', N'MLB BigBall Chunky P Boston Red Sox với performance-inspired design.', 1700000, 2490000, 1, 0, 0, 1),
-- 32. MLB Chunky Liner Low Boston Red Sox Beige map với ảnh ID 130 (chunky-liner-low-boston-red-sox-beige-3asxca12n/main.webp)
(7, 3, 3, 4, 5, 3, 130, N'MLB-CHUNKY-001', N'MLB Chunky Liner Low Boston Red Sox Beige', N'Phiên bản thấp cổ của dòng Chunky Liner với Red Sox branding.', 1600000, 2390000, 1, 0, 0, 1),
-- 33. MLB Chunky Liner Mid Denim Boston Red Sox D Blue map với ảnh ID 134 (chunky-liner-mid-denim-boston-red-sox-d-blue-3asxcdn3n/main.webp)
(7, 3, 3, 4, 5, 11, 134, N'MLB-CHUNKY-002', N'MLB Chunky Liner Mid Denim Boston Red Sox D Blue', N'MLB Chunky Liner Mid với chất liệu denim unique.', 1700000, 2590000, 1, 0, 0, 1),
-- 34. MLB Chunky Liner Mid Denim New York Yankees D Navy map với ảnh ID 138 (chunky-liner-mid-denim-new-york-yankees-d-navy-3asxcdn3n/main.webp)
(7, 3, 3, 4, 5, 11, 138, N'MLB-CHUNKY-003', N'MLB Chunky Liner Mid Denim New York Yankees D Navy', N'Phiên bản denim New York Yankees với D.Navy sophisticated.', 1700000, 2590000, 1, 0, 0, 1),
-- 35. MLB Chunky Liner New York Yankees Grey map với ảnh ID 142 (chunky-liner-new-york-yankees-grey-3asxca12n/main.webp)
(7, 3, 3, 4, 5, 3, 142, N'MLB-CHUNKY-004', N'MLB Chunky Liner New York Yankees Grey', N'Giày MLB Chunky Liner của đội New York Yankees với màu xám neutral.', 1600000, 2390000, 1, 0, 0, 1),
-- 36. MLB Chunky Liner SL Saffiano Boston Red Sox map với ảnh ID 146 (chunky-liner-sl-saffiano-boston-red-sox-3asxcls4n/main.webp)
(7, 3, 3, 4, 5, 12, 146, N'MLB-CHUNKY-005', N'MLB Chunky Liner SL Saffiano Boston Red Sox', N'MLB Chunky Liner SL với chất liệu Saffiano leather cao cấp.', 1900000, 2890000, 1, 0, 0, 1),
-- 37. MLB Liner Basic New York Yankees Black map với ảnh ID 150 (liner-basic-new-york-yankees-black-3asxclb3n/main.webp)
(4, 1, 3, 4, 3, 2, 150, N'MLB-LINER-001', N'MLB Liner Basic New York Yankees Black', N'Giày MLB Liner Basic của đội New York Yankees.', 1200000, 1790000, 1, 0, 0, 1),
-- 38. MLB Liner Basic New York Yankees Green map với ảnh ID 154 (liner-basic-new-york-yankees-green-3asxclb3n/main.webp)
(4, 1, 3, 4, 3, 2, 154, N'MLB-LINER-002', N'MLB Liner Basic New York Yankees Green', N'Phiên bản màu xanh unique của dòng Liner Basic.', 1200000, 1790000, 1, 0, 0, 1),
-- 39. MLB Playball Mule Dia Monogram New York Yankees Beige map với ảnh ID 158 (playball-mule-dia-monogram-new-york-yankees-beige-3amumda2n/main.webp)
(9, 3, 3, 4, 8, 5, 158, N'MLB-MULE-001', N'MLB Playball Mule Dia Monogram New York Yankees Beige', N'Giày MLB Playball Mule với Diamond Monogram luxury pattern.', 1500000, 2290000, 1, 0, 0, 1),
-- 40. MLB Playball Mule Mono NY New York Yankees map với ảnh ID 162 (playball-mule-mono-ny-new-york-yankees-32shsm111-50l/main.webp)
(9, 3, 3, 4, 8, 3, 162, N'MLB-MULE-002', N'MLB Playball Mule Mono NY New York Yankees', N'MLB Playball Mule với Monogram NY design clean và minimal.', 1400000, 2090000, 1, 0, 0, 1);
GO
INSERT INTO SanPhamChiTiet (id_san_pham, id_kich_co, id_mau_sac, so_luong, trang_thai) VALUES 
-- Adidas Samba OG White Black Gum (ID: 1) - Màu: White(2), Black(1), Gum(7)
(1, 2, 2, 15, 1), (1, 3, 2, 20, 1), (1, 4, 2, 25, 1), (1, 5, 2, 30, 1), (1, 6, 2, 28, 1), (1, 7, 2, 22, 1), (1, 8, 2, 18, 1), (1, 9, 2, 12, 1),
(1, 2, 8, 12, 1), (1, 3, 8, 18, 1), (1, 4, 8, 22, 1), (1, 5, 8, 28, 1), (1, 6, 8, 25, 1), (1, 7, 8, 20, 1), (1, 8, 8, 15, 1), (1, 9, 8, 10, 1),
(1, 2, 16, 8, 1), (1, 3, 16, 12, 1), (1, 4, 16, 15, 1), (1, 5, 16, 18, 1), (1, 6, 16, 15, 1), (1, 7, 16, 12, 1), (1, 8, 16, 8, 1), (1, 9, 16, 5, 1),

-- Adidas Samba OG Wonder White Maroon (ID: 2) - Màu: White(2), Maroon(16)
(2, 2, 2, 12, 1), (2, 3, 2, 18, 1), (2, 4, 2, 22, 1), (2, 5, 2, 25, 1), (2, 6, 2, 20, 1), (2, 7, 2, 15, 1), (2, 8, 2, 10, 1),
(2, 2, 16, 10, 1), (2, 3, 16, 15, 1), (2, 4, 16, 18, 1), (2, 5, 16, 20, 1), (2, 6, 16, 15, 1), (2, 7, 16, 12, 1), (2, 8, 16, 8, 1),

-- Adidas Samba OG Cloud White Wonder Quartz Wmns (ID: 3) - Màu: Cloud White(2), Wonder Quartz(8)
(3, 1, 2, 8, 1), (3, 2, 2, 15, 1), (3, 3, 2, 20, 1), (3, 4, 2, 18, 1), (3, 5, 2, 12, 1), (3, 6, 2, 8, 1),
(3, 1, 8, 6, 1), (3, 2, 8, 12, 1), (3, 3, 8, 15, 1), (3, 4, 8, 12, 1), (3, 5, 8, 8, 1), (3, 6, 8, 5, 1),

-- Adidas Gazelle Bold Year of the Snake (ID: 4) - Màu: Snake Pattern(17)
(4, 3, 17, 10, 1), (4, 4, 17, 15, 1), (4, 5, 17, 18, 1), (4, 6, 17, 20, 1), (4, 7, 17, 15, 1), (4, 8, 17, 12, 1),

-- Adidas Yeezy Boost 350 V2 Steel Grey (ID: 5) - Màu: Steel Grey(3)
(5, 3, 3, 5, 1), (5, 4, 3, 8, 1), (5, 5, 3, 10, 1), (5, 6, 3, 12, 1), (5, 7, 3, 8, 1), (5, 8, 3, 5, 1),

-- Adidas adiFOM Superstar Core Black (ID: 6) - Màu: Core Black(1)
(6, 3, 1, 12, 1), (6, 4, 1, 18, 1), (6, 5, 1, 22, 1), (6, 6, 1, 25, 1), (6, 7, 1, 20, 1), (6, 8, 1, 15, 1),

-- Adidas adiFOM Superstar Core White (ID: 7) - Màu: Core White(2)
(7, 3, 2, 15, 1), (7, 4, 2, 20, 1), (7, 5, 2, 25, 1), (7, 6, 2, 28, 1), (7, 7, 2, 22, 1), (7, 8, 2, 18, 1),

-- Adidas adiFOM Supernova Triple Black (ID: 8) - Màu: Triple Black(1)
(8, 4, 1, 20, 1), (8, 5, 1, 25, 1), (8, 6, 1, 30, 1), (8, 7, 1, 28, 1), (8, 8, 1, 22, 1), (8, 9, 1, 18, 1),

-- Adidas UltraBoost 21 Cloud White (ID: 9) - Màu: Cloud White(2)
(9, 4, 2, 8, 1), (9, 5, 2, 12, 1), (9, 6, 2, 15, 1), (9, 7, 2, 18, 1), (9, 8, 2, 15, 1), (9, 9, 2, 10, 1),

-- Adidas Run EQ21 Black (ID: 10) - Màu: Black(1)
(10, 4, 1, 20, 1), (10, 5, 1, 25, 1), (10, 6, 1, 30, 1), (10, 7, 1, 28, 1), (10, 8, 1, 22, 1), (10, 9, 1, 18, 1),

-- Adidas Barricade 13 Tennis Black (ID: 11) - Màu: Black(1)
(11, 4, 1, 8, 1), (11, 5, 1, 12, 1), (11, 6, 1, 15, 1), (11, 7, 1, 18, 1), (11, 8, 1, 15, 1), (11, 9, 1, 10, 1),

-- Adidas Adizero Ubersonic 4 Crystal White (ID: 12) - Màu: Crystal White(2), Semi Flash Aqua(18)
(12, 4, 2, 10, 1), (12, 5, 2, 15, 1), (12, 6, 2, 18, 1), (12, 7, 2, 20, 1), (12, 8, 2, 15, 1), (12, 9, 2, 12, 1),
(12, 4, 18, 8, 1), (12, 5, 18, 12, 1), (12, 6, 18, 15, 1), (12, 7, 18, 18, 1), (12, 8, 18, 12, 1), (12, 9, 18, 8, 1),

-- Adidas Solematch Control 2 Semi Flash Aqua (ID: 13) - Màu: Semi Flash Aqua(18)
(13, 4, 18, 12, 1), (13, 5, 18, 18, 1), (13, 6, 18, 20, 1), (13, 7, 18, 18, 1), (13, 8, 18, 15, 1), (13, 9, 18, 10, 1),

-- Converse Aeon Active CX Himalayan Salt (ID: 14) - Màu: Himalayan Salt(13)
(14, 3, 3, 15, 1), (14, 4, 3, 20, 1), (14, 5, 3, 25, 1), (14, 6, 3, 22, 1), (14, 7, 3, 18, 1), (14, 8, 3, 12, 1),

-- Converse Aeon Active CX OX Egret (ID: 15) - Màu: Egret(13)
(15, 3, 13, 12, 1), (15, 4, 13, 18, 1), (15, 5, 13, 20, 1), (15, 6, 13, 15, 1), (15, 7, 13, 10, 1),

-- Back To The Future x Converse All Star US MT Hi Black (ID: 16) - Màu: Black(1)
(16, 4, 1, 20, 1), (16, 5, 1, 25, 1), (16, 6, 1, 22, 1), (16, 7, 1, 18, 1), (16, 8, 1, 15, 1),

-- Converse Chuck Taylor All Star Cruise OX Black White (ID: 17) - Màu: Black(1), White(2)
(17, 4, 1, 15, 1), (17, 5, 1, 20, 1), (17, 6, 1, 18, 1), (17, 7, 1, 15, 1), (17, 8, 1, 12, 1),
(17, 4, 2, 12, 1), (17, 5, 2, 18, 1), (17, 6, 2, 15, 1), (17, 7, 2, 12, 1), (17, 8, 2, 8, 1),

-- Converse Chuck Taylor All Star Lift OX White Black (ID: 18) - Màu: White(2), Black(1)
(18, 2, 2, 10, 1), (18, 3, 2, 15, 1), (18, 4, 2, 20, 1), (18, 5, 2, 18, 1), (18, 6, 2, 12, 1), (18, 7, 2, 8, 1),
(18, 2, 1, 8, 1), (18, 3, 1, 12, 1), (18, 4, 1, 15, 1), (18, 5, 1, 12, 1), (18, 6, 1, 8, 1), (18, 7, 1, 5, 1),

-- Converse Chuck Taylor All Star Low Flame (ID: 19) - Màu: Flame(12)
(19, 3, 1, 15, 1), (19, 4, 1, 20, 1), (19, 5, 1, 25, 1), (19, 6, 1, 22, 1), (19, 7, 1, 18, 1), (19, 8, 1, 12, 1),

-- CDG Play x Chuck 70 Low Black White (ID: 20) - Màu: Black(1), White(2)
(20, 3, 1, 8, 1), (20, 4, 1, 12, 1), (20, 5, 1, 15, 1), (20, 6, 1, 18, 1), (20, 7, 1, 15, 1), (20, 8, 1, 10, 1),
(20, 3, 2, 6, 1), (20, 4, 2, 10, 1), (20, 5, 2, 12, 1), (20, 6, 2, 15, 1), (20, 7, 2, 12, 1), (20, 8, 2, 8, 1),

-- CDG x Converse Chuck Taylor All Star Hi Milk (ID: 21) - Màu: Mlk White(19)
(21, 3, 2, 8, 1), (21, 4, 2, 12, 1), (21, 5, 2, 15, 1), (21, 6, 2, 18, 1), (21, 7, 2, 15, 1), (21, 8, 2, 10, 1),

-- Kim Jones x Converse Chuck 70 All Star Black (ID: 22) - Màu: Black(1)
(22, 3, 1, 6, 1), (22, 4, 1, 10, 1), (22, 5, 1, 12, 1), (22, 6, 1, 15, 1), (22, 7, 1, 12, 1), (22, 8, 1, 8, 1),

-- Converse Run Star Hike Low Black Gum (ID: 23) - Màu: Black(1), Gum(7)
(23, 3, 1, 12, 1), (23, 4, 1, 18, 1), (23, 5, 1, 22, 1), (23, 6, 1, 25, 1), (23, 7, 1, 20, 1), (23, 8, 1, 15, 1),

-- Converse Run Star Motion Low White (ID: 24) - Màu: White(2)
(24, 3, 2, 18, 1), (24, 4, 2, 25, 1), (24, 5, 2, 28, 1), (24, 6, 2, 22, 1), (24, 7, 2, 18, 1), (24, 8, 2, 12, 1),

-- CDG Play x Chuck Taylor All Star 70 Hi Black (ID: 25) - Màu: Black(1)
(25, 3, 1, 8, 1), (25, 4, 1, 12, 1), (25, 5, 1, 15, 1), (25, 6, 1, 18, 1), (25, 7, 1, 15, 1), (25, 8, 1, 10, 1),

-- Chuck Taylor All Star EVA Lift Platform Y2K Heart High Top Black (ID: 26) - Màu: Black(1)
(26, 3, 1, 12, 1), (26, 4, 1, 18, 1), (26, 5, 1, 22, 1), (26, 6, 1, 25, 1), (26, 7, 1, 20, 1), (26, 8, 1, 15, 1),

-- Converse x Rick Owens DRKSHDW Weapon Beige Black (ID: 27) - Màu: Beige(13), Black(1)
(27, 4, 3, 5, 1), (27, 5, 3, 8, 1), (27, 6, 3, 10, 1), (27, 7, 3, 12, 1), (27, 8, 3, 10, 1), (27, 9, 3, 6, 1),

-- MLB Bigball Chunky A New York Yankees (ID: 28) - Màu: Yankees colors (Navy(4), White(2))
(28, 4, 2, 8, 1), (28, 5, 2, 12, 1), (28, 6, 2, 15, 1), (28, 7, 2, 18, 1), (28, 8, 2, 12, 1), (28, 9, 2, 10, 1),

-- MLB BigBall Chunky Diamond Monogram Boston Red Sox D Beige (ID: 29) - Màu: Beige(13), Red Sox Red(5)
(29, 4, 13, 12, 1), (29, 5, 13, 18, 1), (29, 6, 13, 20, 1), (29, 7, 13, 18, 1), (29, 8, 13, 15, 1),

-- MLB BigBall Chunky Diamond Monogram New York Yankees Black (ID: 30) - Màu: Black(1), Navy(4)
(30, 4, 2, 15, 1), (30, 5, 2, 20, 1), (30, 6, 2, 22, 1), (30, 7, 2, 18, 1), (30, 8, 2, 15, 1),


-- MLB BigBall Chunky P Boston Red Sox (ID: 31) - Màu: Red(5), Beige(13)
(31, 4, 2, 18, 1), (31, 5, 2, 22, 1), (31, 6, 2, 25, 1), (31, 7, 2, 20, 1), (31, 8, 2, 15, 1),

-- MLB Chunky Liner Low Boston Red Sox Beige (ID: 32) - Màu: Beige(13), Red(5)
(32, 4, 13, 15, 1), (32, 5, 13, 20, 1), (32, 6, 13, 22, 1), (32, 7, 13, 18, 1), (32, 8, 13, 15, 1),

-- MLB Chunky Liner Mid Denim Boston Red Sox D Blue (ID: 33) - Màu: D.Blue(21), Red(5)
(33, 4, 21, 12, 1), (33, 5, 21, 18, 1), (33, 6, 21, 20, 1), (33, 7, 21, 18, 1), (33, 8, 21, 15, 1),
(33, 4, 20, 10, 1), (33, 5, 20, 15, 1), (33, 6, 20, 18, 1), (33, 7, 20, 15, 1), (33, 8, 20, 12, 1),

-- MLB Chunky Liner Mid Denim New York Yankees D Navy (ID: 34) - Màu: D.Navy(20), Navy(4)
(34, 4, 20, 15, 1), (34, 5, 20, 20, 1), (34, 6, 20, 22, 1), (34, 7, 20, 18, 1), (34, 8, 20, 15, 1),
(34, 4, 21, 12, 1), (34, 5, 21, 18, 1), (34, 6, 21, 20, 1), (34, 7, 21, 15, 1), (34, 8, 21, 12, 1),

-- MLB Chunky Liner New York Yankees Grey (ID: 35) - Màu: Grey(3), Navy(4)
(35, 4, 3, 15, 1), (35, 5, 3, 20, 1), (35, 6, 3, 22, 1), (35, 7, 3, 18, 1), (35, 8, 3, 15, 1),


-- MLB Chunky Liner SL Saffiano Boston Red Sox (ID: 36) - Màu: Red(5), Brown(7)

(36, 4, 7, 8, 1), (36, 5, 7, 12, 1), (36, 6, 7, 15, 1), (36, 7, 7, 12, 1), (36, 8, 7, 10, 1),

-- MLB Liner Basic New York Yankees Black (ID: 37) - Màu: Black(1), Navy(4)
(37, 4, 1, 25, 1), (37, 5, 1, 30, 1), (37, 6, 1, 28, 1), (37, 7, 1, 25, 1), (37, 8, 1, 20, 1), (37, 9, 1, 15, 1),
(37, 4, 10, 20, 1), (37, 5, 10, 25, 1), (37, 6, 10, 22, 1), (37, 7, 10, 20, 1), (37, 8, 10, 15, 1), (37, 9, 10, 12, 1),

-- MLB Liner Basic New York Yankees Green (ID: 38) - Màu: Green(10), Navy(4)
(38, 4, 10, 20, 1), (38, 5, 10, 25, 1), (38, 6, 10, 22, 1), (38, 7, 10, 18, 1), (38, 8, 10, 15, 1),
(38, 4, 1, 15, 1), (38, 5, 1, 20, 1), (38, 6, 1, 18, 1), (38, 7, 1, 15, 1), (38, 8, 1, 12, 1),

-- MLB Playball Mule Dia Monogram New York Yankees Beige (ID: 39) - Màu: Beige(13), Navy(4)
(39, 4, 13, 12, 1), (39, 5, 13, 18, 1), (39, 6, 13, 20, 1), (39, 7, 13, 15, 1), (39, 8, 13, 10, 1),
(39, 4, 1, 10, 1), (39, 5, 1, 15, 1), (39, 6, 1, 18, 1), (39, 7, 1, 12, 1), (39, 8, 1, 8, 1),

-- MLB Playball Mule Mono NY New York Yankees (ID: 40) - Màu: Grey(3), Navy(4)
(40, 4, 13, 15, 1), (40, 5, 13, 20, 1), (40, 6, 13, 18, 1), (40, 7, 13, 15, 1), (40, 8, 13, 12, 1),
(40, 4, 1, 12, 1), (40, 5, 1, 18, 1), (40, 6, 1, 15, 1), (40, 7, 1, 12, 1), (40, 8, 1, 10, 1);
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
-- Converse Aeon Active CX Himalayan Salt (ID: 14) - 6 biến thể (ID: 82-87)
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
-- DỮ LIỆU MẪU CHO GIỎ HÀNG VÀ TÀI KHOẢN
-- =============================================

INSERT INTO TaiKhoan (tai_khoan, mat_khau, email, ho_ten_tai_khoan, role) VALUES
(N'admin', N'123', N'admin@urbansteps.com', N'Administrator', N'ADMIN'),
(N'user1', N'123', N'user1@gmail.com', N'Nguyễn Văn A', N'USER'),
(N'user2', N'123', N'user2@gmail.com', N'Trần Thị B', N'USER'),
(N'user3', N'123', N'user3@gmail.com', N'Lê Văn C', N'USER');
GO
INSERT INTO GioHang (id_tai_khoan, session_id, create_at, update_at) VALUES
-- Giỏ hàng cho user1 (id_tai_khoan = 2)
(2, NULL, GETDATE(), GETDATE()),
-- Giỏ hàng cho user2 (id_tai_khoan = 3)
(3, NULL, GETDATE(), GETDATE()),
-- Giỏ hàng cho user3 (id_tai_khoan = 4)
(4, NULL, GETDATE(), GETDATE()),
-- Giỏ hàng cho khách (chưa đăng nhập) - sử dụng session_id
(NULL, 'GUEST_SESSION_001', GETDATE(), GETDATE()),
(NULL, 'GUEST_SESSION_002', GETDATE(), GETDATE());
GO

-- Giỏ hàng mẫu cho các tài khoản
INSERT INTO GioHangItem (id_gio_hang, id_san_pham_chi_tiet, so_luong, gia_tai_thoi_diem, create_at, update_at) VALUES
-- Giỏ hàng của user1 (GioHang ID: 1)
(1, 1, 2, 2590000, GETDATE(), GETDATE()),   -- Adidas Samba OG White Black Gum - Size 36 - 2 đôi
(1, 10, 1, 2590000, GETDATE(), GETDATE()),  -- Adidas Samba OG Wonder White Maroon - Size 37 - 1 đôi
(1, 22, 1, 3190000, GETDATE(), GETDATE()),  -- Adidas Gazelle Bold - Size 35 - 1 đôi

-- Giỏ hàng của user2 (GioHang ID: 2)
(2, 28, 2, 6490000, GETDATE(), GETDATE()),  -- Yeezy Boost 350 V2 - Size 35 - 2 đôi
(2, 82, 1, 1890000, GETDATE(), GETDATE()),  -- Converse Aeon Active - Size 35 - 1 đôi
(2, 112, 3, 2490000, GETDATE(), GETDATE()), -- MLB Bigball Chunky - Size 35 - 3 đôi

-- Giỏ hàng của user3 (GioHang ID: 3)
(3, 40, 1, 2890000, GETDATE(), GETDATE()), -- Adidas adiFOM Superstar Core White - Size 35 - 1 đôi
(3, 70, 2, 3990000, GETDATE(), GETDATE()), -- Adidas Adizero Ubersonic - Size 35 - 2 đôi

-- Giỏ hàng của khách (chưa đăng nhập) - GUEST_SESSION_001 (GioHang ID: 4)
(4, 46, 1, 3290000, GETDATE(), GETDATE()),  -- Adidas adiFOM Supernova - Size 35 - 1 đôi
(4, 95, 2, 1590000, GETDATE(), GETDATE()),  -- Converse Chuck Taylor Cruise - Size 36 - 2 đôi

-- Giỏ hàng của khách (chưa đăng nhập) - GUEST_SESSION_002 (GioHang ID: 5)
(5, 148, 1, 2390000, GETDATE(), GETDATE()),  -- MLB Chunky Liner Low - Size 35 - 1 đôi
(5, 175, 2, 2290000, GETDATE(), GETDATE());  -- MLB Playball Mule - Size 36 - 2 đôi
GO


