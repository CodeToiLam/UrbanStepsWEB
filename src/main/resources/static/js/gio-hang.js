document.addEventListener('DOMContentLoaded', function() {
    // Lấy CSRF token từ meta tag
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    // Cập nhật số lượng sản phẩm - SỬA ĐỂ DÙNG FORM DATA
    function updateQuantity(itemId, newQuantity) {
        if (newQuantity < 1) {
            if (confirm('Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?')) {
                removeItem(itemId);
            }
            return;
        }

        showLoading();
        
        // Tạo form data thay vì JSON
        const formData = new FormData();
        formData.append('gioHangChiTietId', itemId);
        formData.append('soLuong', newQuantity);

        fetch('/api/cart/update-quantity', {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            },
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 403) {
                        throw new Error('Yêu cầu bị từ chối: Vui lòng đăng nhập lại hoặc kiểm tra quyền truy cập.');
                    }
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    // Cập nhật hiển thị ngay lập tức
                    updateItemDisplay(itemId, newQuantity, data.itemTotal);
                    updateCartTotal(data.cartTotal, data.cartCount);
                    showMessage('Cập nhật số lượng thành công!', 'success');
                } else {
                    showMessage(data.message || 'Có lỗi xảy ra khi cập nhật số lượng!', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage(error.message || 'Có lỗi xảy ra khi cập nhật số lượng!', 'error');
            })
            .finally(() => {
                hideLoading();
            });
    }
    
    // Cập nhật hiển thị của một item cụ thể
    function updateItemDisplay(itemId, newQuantity, itemTotal) {
        // Cập nhật số lượng trong input
        const quantityInput = document.querySelector(`input[data-item-id="${itemId}"]`);
        if (quantityInput) {
            quantityInput.value = newQuantity;
        }
        
        // Cập nhật số lượng hiển thị
        const quantityDisplay = document.querySelector(`[data-item-id="${itemId}"]`).closest('.quantity-control').querySelector('.quantity-value');
        if (quantityDisplay) {
            quantityDisplay.textContent = newQuantity;
        }
        
        // Cập nhật tổng tiền của item
        const itemTotalElement = document.querySelector(`[data-item-total="${itemId}"]`);
        if (itemTotalElement) {
            itemTotalElement.textContent = formatCurrency(itemTotal);
        }
    }
    
    // Cập nhật tổng tiền giỏ hàng
    function updateCartTotal(cartTotal, cartCount) {
        const cartTotalElement = document.getElementById('cart-total');
        if (cartTotalElement) {
            cartTotalElement.textContent = formatCurrency(cartTotal);
        }
        
        // Cập nhật số lượng trong header
        const cartCountElement = document.querySelector('.cart-count');
        if (cartCountElement) {
            cartCountElement.textContent = cartCount;
            if (cartCount === 0) {
                cartCountElement.style.display = 'none';
            } else {
                cartCountElement.style.display = 'flex';
            }
        }
    }
    
    // Format tiền tệ
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    function removeItem(itemId) {
        if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
            return;
        }

        showLoading();

        fetch('/api/cart/remove', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({
                itemId: itemId
            })
        })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 403) {
                        throw new Error('Yêu cầu bị từ chối: Vui lòng đăng nhập lại hoặc kiểm tra quyền truy cập.');
                    }
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    location.reload();
                    showMessage('Xóa sản phẩm thành công!', 'success');
                } else {
                    showMessage(data.message || 'Có lỗi xảy ra khi xóa sản phẩm!', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage(error.message || 'Có lỗi xảy ra khi xóa sản phẩm!', 'error');
            })
            .finally(() => {
                hideLoading();
            });
    }

    // Cập nhật hiển thị giỏ hàng
    function updateCartDisplay() {
        fetch('/api/cart/info')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    document.querySelectorAll('.total-amount').forEach(element => {
                        element.textContent = formatCurrency(data.totalAmount);
                    });
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
        const promoCode = document.querySelector('#voucherCode')?.value.trim() || document.querySelector('.promo-section input').value.trim();

        if (!promoCode) {
            showMessage('Vui lòng nhập hoặc chọn mã khuyến mãi!', 'error');
            return;
        }

        showLoading();

        // Thử gọi endpoint /checkout/api/apply-voucher-json (ưu tiên)
        fetch('/checkout/api/apply-voucher-json', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({
                voucherCode: promoCode
            })
        })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 403) {
                        throw new Error('Yêu cầu bị từ chối: Vui lòng đăng nhập lại hoặc kiểm tra quyền truy cập.');
                    }
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                handleVoucherResponse(data, promoCode);
            })
            .catch(error => {
                console.error('Error with /apply-voucher-json:', error);
                // Thử lại với endpoint /checkout/api/apply-voucher sử dụng form data
                const formData = new FormData();
                formData.append('voucherCode', promoCode);
                fetch('/checkout/api/apply-voucher', {
                    method: 'POST',
                    headers: {
                        'X-CSRF-TOKEN': csrfToken
                    },
                    body: formData
                })
                    .then(response => {
                        if (!response.ok) {
                            if (response.status === 403) {
                                throw new Error('Yêu cầu bị từ chối: Vui lòng đăng nhập lại hoặc kiểm tra quyền truy cập.');
                            }
                            throw new Error(`HTTP error! Status: ${response.status}`);
                        }
                        return response.json();
                    })
                    .then(data => {
                        handleVoucherResponse(data, promoCode);
                    })
                    .catch(error => {
                        console.error('Error with /apply-voucher:', error);
                        showMessage(error.message || 'Có lỗi xảy ra khi áp dụng mã khuyến mãi!', 'error');
                        document.getElementById('voucherError').textContent = error.message || 'Có lỗi xảy ra khi áp dụng mã khuyến mãi!';
                        document.getElementById('voucherError').style.display = 'block';
                        document.getElementById('voucherMessage').style.display = 'none';
                    })
                    .finally(() => {
                        hideLoading();
                    });
            });
    }

    // Xử lý response từ cả hai endpoint
    function handleVoucherResponse(data, promoCode) {
        if (data.success) {
            document.getElementById('voucherMessage').textContent = data.message;
            document.getElementById('voucherMessage').style.display = 'block';
            document.getElementById('voucherError').style.display = 'none';
            document.getElementById('discountAmount').textContent = formatCurrency(data.totalAmount - data.discountedTotal);
            document.getElementById('finalTotal').textContent = formatCurrency(data.discountedTotal);
            showMessage('Áp dụng mã khuyến mãi thành công!', 'success');
        } else {
            let errorMessage = data.message || 'Mã khuyến mãi không hợp lệ!';
            // Thêm thông tin chi tiết từ server (nếu có)
            if (data.errorCode) {
                switch (data.errorCode) {
                    case 'VOUCHER_NOT_FOUND':
                        errorMessage = `Mã ${promoCode} không tồn tại. Vui lòng kiểm tra lại.`;
                        break;
                    case 'VOUCHER_EXPIRED':
                        errorMessage = `Mã ${promoCode} đã hết hạn.`;
                        break;
                    case 'VOUCHER_MIN_AMOUNT':
                        errorMessage = `Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã ${promoCode}.`;
                        break;
                    case 'VOUCHER_INACTIVE':
                        errorMessage = `Mã ${promoCode} hiện không hoạt động.`;
                        break;
                    default:
                        errorMessage = `Mã ${promoCode} không áp dụng được: ${data.message}`;
                }
            }
            document.getElementById('voucherError').textContent = errorMessage;
            document.getElementById('voucherError').style.display = 'block';
            document.getElementById('voucherMessage').style.display = 'none';
            showMessage(errorMessage, 'error');
        }
    }

    // Thanh toán
    function checkout() {
        showLoading();

        const cartItems = document.querySelectorAll('.cart-item');
        if (cartItems.length === 0) {
            showMessage('Giỏ hàng của bạn đang trống!', 'error');
            hideLoading();
            return;
        }

        window.location.href = '/checkout';
    }

    // Tiện ích - Hiển thị loading
    function showLoading() {
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
        const existingAlert = document.querySelector('.alert-message');
        if (existingAlert) {
            existingAlert.remove();
        }

        const alert = document.createElement('div');
        alert.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-message`;
        alert.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'}"></i>
            ${message}
            <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
        `;
        const container = document.querySelector('.container');
        container.insertBefore(alert, container.firstChild);

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

        document.querySelector('.checkout-btn')?.addEventListener('click', checkout);
        document.querySelector('.promo-section .btn')?.addEventListener('click', applyPromoCode);
        document.querySelector('.promo-section input')?.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                applyPromoCode();
            }
        });

        document.querySelector('#applyVoucherBtn')?.addEventListener('click', applyPromoCode);
        document.querySelector('#voucherCode')?.addEventListener('change', applyPromoCode);

        setInterval(updateCartDisplay, 30000);
    });

    window.addEventListener('beforeunload', function() {});
});