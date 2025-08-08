package vn.urbansteps.dto;

public enum CartErrorType {
    PRODUCT_NOT_FOUND("Sản phẩm không tồn tại"),
    OUT_OF_STOCK("Sản phẩm đã hết hàng"),
    INSUFFICIENT_STOCK("Không đủ hàng trong kho"),
    INVALID_QUANTITY("Số lượng không hợp lệ"),
    MAXIMUM_QUANTITY("Vượt quá số lượng tối đa cho phép"),
    INVALID_PRICE("Sản phẩm không có giá hợp lệ"),
    PRODUCT_UNAVAILABLE("Sản phẩm tạm thời không khả dụng"),
    ITEM_NOT_FOUND("Sản phẩm không tồn tại trong giỏ hàng"),
    DELETE_FAILED("Không thể xóa sản phẩm khỏi giỏ hàng"),
    QUANTITY_EXCEEDS_LIMIT("Số lượng vượt quá giới hạn cho phép"),
    SERVER_ERROR("Lỗi hệ thống");
    
    private final String message;
    
    CartErrorType(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
