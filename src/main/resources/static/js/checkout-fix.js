// Fix checkout process for BuyNow functionality
document.addEventListener('DOMContentLoaded', function() {
    // Check if we're on the product detail page
    const addToCartBtn = document.getElementById('add-to-cart');
    const buyNowBtn = document.getElementById('buy-now');
    
    if (buyNowBtn) {
        // Replace the existing buyNow handler
        buyNowBtn.addEventListener('click', function(event) {
            // Prevent default form action
            event.preventDefault();
            
            // Get selected size and color
            const selectedSize = window.selectedSize;
            const selectedColor = window.selectedColor;
            
            if (!selectedSize || !selectedColor) {
                showAlert('Vui lòng chọn kích cỡ và màu sắc trước khi mua ngay!');
                return;
            }
            
            const quantityInput = document.getElementById('quantity');
            const quantity = quantityInput ? parseInt(quantityInput.value) : 1;
            
            // Get product variant ID
            const sanPhamChiTietId = getSanPhamChiTietId(selectedSize, selectedColor);
            
            if (!sanPhamChiTietId) {
                showAlert('Không tìm thấy sản phẩm với kích cỡ và màu sắc đã chọn!');
                return;
            }
            
            // Store the selected product ID directly in localStorage to ensure it's preserved
            localStorage.setItem('buyNowProductId', sanPhamChiTietId);
            localStorage.setItem('buyNowQuantity', quantity);
            localStorage.setItem('buyNowTimestamp', Date.now()); // Add timestamp to prevent stale data
            
            // Redirect to checkout with explicit parameters
            window.location.href = `/checkout?buyNow=true&buyNowItemId=${sanPhamChiTietId}&buyNowQuantity=${quantity}`;
        });
        
        console.log('BuyNow button handler replaced with fixed version');
    }
    
    // Helper function to show alerts (reused from original code)
    function showAlert(message) {
        if (typeof toastr !== 'undefined') {
            toastr.info(message);
        } else {
            alert(message);
        }
        console.log('Alert shown:', message);
    }
});
