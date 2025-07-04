// Xác nhận xóa sản phẩm
function confirmDelete(event) {
    if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
        event.preventDefault();
    }
}

// Gắn sự kiện cho các liên kết xóa
document.addEventListener('DOMContentLoaded', function() {
    const deleteLinks = document.querySelectorAll('a[href*="delete"]');
    deleteLinks.forEach(link => {
        link.addEventListener('click', confirmDelete);
    });
});