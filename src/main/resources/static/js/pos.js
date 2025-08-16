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
                        const f = new Intl.NumberFormat('vi-VN');
                        dropdown.innerHTML = data.map(sp => {
                                const img = sp.image || '/images/no-image.svg';
                                const brand = sp.brand ? `<div class="sug-sub">${sp.brand}</div>` : '';
                                return `
                                <div class='search-item' onclick='selectSearchProduct(${sp.id}, "${sp.tenSanPham.replace(/"/g,'&quot;')}", ${sp.giaBan || 0})'>
                                    <img class="sug-img" src="${img}" alt="" onerror="this.src='/images/no-image.svg'"/>
                                    <div class="sug-meta">
                                        <div class="sug-name">${sp.tenSanPham}</div>
                                        ${brand}
                                        <div class="sug-price">${f.format(sp.giaBan || 0)} đ</div>
                                    </div>
                                </div>`;
                        }).join('');
            dropdown.style.display = 'block';
        });
}

function selectSearchProduct(variantId, name, price) {
        document.getElementById('search').value = name;
        document.getElementById('search-dropdown').style.display = 'none';
        const resultsDiv = document.getElementById('search-results');
        resultsDiv.innerHTML = `<div class="pos-result-item">
                <div class="sug-meta">
                    <div class="sug-name">${name}</div>
                    <div class="sug-price">${new Intl.NumberFormat('vi-VN').format(price||0)} đ</div>
                </div>
                <div class="sug-action">
                    <input type='number' min='1' value='1' id='qty_${variantId}' style='width:70px;'>
                    <button class='pos-btn' onclick='addToCart(${variantId}, "${name.replace(/"/g,'&quot;')}", ${price||0})'>Thêm</button>
                </div>
            </div>`;
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
                        const f = new Intl.NumberFormat('vi-VN');
                        data.forEach(sp => {
                                const div = document.createElement('div');
                                div.className = 'pos-result-item';
                                const img = sp.image || '/images/no-image.svg';
                                div.innerHTML = `
                                        <img class="sug-img" src="${img}" alt="" onerror="this.src='/images/no-image.svg'"/>
                                        <div class="sug-meta">
                                            <div class="sug-name">${sp.tenSanPham}</div>
                                            ${sp.brand ? `<div class='sug-sub'>${sp.brand}</div>` : ''}
                                            <div class="sug-price">${f.format(sp.giaBan||0)} đ</div>
                                        </div>
                                        <div class="sug-action">
                                            <input type='number' min='1' value='1' id='qty_${sp.id}' style='width:70px;'>
                                            <button class='pos-btn' onclick='addToCart(${sp.id}, "${sp.tenSanPham.replace(/"/g,'&quot;')}", ${sp.giaBan||0})'>Thêm</button>
                                        </div>`;
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
        <td>${new Intl.NumberFormat('vi-VN').format(item.giaTaiThoidiem||0)}</td>
            <td>${item.soLuong}</td>
        <td>${new Intl.NumberFormat('vi-VN').format(thanhTien||0)}</td>
            <td><button onclick='removeCart(${i})'>Xóa</button></td>`;
        tbody.appendChild(tr);
    });
    document.getElementById('cart-total').innerText = new Intl.NumberFormat('vi-VN').format(total||0);
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
