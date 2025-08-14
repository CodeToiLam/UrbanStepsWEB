// Hệ thống quản lý địa chỉ thanh toán hoàn toàn mới
class CheckoutAddressManager {
    constructor() {
        this.selectedAddressId = null;
        this.isUsingManualAddress = true;
        this.addressData = null;
        this.init();
    }

    init() {
        this.loadSavedAddresses();
        this.bindEvents();
    }

    async loadSavedAddresses() {
        try {
            const response = await fetch('/checkout/addresses');
            const result = await response.json();
            
            if (result.success && result.data.length > 0) {
                this.renderAddressList(result.data);
            } else {
                this.showManualAddressOnly();
            }
        } catch (error) {
            console.log('Không thể tải địa chỉ đã lưu:', error);
            this.showManualAddressOnly();
        }
    }

    renderAddressList(addresses) {
        const container = document.getElementById('savedAddressesContainer');
        if (!container) return;

        let html = `
            <div class="saved-addresses-section">
                <h5 class="mb-3">Địa chỉ đã lưu</h5>
                <div class="address-options">
        `;

        addresses.forEach((addr, index) => {
            const isDefault = addr.default;
            html += `
                <div class="address-option ${isDefault ? 'default-address' : ''}" data-id="${addr.id}">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="addressChoice" 
                               id="addr_${addr.id}" value="${addr.id}" ${isDefault ? 'checked' : ''}>
                        <label class="form-check-label" for="addr_${addr.id}">
                            <div class="address-info">
                                <div class="address-name">${addr.ten} - ${addr.sdt}</div>
                                <div class="address-detail">${addr.full}</div>
                                ${isDefault ? '<span class="badge bg-primary">Mặc định</span>' : ''}
                            </div>
                        </label>
                    </div>
                </div>
            `;
        });

        html += `
                    <div class="address-option manual-option">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="addressChoice" 
                                   id="manual_address" value="manual">
                            <label class="form-check-label" for="manual_address">
                                <div class="address-info">
                                    <div class="address-name">Nhập địa chỉ mới</div>
                                    <div class="address-detail">Nhập thông tin địa chỉ giao hàng mới</div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>
                <button type="button" id="useSelectedAddress" class="btn btn-primary mt-3">
                    Sử dụng địa chỉ đã chọn
                </button>
            </div>
        `;

        container.innerHTML = html;

        // Auto select default address
        const defaultAddr = addresses.find(a => a.default);
        if (defaultAddr) {
            this.useAddress(defaultAddr.id, addresses);
        }
    }

    showManualAddressOnly() {
        this.isUsingManualAddress = true;
        this.enableManualFields();
        const container = document.getElementById('savedAddressesContainer');
        if (container) {
            container.innerHTML = '<p class="text-muted">Chưa có địa chỉ đã lưu. Vui lòng nhập địa chỉ giao hàng.</p>';
        }
    }

    bindEvents() {
        // Event delegation cho địa chỉ đã lưu
        document.addEventListener('click', (e) => {
            if (e.target.id === 'useSelectedAddress') {
                this.handleUseSelectedAddress();
            }
            
            if (e.target.name === 'addressChoice') {
                this.handleAddressChoice(e.target);
            }
        });

        // Toggle manual address form
        const toggleBtn = document.getElementById('toggleManualAddress');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                this.toggleManualForm();
            });
        }
    }

    handleAddressChoice(radio) {
        if (radio.value === 'manual') {
            this.isUsingManualAddress = true;
            this.selectedAddressId = null;
            this.showManualFields();
        } else {
            this.isUsingManualAddress = false;
            this.selectedAddressId = radio.value;
            this.hideManualFields();
        }
    }

    async handleUseSelectedAddress() {
        const selected = document.querySelector('input[name="addressChoice"]:checked');
        if (!selected) {
            this.showToast('Vui lòng chọn một địa chỉ', 'error');
            return;
        }

        if (selected.value === 'manual') {
            this.isUsingManualAddress = true;
            this.selectedAddressId = null;
            this.enableManualFields();
            this.showToast('Đã chuyển sang nhập địa chỉ thủ công', 'info');
        } else {
            await this.useAddress(selected.value);
        }
    }

    async useAddress(addressId, addressList = null) {
        try {
            // Nếu không có danh sách địa chỉ, lấy từ server
            if (!addressList) {
                const response = await fetch('/checkout/addresses');
                const result = await response.json();
                addressList = result.success ? result.data : [];
            }

            const address = addressList.find(a => a.id == addressId);
            if (!address) {
                this.showToast('Không tìm thấy địa chỉ', 'error');
                return;
            }

            // Set selected address ID
            this.selectedAddressId = addressId;
            this.isUsingManualAddress = false;

            // Fill form with address data
            this.fillAddressData(address);
            
            // Disable manual fields
            this.disableManualFields();
            
            // Show success message
            this.showToast(`Đã áp dụng địa chỉ: ${address.ten}`, 'success');
            
        } catch (error) {
            console.error('Error using address:', error);
            this.showToast('Có lỗi khi áp dụng địa chỉ', 'error');
        }
    }

    fillAddressData(address) {
        // Fill customer info
        const hoTenEl = document.getElementById('hoTen');
        const sdtEl = document.getElementById('sdt');
        
        if (hoTenEl && address.ten) hoTenEl.value = address.ten;
        if (sdtEl && address.sdt) sdtEl.value = address.sdt;

        // Set address detail
        const diaChiEl = document.getElementById('diaChiGiaoHang');
        if (diaChiEl && address.full) diaChiEl.value = address.full;

        // Set hidden field
        const hiddenEl = document.getElementById('selectedAddressId');
        if (hiddenEl) hiddenEl.value = this.selectedAddressId;
    }

    disableManualFields() {
        const manualSection = document.getElementById('manualAddressFields');
        if (manualSection) {
            manualSection.style.display = 'none';
        }

        // Disable validation for manual address fields
        const fields = ['province', 'district', 'ward', 'diaChiGiaoHang'];
        fields.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.removeAttribute('required');
                el.classList.remove('is-invalid');
            }
        });
    }

    enableManualFields() {
        const manualSection = document.getElementById('manualAddressFields');
        if (manualSection) {
            manualSection.style.display = 'block';
        }

        // Enable validation for manual address fields
        const fields = ['province', 'district', 'ward', 'diaChiGiaoHang'];
        fields.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.setAttribute('required', 'required');
            }
        });

        // Clear selected address
        this.selectedAddressId = null;
        this.isUsingManualAddress = true;
        
        const hiddenEl = document.getElementById('selectedAddressId');
        if (hiddenEl) hiddenEl.value = '';
    }

    showManualFields() {
        this.enableManualFields();
        this.showToast('Chuyển sang nhập địa chỉ thủ công', 'info');
    }

    hideManualFields() {
        this.disableManualFields();
    }

    toggleManualForm() {
        const manualSection = document.getElementById('manualAddressFields');
        if (!manualSection) return;

        const isVisible = manualSection.style.display !== 'none';
        
        if (isVisible) {
            this.disableManualFields();
        } else {
            this.enableManualFields();
            // Unselect saved addresses
            const addressRadios = document.querySelectorAll('input[name="addressChoice"]');
            addressRadios.forEach(radio => radio.checked = false);
        }
    }

    // Validation method for checkout
    validateAddress() {
        if (!this.isUsingManualAddress && this.selectedAddressId) {
            // Using saved address - always valid
            return true;
        }

        // Manual address validation
        const hoTen = document.getElementById('hoTen')?.value?.trim();
        const sdt = document.getElementById('sdt')?.value?.trim();
        const diaChiGiaoHang = document.getElementById('diaChiGiaoHang')?.value?.trim();

        if (!hoTen) {
            this.showToast('Vui lòng nhập họ tên', 'error');
            return false;
        }

        if (!sdt) {
            this.showToast('Vui lòng nhập số điện thoại', 'error');
            return false;
        }

        if (this.isUsingManualAddress) {
            const province = document.getElementById('province')?.value;
            const district = document.getElementById('district')?.value;
            const ward = document.getElementById('ward')?.value;

            if (!province || !district || !ward || !diaChiGiaoHang) {
                this.showToast('Vui lòng điền đầy đủ thông tin địa chỉ', 'error');
                return false;
            }
        }

        return true;
    }

    getAddressData() {
        return {
            selectedAddressId: this.selectedAddressId,
            isUsingManualAddress: this.isUsingManualAddress,
            hoTen: document.getElementById('hoTen')?.value?.trim(),
            sdt: document.getElementById('sdt')?.value?.trim(),
            tinhThanh: document.getElementById('province')?.value,
            quanHuyen: document.getElementById('district')?.value,
            phuongXa: document.getElementById('ward')?.value,
            diaChiGiaoHang: document.getElementById('diaChiGiaoHang')?.value?.trim()
        };
    }

    showToast(message, type = 'info') {
        if (window.toast) {
            window.toast(message, type);
        } else {
            alert(message);
        }
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    window.checkoutAddressManager = new CheckoutAddressManager();
});

// Export for form validation
window.validateCheckoutAddress = function() {
    return window.checkoutAddressManager ? window.checkoutAddressManager.validateAddress() : false;
};

window.getCheckoutAddressData = function() {
    return window.checkoutAddressManager ? window.checkoutAddressManager.getAddressData() : null;
};
