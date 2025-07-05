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
});