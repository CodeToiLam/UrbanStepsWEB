/**
 * Account Page JavaScript - Enhanced functionality for user account management
 * Includes voucher management, address handling, and interactive features
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize account page functionality
    initAccountTabs();
    initVoucherManagement();
    initAddressManagement();
    initOrderActions();
    initPasswordStrength();
    initCopyFunctionality();
});

/**
 * Initialize tab functionality with URL hash support
 */
function initAccountTabs() {
    // Handle tab switching with URL hash
    const hash = window.location.hash;
    if (hash) {
        const trigger = document.querySelector(`a[href="${hash}"]`);
        if (trigger) {
            const tab = new bootstrap.Tab(trigger);
            tab.show();
        }
    }

    // Update URL when tab changes
    document.querySelectorAll('#accountTab .nav-link').forEach(link => {
        link.addEventListener('shown.bs.tab', function(e) {
            const hash = e.target.getAttribute('href');
            if (hash && hash.startsWith('#')) {
                history.replaceState(null, null, hash);
            }
        });
    });
}

/**
 * Voucher management functionality
 */
function initVoucherManagement() {
    const voucherTab = document.getElementById('vouchers-tab');
    const voucherContent = document.getElementById('vouchers');
    let vouchersLoaded = false;

    function loadVouchers() {
        if (vouchersLoaded) return;
        vouchersLoaded = true;

        const vouchersList = document.getElementById('vouchers-list');
        const vouchersEmpty = document.getElementById('vouchers-empty');
        
        // Show loading state
        vouchersList.innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Đang tải...</span></div></div>';

        fetch('/tai-khoan/api/vouchers')
            .then(response => response.json())
            .then(data => {
                const vouchers = Array.isArray(data) ? data : [];
                vouchersList.innerHTML = '';

                if (vouchers.length === 0) {
                    vouchersEmpty.style.display = 'block';
                    return;
                }

                vouchers.forEach(voucher => {
                    const voucherCard = createVoucherCard(voucher);
                    vouchersList.appendChild(voucherCard);
                });
            })
            .catch(error => {
                console.error('Error loading vouchers:', error);
                vouchersList.innerHTML = '<div class="col-12 text-center text-danger">Lỗi tải voucher. Vui lòng thử lại.</div>';
            });
    }

    // Load vouchers when tab is activated
    if (voucherTab) {
        voucherTab.addEventListener('shown.bs.tab', loadVouchers);
        
        // Load immediately if page opened with vouchers tab
        if (window.location.hash === '#vouchers') {
            loadVouchers();
        }
    }
}

/**
 * Create voucher card element
 */
function createVoucherCard(voucher) {
    const col = document.createElement('div');
    col.className = 'col-md-6 mb-3';

    const isPercentage = voucher.giamTheoPhanTram;
    const valueText = isPercentage 
        ? `${voucher.giaTri || 0}%` 
        : `${(voucher.giaTri || 0).toLocaleString('vi-VN')}đ`;
    
    const valueClass = isPercentage ? 'bg-success' : 'bg-primary';
    
    const minOrderText = voucher.giaTriDonToiThieu 
        ? `Đơn tối thiểu: ${(voucher.giaTriDonToiThieu).toLocaleString('vi-VN')}đ<br>` 
        : '';
    
    const maxDiscountText = voucher.giamToiDa 
        ? `Giảm tối đa: ${(voucher.giamToiDa).toLocaleString('vi-VN')}đ<br>` 
        : '';
    
    const expiryDate = voucher.ngayKetThuc 
        ? voucher.ngayKetThuc.replace('T', ' ').substring(0, 10) 
        : '';

    col.innerHTML = `
        <div class="voucher-card h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <div class="voucher-code">${voucher.maPhieuGiamGia || ''}</div>
                    <div class="voucher-name">${voucher.tenPhieuGiamGia || ''}</div>
                </div>
                <span class="badge ${valueClass} voucher-value">${valueText}</span>
            </div>
            <div class="voucher-details mb-3">
                ${minOrderText}
                ${maxDiscountText}
                ${expiryDate ? `HSD: ${expiryDate}` : ''}
            </div>
            <div class="d-flex gap-2">
                <input type="text" class="form-control form-control-sm" value="${voucher.maPhieuGiamGia || ''}" readonly>
                <button type="button" class="btn btn-sm btn-outline-secondary copy-btn" 
                        onclick="copyVoucherCode(this, '${voucher.maPhieuGiamGia || ''}')">
                    Copy
                </button>
            </div>
        </div>
    `;

    return col;
}

/**
 * Copy voucher code functionality
 */
function copyVoucherCode(button, code) {
    navigator.clipboard.writeText(code).then(() => {
        button.classList.add('copied');
        button.textContent = 'Đã copy!';
        
        setTimeout(() => {
            button.classList.remove('copied');
            button.textContent = 'Copy';
        }, 2000);
    }).catch(err => {
        console.error('Failed to copy: ', err);
        showToast('Không thể sao chép mã', 'error');
    });
}

/**
 * Address management functionality
 */
function initAddressManagement() {
    // Confirm address deletion
    document.addEventListener('click', function(e) {
        if (e.target.matches('a[href*="/dia-chi/xoa/"]')) {
            e.preventDefault();
            
            if (confirm('Bạn có chắc chắn muốn xóa địa chỉ này?')) {
                window.location.href = e.target.href;
            }
        }
    });

    // Set default address
    document.addEventListener('click', function(e) {
        if (e.target.matches('.set-default-address')) {
            e.preventDefault();
            
            const addressId = e.target.dataset.addressId;
            setDefaultAddress(addressId);
        }
    });
}

/**
 * Set default address
 */
function setDefaultAddress(addressId) {
    fetch(`/tai-khoan/dia-chi/set-default/${addressId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('Đã đặt làm địa chỉ mặc định', 'success');
            // Reload address tab
            location.reload();
        } else {
            showToast(data.message || 'Có lỗi xảy ra', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Có lỗi xảy ra', 'error');
    });
}

/**
 * Order actions functionality
 */
function initOrderActions() {
    // Cancel order confirmation
    document.addEventListener('submit', function(e) {
        if (e.target.matches('form[action*="/cancel"]')) {
            e.preventDefault();
            
            if (confirm('Bạn chắc chắn muốn hủy đơn hàng này? Hành động này không thể hoàn tác.')) {
                e.target.submit();
            }
        }
    });

    // Return request confirmation
    document.addEventListener('click', function(e) {
        if (e.target.matches('a[href*="/return-request"]')) {
            e.preventDefault();
            
            if (confirm('Bạn muốn yêu cầu trả hàng cho đơn hàng này?')) {
                window.location.href = e.target.href;
            }
        }
    });
}

/**
 * Password strength indicator
 */
function initPasswordStrength() {
    const newPasswordInput = document.querySelector('input[name="newPassword"]');
    
    if (newPasswordInput) {
        // Create password strength indicator
        const strengthIndicator = document.createElement('div');
        strengthIndicator.className = 'password-strength mt-2';
        strengthIndicator.innerHTML = '<div class="strength-bar"></div>';
        
        newPasswordInput.parentNode.appendChild(strengthIndicator);
        
        newPasswordInput.addEventListener('input', function() {
            const password = this.value;
            const strength = calculatePasswordStrength(password);
            updatePasswordStrength(strengthIndicator, strength);
        });
    }
}

/**
 * Calculate password strength
 */
function calculatePasswordStrength(password) {
    let score = 0;
    
    if (password.length >= 8) score += 25;
    if (password.match(/[a-z]/)) score += 25;
    if (password.match(/[A-Z]/)) score += 25;
    if (password.match(/[0-9]/)) score += 25;
    if (password.match(/[^a-zA-Z0-9]/)) score += 25;
    
    return Math.min(score, 100);
}

/**
 * Update password strength indicator
 */
function updatePasswordStrength(indicator, strength) {
    const bar = indicator.querySelector('.strength-bar');
    
    bar.style.width = strength + '%';
    
    if (strength < 50) {
        bar.className = 'strength-bar strength-weak';
    } else if (strength < 75) {
        bar.className = 'strength-bar strength-medium';
    } else {
        bar.className = 'strength-bar strength-strong';
    }
}

/**
 * Initialize copy functionality for all copy buttons
 */
function initCopyFunctionality() {
    document.addEventListener('click', function(e) {
        if (e.target.matches('.copy-btn:not([onclick])')) {
            const input = e.target.previousElementSibling;
            if (input && input.value) {
                copyToClipboard(input.value, e.target);
            }
        }
    });
}

/**
 * Copy text to clipboard with visual feedback
 */
function copyToClipboard(text, button) {
    navigator.clipboard.writeText(text).then(() => {
        button.classList.add('copied');
        const originalText = button.textContent;
        button.textContent = 'Đã copy!';
        
        setTimeout(() => {
            button.classList.remove('copied');
            button.textContent = originalText;
        }, 2000);
    }).catch(err => {
        console.error('Failed to copy: ', err);
        showToast('Không thể sao chép', 'error');
    });
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    // Create toast container if it doesn't exist
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }

    // Create toast element
    const toastElement = document.createElement('div');
    toastElement.className = `toast align-items-center text-white bg-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'primary'} border-0`;
    toastElement.setAttribute('role', 'alert');
    toastElement.setAttribute('aria-live', 'assertive');
    toastElement.setAttribute('aria-atomic', 'true');

    toastElement.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    toastContainer.appendChild(toastElement);

    // Initialize and show toast
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 3000
    });
    
    toast.show();

    // Remove toast element after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        toastElement.remove();
    });
}

/**
 * Form validation helpers
 */
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], select[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
        }
    });

    return isValid;
}

/**
 * Auto-save functionality for profile form
 */
function initAutoSave() {
    const profileForm = document.querySelector('form[action*="/cap-nhat"]');
    
    if (profileForm) {
        const inputs = profileForm.querySelectorAll('input, select');
        
        inputs.forEach(input => {
            input.addEventListener('change', function() {
                // Auto-save logic can be implemented here
                console.log('Field changed:', input.name, input.value);
            });
        });
    }
}

/**
 * Image preview for profile picture upload (if implemented)
 */
function initImagePreview() {
    const imageInput = document.querySelector('input[type="file"][accept*="image"]');
    
    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const preview = document.querySelector('.profile-image-preview');
                    if (preview) {
                        preview.src = e.target.result;
                    }
                };
                reader.readAsDataURL(file);
            }
        });
    }
}

/**
 * Lazy loading for order history
 */
function initLazyLoading() {
    const ordersTable = document.querySelector('#orders .table tbody');
    
    if (ordersTable && ordersTable.children.length >= 10) {
        // Implement pagination or infinite scroll here
        console.log('Consider implementing pagination for orders');
    }
}

// Export functions for global access
window.AccountManager = {
    copyVoucherCode,
    showToast,
    validateForm,
    setDefaultAddress
};
