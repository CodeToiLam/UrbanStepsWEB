// Admin Order Detail JavaScript Functions
function updateOrderStatus(orderId, newStatus) {
    if (!confirm('Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng này?')) {
        return;
    }
    
    const formData = new FormData();
    formData.append('orderId', orderId);
    formData.append('newStatus', newStatus);
    
    fetch('/admin/order/update-status', {
        method: 'POST',
        body: formData
    })
    .then(response => response.text())
    .then(data => {
        if (data.startsWith('success:')) {
            alert(data.substring(8));
            location.reload();
        } else if (data.startsWith('error:')) {
            alert(data.substring(6));
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi cập nhật trạng thái');
    });
}

// Confirm cancel order
function confirmCancelOrder() {
    return confirm('Bạn có chắc chắn muốn hủy đơn hàng này không? Hành động này không thể hoàn tác.');
}

// Confirm refund item
function confirmRefundItem() {
    return confirm('Xác nhận hoàn trả sản phẩm này?');
}

// Confirm return all
function confirmReturnAll() {
    return confirm('Hoàn trả toàn bộ các sản phẩm của đơn hàng này?');
}
