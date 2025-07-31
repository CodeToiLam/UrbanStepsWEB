document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/thong-ke')
        .then(res => res.json())
        .then(data => {
            document.getElementById('tongSanPham').textContent = data.tongSanPham;
            document.getElementById('tongDonHang').textContent = data.tongDonHang;
            document.getElementById('tongDoanhThu').textContent = data.tongDoanhThu.toLocaleString('vi-VN') + ' ₫';
            document.getElementById('tongKhachHang').textContent = data.tongKhachHang;
            let topBody = document.getElementById('topSanPhamBanChay');
            topBody.innerHTML = '';
            data.topSanPhamBanChay.forEach(sp => {
                let row = `<tr><td>${sp.tenSanPham}</td><td>${sp.soLuongBan}</td></tr>`;
                topBody.innerHTML += row;
            });
        });
});
