document.addEventListener('DOMContentLoaded', function() {
    // Lấy CSRF token từ meta tag (an toan hơn với try-catch)
    let csrfToken = '';
    try {
        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        if (csrfMeta) {
            csrfToken = csrfMeta.getAttribute('content') || '';
        }
    } catch (error) {
        console.warn('Không thể lấy CSRF token:', error);
    }

    // Format tiền tệ VND
    function formatCurrency(amount) {
        if (typeof amount === 'string') {
            amount = parseFloat(amount);
        }
        if (isNaN(amount)) {
            return '0đ';
        }
        return amount.toLocaleString('vi-VN') + 'đ';
    }

    // Validation số lượng
    function validateQuantity(itemId, newQuantity, maxStock) {
        if (newQuantity < 1) {
            return false; // Sẽ được xử lý riêng để xóa sản phẩm
        }
        
        if (maxStock !== undefined && newQuantity > maxStock) {
            showMessage(`Chỉ còn ${maxStock} sản phẩm trong kho`, 'error');
            return false;
        }
        
        if (newQuantity > 999) {
            showMessage('Số lượng tối đa là 999', 'error');
            return false;
        }
        
        return true;
    }

    // Validation giá sản phẩm
    function validatePrice(price) {
        if (!price || price <= 0) {
            showMessage('Sản phẩm này không có giá hợp lệ', 'error');
            return false;
        }
        return true;
    }

    // Cập nhật số lượng sản phẩm với validation
    function updateQuantity(itemId, newQuantity) {
        // Xử lý trường hợp xóa sản phẩm
        if (newQuantity < 1) {
            // Use our modern confirmation dialog instead of default confirm
            const confirmDialog = document.createElement('div');
            confirmDialog.className = 'cart-confirm-dialog';
            confirmDialog.innerHTML = `
                <div class="cart-confirm-content">
                    <h5><i class="fas fa-question-circle text-primary"></i> Xác nhận</h5>
                    <p>Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?</p>
                    <div class="cart-confirm-actions">
                        <button class="btn btn-outline-secondary btn-cancel">Hủy bỏ</button>
                        <button class="btn btn-danger btn-confirm">Xóa sản phẩm</button>
                    </div>
                </div>
            `;
            document.body.appendChild(confirmDialog);
            
            // Add event listeners
            confirmDialog.querySelector('.btn-cancel').addEventListener('click', () => {
                // Reset quantity input
                const quantityInput = document.querySelector(`input[data-item-id="${itemId}"]`);
                if (quantityInput) {
                    quantityInput.value = quantityInput.getAttribute('value') || 1;
                }
                confirmDialog.remove();
            });
            
            confirmDialog.querySelector('.btn-confirm').addEventListener('click', () => {
                confirmDialog.remove();
                removeItem(itemId);
            });
            
            // Close when clicking outside
            confirmDialog.addEventListener('click', (e) => {
                if (e.target === confirmDialog) {
                    // Reset quantity input
                    const quantityInput = document.querySelector(`input[data-item-id="${itemId}"]`);
                    if (quantityInput) {
                        quantityInput.value = quantityInput.getAttribute('value') || 1;
                    }
                    confirmDialog.remove();
                }
            });
            return;
        }

        // Get the max stock from the data attribute if available
        const itemElement = document.querySelector(`.cart-item[data-item-id="${itemId}"]`);
                         
        const maxStock = itemElement && itemElement.getAttribute('data-max-stock') ? 
                         parseInt(itemElement.getAttribute('data-max-stock')) : undefined;

        // Validation số lượng with maxStock
        if (!validateQuantity(itemId, newQuantity, maxStock)) {
            // Reset quantity input on validation failure
            const quantityInput = document.querySelector(`input[data-item-id="${itemId}"]`);
            if (quantityInput) {
                quantityInput.value = quantityInput.getAttribute('value') || 1;
            }
            return;
        }

        showLoading();
        
        // Hiển thị loading cho item cụ thể
        const quantityInput = document.querySelector(`input[data-item-id="${itemId}"]`);
        const cartItem = quantityInput?.closest('.cart-item');
        const quantityDisplay = cartItem?.querySelector('.quantity-value, .quantity-input');
        const originalText = quantityDisplay?.value || quantityDisplay?.textContent;
        
        if (quantityDisplay) {
            if (quantityDisplay.tagName === 'INPUT') {
                quantityDisplay.style.color = '#007bff';
            } else {
                quantityDisplay.textContent = '...';
                quantityDisplay.style.color = '#007bff';
            }
        }
        
        // Tạo form data
        const formData = new FormData();
        formData.append('gioHangChiTietId', itemId);
        formData.append('soLuong', newQuantity);

        const headers = {};
        if (csrfToken) {
            headers['X-CSRF-TOKEN'] = csrfToken;
        }
        
        // Add retry mechanism
        let retryCount = 0;
        const maxRetries = 2;
        
        function attemptUpdate() {
            fetch('/api/cart/update-quantity', {
                method: 'POST',
                headers: headers,
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
                        // Validation giá
                        if (!validatePrice(data.itemTotal)) {
                            throw new Error('Giá sản phẩm không hợp lệ');
                        }
                        
                        // Cập nhật hiển thị ngay lập tức
                        updateItemDisplay(itemId, newQuantity, data.itemTotal);
                        updateCartTotal(data.cartTotal, data.cartCount);
                        showMessage('Cập nhật số lượng thành công!', 'success');
                    } else {
                        // If failed but we can retry
                        if (retryCount < maxRetries) {
                            retryCount++;
                            console.log(`Retry attempt ${retryCount} for updating item ${itemId} quantity to ${newQuantity}`);
                            setTimeout(attemptUpdate, 1000); // Wait 1 second before retry
                            return;
                        }
                        
                        // Khôi phục số lượng cũ nếu thất bại sau khi hết lượt retry
                        if (quantityDisplay && originalText) {
                            if (quantityDisplay.tagName === 'INPUT') {
                                quantityDisplay.value = originalText;
                            } else {
                                quantityDisplay.textContent = originalText;
                            }
                            quantityDisplay.style.color = '';
                        }
                        showMessage(data.message || 'Có lỗi xảy ra khi cập nhật số lượng!', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    
                    // If error but we can retry
                    if (retryCount < maxRetries) {
                        retryCount++;
                        console.log(`Retry attempt ${retryCount} for updating item ${itemId} quantity after error`);
                        setTimeout(attemptUpdate, 1000); // Wait 1 second before retry
                        return;
                    }
                    
                    // Khôi phục số lượng cũ nếu có lỗi sau khi hết lượt retry
                    if (quantityDisplay && originalText) {
                        if (quantityDisplay.tagName === 'INPUT') {
                            quantityDisplay.value = originalText;
                        } else {
                            quantityDisplay.textContent = originalText;
                        }
                        quantityDisplay.style.color = '';
                    }
                    showMessage(error.message || 'Có lỗi xảy ra khi cập nhật số lượng!', 'error');
                })
                .finally(() => {
                    if (retryCount >= maxRetries || !retryCount) {
                        hideLoading();
                    }
                });
        }
        
        // Start the update process
        attemptUpdate();
    }
    
    // Cập nhật hiển thị của một item cụ thể
    function updateItemDisplay(itemId, newQuantity, itemTotal) {
        console.log(`Updating display for item ${itemId}: quantity=${newQuantity}, total=${itemTotal}`);
        
        // Cập nhật số lượng trong input hidden
        const quantityInput = document.querySelector(`input[type="hidden"][data-item-id="${itemId}"]`);
        if (quantityInput) {
            quantityInput.value = newQuantity;
            console.log(`Updated hidden input for item ${itemId}`);
        }
        
        // Tìm container của item này
        const cartItem = quantityInput?.closest('.cart-item');
        if (!cartItem) {
            console.warn(`Cart item container not found for item ${itemId}`);
            return;
        }
        
        // Cập nhật số lượng hiển thị trong input
        const quantityInputElement = cartItem.querySelector('.quantity-input');
        if (quantityInputElement) {
            quantityInputElement.value = newQuantity;
            quantityInputElement.style.color = '';
            console.log(`Updated quantity input for item ${itemId}: ${newQuantity}`);
        }
        
        // Cập nhật số lượng hiển thị trong span (nếu có)
        const quantityValueElement = cartItem.querySelector('.quantity-value');
        if (quantityValueElement) {
            quantityValueElement.textContent = newQuantity;
            quantityValueElement.style.color = '';
            console.log(`Updated quantity display for item ${itemId}: ${newQuantity}`);
        }
        
        // Add animation effect for quantity
        const quantitySelector = cartItem.querySelector('.cart-quantity-selector');
        if (quantitySelector) {
            quantitySelector.classList.add('updating');
            setTimeout(() => {
                quantitySelector.classList.remove('updating');
            }, 800);
        }
        
        // Cập nhật tổng tiền của item với format tiền tệ và animation
        const itemTotalElement = cartItem.querySelector(`[data-item-total="${itemId}"]`);
        if (itemTotalElement) {
            const oldText = itemTotalElement.textContent;
            const newText = formatCurrency(itemTotal);
            
            // Add highlight animation for price change
            itemTotalElement.classList.add('price-updated');
            itemTotalElement.textContent = newText;
            
            // Remove highlight after animation completes
            setTimeout(() => {
                itemTotalElement.classList.remove('price-updated');
            }, 1500);
            
            console.log(`Updated item total for item ${itemId}: ${formatCurrency(itemTotal)}`);
        } else {
            console.warn(`Item total element not found for item ${itemId}`);
        }
        
        // Cập nhật onclick attributes cho quantity buttons với giá trị mới
        const decreaseBtn = cartItem.querySelector('.decrease-btn');
        const increaseBtn = cartItem.querySelector('.increase-btn');
        
        if (decreaseBtn) {
            decreaseBtn.setAttribute('onclick', `updateQuantity(${itemId}, ${newQuantity - 1})`);
        }
        if (increaseBtn) {
            increaseBtn.setAttribute('onclick', `updateQuantity(${itemId}, ${newQuantity + 1})`);
        }
        
        console.log(`Finished updating item ${itemId}`);
    }
    
    // Cập nhật tổng tiền giỏ hàng
    function updateCartTotal(cartTotal, cartCount) {
        // Cập nhật tổng tiền với format tiền tệ
        const cartTotalElement = document.getElementById('cart-total');
        if (cartTotalElement) {
            cartTotalElement.textContent = formatCurrency(cartTotal);
        }
        
        // Cập nhật tất cả element có class cart-total
        const allCartTotalElements = document.querySelectorAll('.cart-total');
        allCartTotalElements.forEach(element => {
            element.textContent = formatCurrency(cartTotal);
        });
        
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
        
        console.log(`Updated cart total: ${formatCurrency(cartTotal)}, count: ${cartCount}`);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    function removeItem(itemId) {
        // Create a more modern, elegant confirmation dialog
        const confirmDialog = document.createElement('div');
        confirmDialog.className = 'cart-confirm-dialog';
        confirmDialog.innerHTML = `
            <div class="cart-confirm-content">
                <h5><i class="fas fa-question-circle text-primary"></i> Xác nhận</h5>
                <p>Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?</p>
                <div class="cart-confirm-actions">
                    <button class="btn btn-outline-secondary btn-cancel">Hủy bỏ</button>
                    <button class="btn btn-danger btn-confirm">Xóa sản phẩm</button>
                </div>
            </div>
        `;
        document.body.appendChild(confirmDialog);
        
        // Add event listeners
        const cancelBtn = confirmDialog.querySelector('.btn-cancel');
        const confirmBtn = confirmDialog.querySelector('.btn-confirm');
        
        cancelBtn.addEventListener('click', () => {
            confirmDialog.remove();
        });
        
        confirmBtn.addEventListener('click', () => {
            confirmDialog.remove();
            performDelete(itemId);
        });
        
        // Close when clicking outside
        confirmDialog.addEventListener('click', (e) => {
            if (e.target === confirmDialog) {
                confirmDialog.remove();
            }
        });
    }
    
    function performDelete(itemId) {
        showLoading();
        
        // Add retry mechanism
        let retryCount = 0;
        const maxRetries = 2;
        
        // Find the cart item for animation
        const cartItem = document.querySelector(`.cart-item[data-item-id="${itemId}"]`);
        if (cartItem) {
            cartItem.style.transition = 'all 0.5s ease';
            cartItem.style.opacity = '0.5';
        }

        function attemptDelete() {
            const headers = {
                'Content-Type': 'application/json'
            };
            
            if (csrfToken) {
                headers['X-CSRF-TOKEN'] = csrfToken;
            }
            
            fetch('/api/cart/remove', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({
                    itemId: itemId
                })
            })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 403) {
                            throw new Error('Yêu cầu bị từ chối: Vui lòng đăng nhập lại.');
                        }
                        throw new Error(`Lỗi kết nối: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        // Animate removal of cart item
                        if (cartItem) {
                            cartItem.style.height = '0';
                            cartItem.style.margin = '0';
                            cartItem.style.padding = '0';
                            cartItem.style.opacity = '0';
                            
                            // Remove from DOM after animation
                            setTimeout(() => {
                                cartItem.remove();
                                
                                // Check if cart is empty after removal
                                const remainingItems = document.querySelectorAll('.cart-item');
                                if (remainingItems.length === 0) {
                                    location.reload(); // Reload to show empty cart state
                                } else {
                                    // Otherwise update totals
                                    updateCartCountDisplay(remainingItems.length);
                                    // We'll get total from server response
                                    if (data.cartTotal) {
                                        updateCartTotalDisplay(data.cartTotal);
                                    }
                                }
                            }, 500);
                        } else {
                            // Fallback if animation doesn't work
                            location.reload();
                        }
                        
                        showMessage('Sản phẩm đã được xóa khỏi giỏ hàng', 'success');
                    } else {
                        // If failed but we can retry
                        if (retryCount < maxRetries) {
                            retryCount++;
                            console.log(`Retry attempt ${retryCount} for deleting item ${itemId}`);
                            setTimeout(attemptDelete, 1000); // Wait 1 second before retry
                            return;
                        }
                        
                        // Restore opacity if all retries failed
                        if (cartItem) {
                            cartItem.style.opacity = '1';
                        }
                        showMessage(data.message || 'Không thể xóa sản phẩm', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    
                    // If error but we can retry
                    if (retryCount < maxRetries) {
                        retryCount++;
                        console.log(`Retry attempt ${retryCount} for deleting item ${itemId} after error`);
                        setTimeout(attemptDelete, 1000); // Wait 1 second before retry
                        return;
                    }
                    
                    // Restore opacity if all retries failed
                    if (cartItem) {
                        cartItem.style.opacity = '1';
                    }
                    showMessage(error.message || 'Không thể xóa sản phẩm', 'error');
                })
                .finally(() => {
                    if (retryCount >= maxRetries || !retryCount) {
                        hideLoading();
                    }
                });
        }
        
        // Start the deletion process
        attemptDelete();
    }

    // Hiển thị thông báo
    function showMessage(message, type = 'info') {
        // Make the function globally available
        window.showMessage = window.showMessage || showMessage;
        
        // Xóa thông báo cũ nếu có
        const existingAlerts = document.querySelectorAll('.toast-notification');
        existingAlerts.forEach(alert => {
            // Don't remove immediately, just prepare it to fade out
            if (!alert.classList.contains('toast-removing')) {
                alert.classList.add('toast-removing');
                setTimeout(() => alert.remove(), 300);
            }
        });

        // Create a more modern toast notification
        const toast = document.createElement('div');
        
        // Determine icon based on type
        let icon = 'info-circle';
        if (type === 'success') icon = 'check-circle';
        if (type === 'error') icon = 'exclamation-circle';
        if (type === 'warning') icon = 'exclamation-triangle';
        
        toast.className = `toast-notification toast-${type === 'error' ? 'danger' : type}`;
        toast.innerHTML = `
            <div class="toast-content">
                <i class="fas fa-${icon} toast-icon"></i>
                <div class="toast-message">${message}</div>
                <button type="button" class="toast-close" aria-label="Close">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="toast-progress"></div>
        `;

        // Add toast container if it doesn't exist
        let toastContainer = document.querySelector('.toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.className = 'toast-container';
            document.body.appendChild(toastContainer);
        }
        
        // Add toast to container
        toastContainer.appendChild(toast);
        
        // Add animation class after a small delay to trigger CSS transition
        setTimeout(() => {
            toast.classList.add('toast-visible');
        }, 10);
        
        // Add event listener to close button
        const closeBtn = toast.querySelector('.toast-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                toast.classList.add('toast-removing');
                setTimeout(() => toast.remove(), 300);
            });
        }

        // Auto hide after 5 seconds
        setTimeout(() => {
            if (document.body.contains(toast) && !toast.classList.contains('toast-removing')) {
                toast.classList.add('toast-removing');
                setTimeout(() => {
                    if (document.body.contains(toast)) {
                        toast.remove();
                    }
                }, 300);
            }
        }, 5000);
    }

    // Hiển thị loading
    function showLoading() {
        let loadingOverlay = document.querySelector('.loading-overlay');
        if (!loadingOverlay) {
            loadingOverlay = document.createElement('div');
            loadingOverlay.className = 'loading-overlay';
            loadingOverlay.innerHTML = `
                <div class="loading-content">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <p>Đang xử lý...</p>
                </div>
            `;
            document.body.appendChild(loadingOverlay);
        }
        loadingOverlay.style.display = 'flex';
    }

    // Ẩn loading
    function hideLoading() {
        const loadingOverlay = document.querySelector('.loading-overlay');
        if (loadingOverlay) {
            loadingOverlay.style.display = 'none';
        }
    }
    
    // Refresh cart contents without full page reload
    function refreshCart() {
        showLoading();
        
        fetch('/api/cart/get', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể tải lại giỏ hàng');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    // Update cart total
                    updateCartTotalDisplay(data.cartTotal);
                    updateCartCountDisplay(data.cartCount || 0);
                    
                    // If there are items but our display is empty, reload the page
                    const currentItems = document.querySelectorAll('.cart-item');
                    
                    if ((data.cartCount > 0 && currentItems.length === 0) || 
                        (data.cartCount === 0 && currentItems.length > 0)) {
                        location.reload();
                        return;
                    }
                    
                    // Otherwise, update each item
                    if (data.items && Array.isArray(data.items)) {
                        data.items.forEach(item => {
                            const cartItemEl = document.querySelector(`.cart-item[data-item-id="${item.id}"]`);
                            if (cartItemEl) {
                                // Update quantity
                                const quantityInput = cartItemEl.querySelector('.quantity-input');
                                if (quantityInput) {
                                    quantityInput.value = item.quantity;
                                }
                                
                                // Update item total
                                const itemTotalEl = cartItemEl.querySelector(`[data-item-total="${item.id}"]`);
                                if (itemTotalEl) {
                                    itemTotalEl.textContent = formatCurrency(item.total);
                                }
                            }
                        });
                        
                        showMessage('Giỏ hàng đã được cập nhật', 'success');
                    }
                } else {
                    showMessage('Không thể tải lại giỏ hàng', 'error');
                }
            })
            .catch(error => {
                console.error('Error refreshing cart:', error);
                showMessage(error.message || 'Lỗi khi tải lại giỏ hàng', 'error');
            })
            .finally(() => {
                hideLoading();
            });
    }
    
    // Update cart count in UI
    function updateCartCountDisplay(count) {
        const cartCountElements = document.querySelectorAll('.cart-count');
        cartCountElements.forEach(element => {
            element.textContent = count.toString();
        });
    }
    
    // Update cart total in UI
    function updateCartTotalDisplay(total) {
        const formattedTotal = formatCurrency(total);
        const totalElements = document.querySelectorAll('#cart-total, #finalTotal');
        totalElements.forEach(element => {
            element.textContent = formattedTotal;
        });
    }

    // Checkout
    function checkout() {
        window.location.href = '/checkout';
    }

    // Thêm styles
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

    // Setup event listeners cho các nút quantity
    document.querySelectorAll('.quantity-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            try {
                // Get the item ID from the closest parent with a quantity input
                const container = this.closest('.cart-quantity-selector');
                const quantityInput = container?.querySelector('input.quantity-input');
                
                if (!quantityInput) {
                    console.error('Could not find quantity input');
                    return;
                }
                
                const itemId = parseInt(quantityInput.getAttribute('data-item-id'));
                const currentQuantity = parseInt(quantityInput.value);
                
                if (isNaN(itemId) || isNaN(currentQuantity)) {
                    console.error('Invalid item ID or quantity');
                    return;
                }
                
                // Determine if it's an increase or decrease button
                let newQuantity;
                if (this.classList.contains('increase-btn')) {
                    newQuantity = currentQuantity + 1;
                } else if (this.classList.contains('decrease-btn')) {
                    newQuantity = currentQuantity - 1;
                } else {
                    console.error('Unknown button type');
                    return;
                }
                
                // Call the update function directly
                if (typeof window.updateQuantity === 'function') {
                    window.updateQuantity(itemId, newQuantity);
                } else {
                    console.error('updateQuantity function not found');
                    alert('Lỗi: Không thể cập nhật số lượng, vui lòng tải lại trang');
                }
            } catch (error) {
                console.error('Error handling quantity button click:', error);
                
                // Fallback to onclick attribute if available
                const onclick = this.getAttribute('onclick');
                if (onclick && confirm('Có lỗi xảy ra. Thử lại?')) {
                    try {
                        eval(onclick);
                    } catch (evalError) {
                        console.error('Error executing onclick:', evalError);
                    }
                }
            }
        });
    });

    // Setup event listeners cho các nút xóa
    document.querySelectorAll('.remove-item-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            try {
                // Try to find the item ID from the button or container
                let itemId;
                
                // Method 1: From data attribute
                itemId = this.getAttribute('data-item-id');
                
                // Method 2: From parent container
                if (!itemId) {
                    const cartItem = this.closest('.cart-item');
                    const quantityInput = cartItem?.querySelector('input.quantity-input');
                    if (quantityInput) {
                        itemId = quantityInput.getAttribute('data-item-id');
                    }
                }
                
                // Method 3: From onclick attribute
                if (!itemId) {
                    const onclick = this.getAttribute('onclick');
                    if (onclick) {
                        const matches = onclick.match(/removeItem\((\d+)\)/);
                        if (matches) {
                            itemId = matches[1];
                        }
                    }
                }
                
                if (!itemId) {
                    console.error('Could not determine item ID');
                    return;
                }
                
                // Parse the item ID to an integer
                itemId = parseInt(itemId);
                
                if (isNaN(itemId)) {
                    console.error('Invalid item ID');
                    return;
                }
                
                // Call the remove function directly
                if (typeof window.removeItem === 'function') {
                    window.removeItem(itemId);
                } else {
                    console.error('removeItem function not found');
                    alert('Lỗi: Không thể xóa sản phẩm, vui lòng tải lại trang');
                }
            } catch (error) {
                console.error('Error handling remove button click:', error);
                
                // Fallback to onclick attribute if available
                const onclick = this.getAttribute('onclick');
                if (onclick && confirm('Có lỗi xảy ra. Thử lại?')) {
                    try {
                        eval(onclick);
                    } catch (evalError) {
                        console.error('Error executing onclick:', evalError);
                    }
                }
            }
        });
    });

    // Make functions global so they can be called from HTML onclick
    window.updateQuantity = updateQuantity;
    window.removeItem = removeItem;
    window.checkout = checkout;
    window.showMessage = showMessage;  // Make showMessage available globally
});

// Function để xử lý khi người dùng nhập số lượng trực tiếp
window.updateQuantityFromInput = function(inputElement) {
    const itemId = inputElement.getAttribute('data-item-id');
    const newQuantity = parseInt(inputElement.value);
    
    if (!itemId || isNaN(newQuantity)) {
        console.error('Invalid item ID or quantity');
        return;
    }
    
    // Lấy thông tin max stock nếu có
    const cartItem = inputElement.closest('.cart-item');
    const maxStock = cartItem ? parseInt(cartItem.getAttribute('data-max-stock')) : undefined;
    
    // Validate số lượng trước khi cập nhật
    if (newQuantity < 1) {
        if (confirm('Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?')) {
            // Call global removeItem function
            if (typeof window.removeItem === 'function') {
                window.removeItem(parseInt(itemId));
            } else {
                console.error('removeItem function not found');
                alert('Lỗi: Không thể xóa sản phẩm, vui lòng tải lại trang');
            }
        } else {
            // Khôi phục giá trị cũ
            const hiddenInput = document.querySelector(`input[type="hidden"][data-item-id="${itemId}"]`);
            if (hiddenInput) {
                inputElement.value = hiddenInput.value;
            }
        }
        return;
    }
    
    if (maxStock !== undefined && newQuantity > maxStock) {
        // Use global showMessage if available, otherwise fallback to alert
        if (typeof window.showMessage === 'function') {
            showMessage(`Chỉ còn ${maxStock} sản phẩm trong kho`, 'error');
        } else {
            alert(`Chỉ còn ${maxStock} sản phẩm trong kho`);
        }
        // Khôi phục giá trị cũ
        const hiddenInput = document.querySelector(`input[type="hidden"][data-item-id="${itemId}"]`);
        if (hiddenInput) {
            inputElement.value = hiddenInput.value;
        }
        return;
    }
    
    if (newQuantity > 999) {
        // Use global showMessage if available, otherwise fallback to alert
        if (typeof window.showMessage === 'function') {
            showMessage('Số lượng tối đa là 999', 'error');
        } else {
            alert('Số lượng tối đa là 999');
        }
        // Khôi phục giá trị cũ
        const hiddenInput = document.querySelector(`input[type="hidden"][data-item-id="${itemId}"]`);
        if (hiddenInput) {
            inputElement.value = hiddenInput.value;
        }
        return;
    }
    
    // Call global updateQuantity function
    if (typeof window.updateQuantity === 'function') {
        window.updateQuantity(parseInt(itemId), newQuantity);
    } else {
        console.error('updateQuantity function not found');
        alert('Lỗi: Không thể cập nhật số lượng, vui lòng tải lại trang');
        // Khôi phục giá trị cũ
        const hiddenInput = document.querySelector(`input[type="hidden"][data-item-id="${itemId}"]`);
        if (hiddenInput) {
            inputElement.value = hiddenInput.value;
        }
    }
};
