
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý xác nhận xóa
    function confirmDelete(event) {
        if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
            event.preventDefault();
        }
    }

    // Thêm sự kiện cho các nút xóa
    const deleteButtons = document.querySelectorAll('.admin-btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', confirmDelete);
    });

    // Thêm sự kiện cho các nút xóa phiếu giảm giá
    const deleteDiscountButtons = document.querySelectorAll('.delete-discount');
    deleteDiscountButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!confirm('Bạn có chắc muốn xóa phiếu giảm giá này?')) {
                event.preventDefault();
            }
        });
    });

    // Xử lý validation form
    const forms = document.querySelectorAll('.needs-validation');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Đảm bảo các icon trong nút hành động không gây xung đột
    document.querySelectorAll('.admin-icon-btn, .admin-btn, .logout-btn').forEach(button => {
        button.addEventListener('click', function(event) {
            // Ngăn chặn hành vi mặc định nếu icon được click
            if (event.target.tagName === 'I') {
                event.stopPropagation();
            }
        });
    });
});
