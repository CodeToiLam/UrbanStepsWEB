package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer orderId;
    private String orderCode;

    private Integer accountId; // null if guest
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    private Status status; // NEW, PROCESSING, APPROVED, REJECTED

    @Column(length = 2000)
    private String reason;
    
    @Column(length = 1000)
    private String adminNote; // Admin's note for approval/rejection

    // Store JSON array of image URLs as NVARCHAR(MAX) to support Unicode and match SQL Server schema
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String imageUrlsJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnRequestItem> items;

    public enum Status { NEW, PROCESSING, APPROVED, REJECTED }
    
    public String getStatusText() {
        switch (status) {
            case NEW: return "Chờ xử lý";
            case PROCESSING: return "Đang xử lý";
            case APPROVED: return "Đã phê duyệt";
            case REJECTED: return "Đã từ chối";
            default: return "Không xác định";
        }
    }
    
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helpers for templates: safely expose images
    public List<String> getImageUrls() {
        try {
            if (imageUrlsJson == null || imageUrlsJson.isBlank()) return java.util.List.of();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(imageUrlsJson, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            return java.util.List.of();
        }
    }

    public String getImages() { // comma separated for legacy template usage
        return String.join(",", getImageUrls());
    }

    // --- Transient helpers: extract structured details from reason suffix "|| META:{...json...}"
    @Transient
    public java.util.Map<String, String> getMeta() {
        try {
            if (reason == null) return java.util.Map.of();
            int idx = reason.indexOf("|| META:");
            if (idx < 0) return java.util.Map.of();
            String json = reason.substring(idx + 8).trim();
            if (json.isEmpty()) return java.util.Map.of();
            ObjectMapper mapper = new ObjectMapper();
            java.util.Map<String, String> map = mapper.readValue(json, new TypeReference<java.util.Map<String,String>>(){});
            return map != null ? map : java.util.Map.of();
        } catch (Exception e) {
            return java.util.Map.of();
        }
    }

    @Transient
    public String getReturnMethodText() {
        String m = getMeta().getOrDefault("returnMethod", "");
        if ("PICKUP".equalsIgnoreCase(m)) return "Hẹn nhân viên đến lấy";
        if ("DROP_OFF".equalsIgnoreCase(m)) return "Gửi tại bưu cục";
        return null;
    }

    @Transient
    public String getPickupAddress() {
        return getMeta().get("pickupAddress");
    }

    @Transient
    public String getRefundMethodText() {
        String m = getMeta().getOrDefault("refundMethod", "");
        if ("BANK".equalsIgnoreCase(m)) return "Chuyển khoản ngân hàng";
        if ("ORIGINAL".equalsIgnoreCase(m)) return "Về phương thức thanh toán ban đầu";
        return null;
    }

    @Transient
    public String getBankName() { return getMeta().get("bankName"); }
    @Transient
    public String getBankAccount() { return getMeta().get("bankAccount"); }
    @Transient
    public String getBankHolder() { return getMeta().get("bankHolder"); }
}
