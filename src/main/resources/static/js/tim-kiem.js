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
    
    // Thực hiện AJAX call để thêm vào giỏ hàng
    fetch('/api/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `sanPhamChiTietId=${productId}&soLuong=1`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            button.innerHTML = '<i class="bi bi-check"></i> Đã thêm';
            button.classList.replace('btn-primary', 'btn-success');
            
            // Hiện thông báo thành công
            showMessage('Đã thêm sản phẩm vào giỏ hàng', 'success');
            
            // Cập nhật số lượng giỏ hàng nếu có
            if (data.cartCount && document.querySelector('.cart-count')) {
                document.querySelector('.cart-count').textContent = data.cartCount;
            }
            
            // Reset lại nút sau 2 giây
            setTimeout(() => {
                button.innerHTML = originalText;
                button.classList.replace('btn-success', 'btn-primary');
                button.disabled = false;
            }, 2000);
        } else {
            button.innerHTML = '<i class="bi bi-x"></i> Lỗi';
            button.classList.replace('btn-primary', 'btn-danger');
            
            // Hiện thông báo lỗi
            showMessage(data.message || 'Không thể thêm vào giỏ hàng', 'danger');
            
            // Reset lại nút sau 2 giây
            setTimeout(() => {
                button.innerHTML = originalText;
                button.classList.replace('btn-danger', 'btn-primary');
                button.disabled = false;
            }, 2000);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        button.innerHTML = '<i class="bi bi-x"></i> Lỗi';
        button.classList.replace('btn-primary', 'btn-danger');
        
        // Hiện thông báo lỗi
        showMessage('Có lỗi xảy ra khi thêm vào giỏ hàng', 'danger');
        
        // Reset lại nút sau 2 giây
        setTimeout(() => {
            button.innerHTML = originalText;
            button.classList.replace('btn-danger', 'btn-primary');
            button.disabled = false;
        }, 2000);
    });
}

// Hiển thị thông báo
function showMessage(message, type = 'info') {
    // Kiểm tra nếu đã có thông báo thì xóa đi
    const existingAlert = document.querySelector('.search-alert');
    if (existingAlert) {
        existingAlert.remove();
    }
    
    // Tạo thông báo mới
    const alertEl = document.createElement('div');
    alertEl.className = `alert alert-${type} alert-dismissible fade show search-alert`;
    alertEl.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    // Chèn vào đầu danh sách sản phẩm
    const productList = document.querySelector('.product-grid');
    if (productList && productList.parentNode) {
        productList.parentNode.insertBefore(alertEl, productList);
    } else {
        document.querySelector('main').insertBefore(alertEl, document.querySelector('main').firstChild);
    }
    
    // Tự động ẩn sau 3 giây
    setTimeout(() => {
        if (alertEl.parentNode) {
            alertEl.classList.remove('show');
            setTimeout(() => alertEl.remove(), 300);
        }
    }, 3000);
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
    const productNames = document.querySelectorAll('.product-name, .product-name span, .product-name a');
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
    // Đã được xử lý bởi search-suggest.js dùng chung cho header và trang
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
