package vn.urbansteps.dto;

import java.math.BigDecimal;

public class CartOperationResult {
    private boolean success;
    private CartErrorType errorType;
    private String customMessage;
    private BigDecimal cartTotal;
    private int cartCount;
    private BigDecimal itemTotal;
    private Integer relatedProductId; // For errors related to specific products
    
    // Constructors
    public CartOperationResult() {
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public CartErrorType getErrorType() {
        return errorType;
    }
    
    public void setErrorType(CartErrorType errorType) {
        this.errorType = errorType;
    }
    
    public String getCustomMessage() {
        return customMessage;
    }
    
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
    
    public BigDecimal getCartTotal() {
        return cartTotal;
    }
    
    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }
    
    public int getCartCount() {
        return cartCount;
    }
    
    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }
    
    public BigDecimal getItemTotal() {
        return itemTotal;
    }
    
    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }
    
    // Factory methods
    public static CartOperationResult success(BigDecimal cartTotal, int cartCount) {
        CartOperationResult result = new CartOperationResult();
        result.setSuccess(true);
        result.setCartTotal(cartTotal);
        result.setCartCount(cartCount);
        return result;
    }
    
    public static CartOperationResult success(BigDecimal cartTotal, int cartCount, BigDecimal itemTotal) {
        CartOperationResult result = success(cartTotal, cartCount);
        result.setItemTotal(itemTotal);
        return result;
    }
    
    public static CartOperationResult error(CartErrorType errorType) {
        CartOperationResult result = new CartOperationResult();
        result.setSuccess(false);
        result.setErrorType(errorType);
        return result;
    }
    
    public static CartOperationResult error(CartErrorType errorType, String customMessage) {
        CartOperationResult result = error(errorType);
        result.setCustomMessage(customMessage);
        return result;
    }
    
    public static CartOperationResult error(CartErrorType errorType, String customMessage, Integer relatedProductId) {
        CartOperationResult result = error(errorType, customMessage);
        result.setRelatedProductId(relatedProductId);
        return result;
    }
    
    public Integer getRelatedProductId() {
        return relatedProductId;
    }
    
    public void setRelatedProductId(Integer relatedProductId) {
        this.relatedProductId = relatedProductId;
    }
    
    public String getMessage() {
        return customMessage != null ? customMessage : (errorType != null ? errorType.getMessage() : null);
    }
}
