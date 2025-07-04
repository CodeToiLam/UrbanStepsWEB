/**
 * ====== URBANSTEPS - TÌM KIẾM SẢN PHẨM ====== 
 * JavaScript cho trang tìm kiếm sản phẩm
 */

// Hàm thêm sản phẩm vào giỏ hàng
function addToCart(button) {
    const productId = button.getAttribute('data-product-id');
    
    // Hiệu ứng loading khi đang thêm vào giỏ hàng
    const originalText = button.innerHTML;
    button.innerHTML = '<i class="bi bi-arrow-repeat"></i> Đang thêm...';
    button.disabled = true;
    
    // Giả lập thêm vào giỏ hàng (thay thế bằng AJAX call thực tế)
    setTimeout(() => {
        button.innerHTML = '<i class="bi bi-check"></i> Đã thêm';
        button.classList.replace('btn-primary', 'btn-success');
        
        // Reset lại nút sau 2 giây
        setTimeout(() => {
            button.innerHTML = originalText;
            button.classList.replace('btn-success', 'btn-primary');
            button.disabled = false;
        }, 2000);
    }, 1000);
}

// Khởi tạo trang tìm kiếm khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', function() {
    // Lấy từ khóa tìm kiếm từ URL
    const keyword = new URLSearchParams(window.location.search).get('keyword');
    
    // Làm nổi bật từ khóa tìm kiếm trong kết quả
    if (keyword) {
        highlightSearchKeyword(keyword);
    }

    // Khởi tạo tự động submit form khi thay đổi bộ lọc
    initAutoSubmitFilters();
    
    // Khởi tạo các chức năng khác
    initSearchSuggestions();
    initProductCardAnimations();
});

// Hàm làm nổi bật từ khóa tìm kiếm
function highlightSearchKeyword(keyword) {
    const productNames = document.querySelectorAll('.product-name a');
    productNames.forEach(element => {
        const regex = new RegExp(`(${keyword})`, 'gi');
        element.innerHTML = element.innerHTML.replace(regex, '<mark>$1</mark>');
    });
}

// Khởi tạo tự động submit form khi thay đổi bộ lọc
function initAutoSubmitFilters() {
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
}

// Khởi tạo gợi ý tìm kiếm
function initSearchSuggestions() {
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        // Có thể thêm logic gợi ý tìm kiếm ở đây
        searchInput.addEventListener('input', function() {
            // TODO: Thêm logic gợi ý tìm kiếm realtime
        });
    }
}

// Khởi tạo hiệu ứng cho card sản phẩm
function initProductCardAnimations() {
    const productCards = document.querySelectorAll('.product-card');
    
    // Thêm hiệu ứng xuất hiện từng card
    productCards.forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
        card.classList.add('animate-fadeInUp');
    });
}

// Hàm cập nhật sắp xếp
function updateSort(sortValue) {
    const currentUrl = new URL(window.location);
    if (sortValue) {
        currentUrl.searchParams.set('sapXep', sortValue);
    } else {
        currentUrl.searchParams.delete('sapXep');
    }
    window.location.href = currentUrl.toString();
}
