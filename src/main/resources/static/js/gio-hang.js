// Cập nhật số lượng sản phẩm
function updateQuantity(itemId, newQuantity) {
    // Kiểm tra số lượng hợp lệ
    if (newQuantity < 1) {
        if (confirm('Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?')) {
            removeItem(itemId);
        }
        return;
    }

    // Hiển thị loading
    showLoading();

    // Gửi request cập nhật
    fetch('/api/cart/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            itemId: itemId,
            quantity: newQuantity
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Cập nhật giao diện
            updateCartDisplay();
            showMessage('Cập nhật số lượng thành công!', 'success');
        } else {
            showMessage(data.message || 'Có lỗi xảy ra khi cập nhật số lượng!', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('Có lỗi xảy ra khi cập nhật số lượng!', 'error');
    })
    .finally(() => {
        hideLoading();
    });
}

// Xóa sản phẩm khỏi giỏ hàng
function removeItem(itemId) {
    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }

    // Hiển thị loading
    showLoading();

    // Gửi request xóa
    fetch('/api/cart/remove', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            itemId: itemId
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Reload trang để cập nhật giao diện
            location.reload();
            showMessage('Xóa sản phẩm thành công!', 'success');
        } else {
            showMessage(data.message || 'Có lỗi xảy ra khi xóa sản phẩm!', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('Có lỗi xảy ra khi xóa sản phẩm!', 'error');
    })
    .finally(() => {
        hideLoading();
    });
}

// Cập nhật hiển thị giỏ hàng
function updateCartDisplay() {
    fetch('/api/cart/info')
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Cập nhật tổng tiền
            document.querySelectorAll('.total-amount').forEach(element => {
                element.textContent = formatCurrency(data.totalAmount);
            });
            
            // Cập nhật số lượng items
            document.querySelectorAll('.item-count').forEach(element => {
                element.textContent = data.itemCount;
            });
        }
    })
    .catch(error => {
        console.error('Error updating cart display:', error);
    });
}

// Áp dụng mã khuyến mãi
function applyPromoCode() {
    const promoCode = document.querySelector('.promo-section input').value.trim();
    
    if (!promoCode) {
        showMessage('Vui lòng nhập mã khuyến mãi!', 'error');
        return;
    }

    // Hiển thị loading
    showLoading();

    // Gửi request áp dụng mã
    fetch('/api/cart/apply-promo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            promoCode: promoCode
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Reload trang để cập nhật giá
            location.reload();
            showMessage('Áp dụng mã khuyến mãi thành công!', 'success');
        } else {
            showMessage(data.message || 'Mã khuyến mãi không hợp lệ!', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('Có lỗi xảy ra khi áp dụng mã khuyến mãi!', 'error');
    })
    .finally(() => {
        hideLoading();
    });
}

// Thanh toán
function checkout() {
    // Hiển thị loading
    showLoading();

    // Kiểm tra giỏ hàng có sản phẩm không
    const cartItems = document.querySelectorAll('.cart-item');
    if (cartItems.length === 0) {
        showMessage('Giỏ hàng của bạn đang trống!', 'error');
        hideLoading();
        return;
    }

    // Chuyển đến trang thanh toán
    window.location.href = '/checkout';
}

// Tiện ích - Hiển thị loading
function showLoading() {
    // Tạo overlay loading nếu chưa có
    if (!document.querySelector('.loading-overlay')) {
        const overlay = document.createElement('div');
        overlay.className = 'loading-overlay';
        overlay.innerHTML = `
            <div class="loading-content">
                <div class="loading-spinner"></div>
                <p>Đang xử lý...</p>
            </div>
        `;
        document.body.appendChild(overlay);
    }
    
    document.querySelector('.loading-overlay').style.display = 'flex';
}

// Tiện ích - Ẩn loading
function hideLoading() {
    const overlay = document.querySelector('.loading-overlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
}

// Tiện ích - Hiển thị thông báo
function showMessage(message, type = 'info') {
    // Xóa thông báo cũ
    const existingAlert = document.querySelector('.alert-message');
    if (existingAlert) {
        existingAlert.remove();
    }

    // Tạo thông báo mới
    const alert = document.createElement('div');
    alert.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-message`;
    alert.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'}"></i>
        ${message}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
    `;
    
    // Thêm vào đầu container
    const container = document.querySelector('.container');
    container.insertBefore(alert, container.firstChild);

    // Tự động ẩn sau 5 giây
    setTimeout(() => {
        if (alert.parentElement) {
            alert.remove();
        }
    }, 5000);
}

// Tiện ích - Format tiền tệ
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
    }).format(amount);
}

// Khởi tạo khi trang load
document.addEventListener('DOMContentLoaded', function() {
    // Thêm CSS cho loading overlay
    const style = document.createElement('style');
    style.textContent = `
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }
        
        .loading-content {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
        }
        
        .loading-content p {
            margin-top: 1rem;
            margin-bottom: 0;
            color: #666;
        }
        
        .alert-message {
            position: relative;
            margin-bottom: 1rem;
        }
        
        .alert-message .btn-close {
            position: absolute;
            top: 0.5rem;
            right: 0.5rem;
            background: none;
            border: none;
            font-size: 1.2rem;
            cursor: pointer;
            opacity: 0.7;
        }
        
        .alert-message .btn-close:hover {
            opacity: 1;
        }
    `;
    document.head.appendChild(style);

    // Gán sự kiện cho các nút
    document.querySelectorAll('.quantity-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const onclick = this.getAttribute('onclick');
            if (onclick) {
                eval(onclick);
            }
        });
    });

    document.querySelectorAll('.remove-item-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const onclick = this.getAttribute('onclick');
            if (onclick) {
                eval(onclick);
            }
        });
    });

    // Sự kiện cho nút thanh toán
    document.querySelector('.checkout-btn')?.addEventListener('click', checkout);

    // Sự kiện cho nút áp dụng mã khuyến mãi
    document.querySelector('.promo-section .btn')?.addEventListener('click', applyPromoCode);

    // Sự kiện Enter cho ô nhập mã khuyến mãi
    document.querySelector('.promo-section input')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            applyPromoCode();
        }
    });

    // Cập nhật cart display định kỳ (mỗi 30 giây)
    setInterval(updateCartDisplay, 30000);
});

// Xử lý khi người dùng rời khỏi trang
window.addEventListener('beforeunload', function() {
    // Có thể thêm logic lưu trạng thái giỏ hàng ở đây
});
