// Live search sản phẩm, hiện dropdown
function liveSearchProducts() {
    const keyword = document.getElementById('search').value.trim();
    const dropdown = document.getElementById('search-dropdown');
    if (!keyword) {
        dropdown.style.display = 'none';
        dropdown.innerHTML = '';
        return;
    }
    fetch(`/pos/products?keyword=${encodeURIComponent(keyword)}`)
        .then(res => res.json())
        .then(data => {
            if (!data || data.length === 0) {
                dropdown.style.display = 'none';
                dropdown.innerHTML = '';
                return;
            }
            dropdown.innerHTML = data.map(sp =>
                `<div class='search-item' onclick='selectSearchProduct("${sp.tenSanPham}")'>
                    <b>${sp.tenSanPham}</b> <span style='color:#1976d2;'>${sp.giaBan} VNĐ</span>
                </div>`
            ).join('');
            dropdown.style.display = 'block';
        });
}

function selectSearchProduct(name) {
    document.getElementById('search').value = name;
    document.getElementById('search-dropdown').style.display = 'none';
    searchProducts();
}
// Đóng dropdown khi click ngoài
document.addEventListener('click', function(e) {
    const dropdown = document.getElementById('search-dropdown');
    if (dropdown && !dropdown.contains(e.target) && e.target.id !== 'search') {
        dropdown.style.display = 'none';
    }
});
// pos.js - Xử lý logic POS frontend
let cart = [];

function searchProducts() {
    const keyword = document.getElementById('search').value.trim();
    if (!keyword) return;
    fetch(`/pos/products?keyword=${encodeURIComponent(keyword)}`)
        .then(res => res.json())
        .then(data => {
            const resultsDiv = document.getElementById('search-results');
            resultsDiv.innerHTML = '';
            if (data.length === 0) {
                resultsDiv.innerHTML = '<em>Không tìm thấy sản phẩm.</em>';
                return;
            }
            data.forEach(sp => {
                const div = document.createElement('div');
                div.innerHTML = `<b>${sp.tenSanPham}</b> - ${sp.giaBan} VNĐ
                    <input type='number' min='1' value='1' id='qty_${sp.id}' style='width:60px;'>
                    <button onclick='addToCart(${sp.id}, "${sp.tenSanPham}", ${sp.giaBan})'>Thêm</button>`;
                resultsDiv.appendChild(div);
            });
        });
}

function addToCart(id, tenSanPham, giaBan) {
    const qty = parseInt(document.getElementById(`qty_${id}`).value);
    if (!qty || qty < 1) return;
    // Nếu đã có trong giỏ thì cộng dồn
    const idx = cart.findIndex(item => item.sanPhamChiTietId === id);
    if (idx >= 0) {
        cart[idx].soLuong += qty;
    } else {
        cart.push({ sanPhamChiTietId: id, tenSanPham, giaTaiThoidiem: giaBan, soLuong: qty });
    }
    renderCart();
}

function renderCart() {
    const tbody = document.querySelector('#cart-table tbody');
    tbody.innerHTML = '';
    let total = 0;
    cart.forEach((item, i) => {
        const thanhTien = item.giaTaiThoidiem * item.soLuong;
        total += thanhTien;
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${item.tenSanPham}</td>
            <td>${item.giaTaiThoidiem}</td>
            <td>${item.soLuong}</td>
            <td>${thanhTien}</td>
            <td><button onclick='removeCart(${i})'>Xóa</button></td>`;
        tbody.appendChild(tr);
    });
    document.getElementById('cart-total').innerText = total;
}

function removeCart(idx) {
    cart.splice(idx, 1);
    renderCart();
}

function submitOrder(e) {
    e.preventDefault();
    if (cart.length === 0) {
        alert('Vui lòng chọn sản phẩm!');
        return;
    }
    const form = document.getElementById('pos-form');
    const data = {
        hoTen: form.hoTen.value,
        sdt: form.sdt.value,
        ghiChu: form.ghiChu.value,
        tienMat: parseInt(form.tienMat.value) || 0,
        tienChuyenKhoan: parseInt(form.tienChuyenKhoan.value) || 0,
        phuongThucThanhToan: parseInt(form.phuongThucThanhToan.value),
        products: cart.map(item => ({
            sanPhamChiTietId: item.sanPhamChiTietId,
            soLuong: item.soLuong,
            giaTaiThoidiem: item.giaTaiThoidiem
        }))
    };
    fetch('/pos/order', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(order => {
        document.getElementById('order-result').innerHTML = `<b>Đã tạo đơn thành công!</b> Mã đơn: ${order.maHoaDon}`;
        cart = [];
        renderCart();
        form.reset();
    })
    .catch(() => {
        document.getElementById('order-result').innerHTML = '<span style="color:red">Lỗi tạo đơn!</span>';
    });
}
