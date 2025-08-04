document.addEventListener('DOMContentLoaded', function() {
    // Lấy CSRF token từ meta tag
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    // Áp dụng mã khuyến mãi
    function applyPromoCode() {
        const promoCode = document.querySelector('#voucherCode')?.value.trim() || document.querySelector('input[name="maGiamGia"]').value.trim();

        if (!promoCode) {
            showMessage('Vui lòng nhập hoặc chọn mã khuyến mãi!', 'error');
            return;
        }

        showLoading();

        fetch('/checkout/api/apply-voucher-json', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken // Thêm CSRF token
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
                if (data.success) {
                    document.getElementById('voucherMessage').textContent = data.message;
                    document.getElementById('voucherMessage').style.display = 'block';
                    document.getElementById('voucherError').style.display = 'none';
                    document.getElementById('discountAmount').textContent = formatCurrency(data.totalAmount - data.discountedTotal);
                    document.getElementById('finalTotal').textContent = formatCurrency(data.discountedTotal);
                    document.querySelector('input[name="maGiamGia"]').value = promoCode; // Cập nhật mã vào input để gửi form
                    showMessage('Áp dụng mã khuyến mãi thành công!', 'success');
                } else {
                    document.getElementById('voucherError').textContent = data.message || 'Mã khuyến mãi không hợp lệ!';
                    document.getElementById('voucherError').style.display = 'block';
                    document.getElementById('voucherMessage').style.display = 'none';
                    showMessage(data.message || 'Mã khuyến mãi không hợp lệ!', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage(error.message || 'Có lỗi xảy ra khi áp dụng mã khuyến mãi!', 'error');
                document.getElementById('voucherError').textContent = error.message || 'Có lỗi xảy ra khi áp dụng mã khuyến mãi!';
                document.getElementById('voucherError').style.display = 'block';
                document.getElementById('voucherMessage').style.display = 'none';
            })
            .finally(() => {
                hideLoading();
            });
    }

    // Xử lý đặt hàng
    function placeOrder() {
        showLoading();

        const orderData = {
            fullName: document.querySelector('input[name="fullName"]').value.trim(),
            phoneNumber: document.querySelector('input[name="phoneNumber"]').value.trim(),
            email: document.querySelector('input[name="email"]').value.trim(),
            province: document.querySelector('#province').value,
            district: document.querySelector('#district').value,
            ward: document.querySelector('#ward').value,
            addressDetail: document.querySelector('input[name="addressDetail"]').value.trim(),
            note: document.querySelector('textarea[name="note"]').value.trim(),
            paymentMethod: document.querySelector('input[name="paymentMethod"]:checked').value,
            promoCode: document.querySelector('input[name="maGiamGia"]').value.trim()
        };

        fetch('/checkout/place-order', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(orderData)
        })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 403) {
                        throw new Error('Yêu cầu bị từ chối: Vui lòng đăng nhập lại.');
                    }
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    showMessage('Đặt hàng thành công!', 'success');
                    window.location.href = '/thanh-toan-thanh-cong'; // Chuyển hướng đến trang thành công
                } else {
                    showMessage(data.message || 'Có lỗi xảy ra khi đặt hàng!', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage(error.message || 'Có lỗi xảy ra khi đặt hàng!', 'error');
            })
            .finally(() => {
                hideLoading();
            });
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

    // Xử lý lỗi tải dữ liệu địa chỉ
    function loadAddressData(attempt = 1, maxAttempts = 3) {
        const provinceSelect = document.getElementById("province");
        const districtSelect = document.getElementById("district");
        const wardSelect = document.getElementById("ward");
        let addressData = [];

        // Thêm option mặc định
        provinceSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố</option>';
        districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';
        wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';

        // Load dữ liệu từ JSON
        fetch('/data/vietnamAddress.json')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Không tải được dữ liệu địa chỉ (lần thử ${attempt}/${maxAttempts})`);
                }
                return response.json();
            })
            .then(data => {
                addressData = data;

                // Thêm các tỉnh thành vào dropdown
                data.forEach(province => {
                    const option = document.createElement('option');
                    option.value = province.Id;
                    option.textContent = province.Name;
                    provinceSelect.appendChild(option);
                });

                // Khôi phục giá trị đã chọn nếu có
                if (window.preSelectedAddress) {
                    restoreSelectedAddress();
                }

                // Xử lý khi chọn tỉnh/thành phố
                provinceSelect.addEventListener('change', function() {
                    districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';
                    wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';
                    districtSelect.disabled = !this.value;
                    wardSelect.disabled = true;

                    if (!this.value) return;

                    const selectedProvince = addressData.find(p => p.Id === this.value);
                    if (selectedProvince && selectedProvince.Districts) {
                        selectedProvince.Districts.forEach(district => {
                            const option = document.createElement('option');
                            option.value = district.Id;
                            option.textContent = district.Name;
                            districtSelect.appendChild(option);
                        });
                    }
                });

                // Xử lý khi chọn quận/huyện
                districtSelect.addEventListener('change', function() {
                    wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';
                    wardSelect.disabled = !this.value;

                    if (!this.value || !provinceSelect.value) return;

                    const selectedProvince = addressData.find(p => p.Id === provinceSelect.value);
                    if (!selectedProvince) return;

                    const selectedDistrict = selectedProvince.Districts.find(d => d.Id === this.value);
                    if (selectedDistrict && selectedDistrict.Wards) {
                        selectedDistrict.Wards.forEach(ward => {
                            const option = document.createElement('option');
                            option.value = ward.Id;
                            option.textContent = ward.Name;
                            wardSelect.appendChild(option);
                        });
                    }
                });
            })
            .catch(error => {
                console.error('Lỗi khi tải dữ liệu địa chỉ:', error);
                if (attempt < maxAttempts) {
                    console.log(`Thử lại lần ${attempt + 1}`);
                    setTimeout(() => loadAddressData(attempt + 1, maxAttempts), 1000);
                } else {
                    showAddressError();
                }
            });
    }

    function showAddressError() {
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-warning mb-3';
        alertDiv.innerHTML = `
            <i class="fas fa-exclamation-triangle me-2"></i>
            Không thể tải danh sách địa chỉ. Vui lòng nhập địa chỉ thủ công vào ô "Địa chỉ cụ thể".
        `;
        document.querySelector('.address-selector').prepend(alertDiv);

        // Vô hiệu hóa các dropdown để buộc nhập thủ công
        document.getElementById('province').disabled = true;
        document.getElementById('district').disabled = true;
        document.getElementById('ward').disabled = true;
    }

    function restoreSelectedAddress() {
        // Code để khôi phục giá trị đã chọn (nếu cần)
    }

    // Gắn sự kiện cho nút áp dụng mã khuyến mãi và dropdown
    document.querySelector('#applyVoucherBtn')?.addEventListener('click', applyPromoCode);
    document.querySelector('#voucherCode')?.addEventListener('change', applyPromoCode);
    document.querySelector('input[name="maGiamGia"]')?.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            applyPromoCode();
        }
    });

    // Gắn sự kiện cho nút "Đặt hàng ngay"
    document.querySelector('#placeOrderBtn')?.addEventListener('click', placeOrder);

    // Khởi tạo tải dữ liệu địa chỉ
    loadAddressData();
});