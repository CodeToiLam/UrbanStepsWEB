// Search functionality
function addToCart(button) {
    const productId = button.getAttribute('data-product-id');
    
    // Hiệu ứng loading
    const originalText = button.innerHTML;
    button.innerHTML = '<i class="bi bi-arrow-repeat"></i> Đang thêm...';
    button.disabled = true;
    
    // Giả lập thêm vào giỏ hàng
    setTimeout(() => {
        button.innerHTML = '<i class="bi bi-check"></i> Đã thêm';
        button.classList.replace('btn-primary', 'btn-success');
        
        // Reset sau 2 giây
        setTimeout(() => {
            button.innerHTML = originalText;
            button.classList.replace('btn-success', 'btn-primary');
            button.disabled = false;
        }, 2000);
    }, 1000);
}

// Highlight search keyword trong kết quả
document.addEventListener('DOMContentLoaded', function() {
    const keyword = new URLSearchParams(window.location.search).get('keyword');
    if (keyword) {
        const productNames = document.querySelectorAll('.product-name a');
        productNames.forEach(element => {
            const regex = new RegExp(`(${keyword})`, 'gi');
            element.innerHTML = element.innerHTML.replace(regex, '<mark>$1</mark>');
        });
    }

    // Auto submit form on filter change
    const thuongHieuSelect = document.getElementById('thuongHieu');
    const sapXepSelect = document.getElementById('sapXep');
    
    if (thuongHieuSelect) {
        thuongHieuSelect.addEventListener('change', function() {
            this.form.submit();
        });
    }
    
    if (sapXepSelect) {
        sapXepSelect.addEventListener('change', function() {
            this.form.submit();
        });
    }
});
