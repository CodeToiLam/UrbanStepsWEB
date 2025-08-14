package vn.urbansteps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.ReturnRequest;
import vn.urbansteps.model.ReturnRequestItem;
import vn.urbansteps.repository.ReturnRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReturnRequestService {

	private final ReturnRequestRepository repository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Tạo yêu cầu trả hàng mới từ thông tin đơn hàng và danh sách item.
	 */
	@Transactional
	public ReturnRequest createRequest(Integer orderId,
									   String orderCode,
									   Integer accountId,
									   String customerName,
									   String customerEmail,
									   String customerPhone,
									   String reason,
									   List<ReturnRequestItem> items,
									   List<String> imageUrls) {
		if (orderId == null || orderCode == null || orderCode.isBlank()) {
			throw new IllegalArgumentException("Thiếu thông tin đơn hàng");
		}
		if (items == null || items.isEmpty()) {
			throw new IllegalArgumentException("Danh sách sản phẩm trả không được rỗng");
		}

		// Phòng ngừa tạo trùng khi đang có yêu cầu NEW/PROCESSING
		long pending = repository.countByOrderIdAndStatus(orderId, ReturnRequest.Status.NEW)
				+ repository.countByOrderIdAndStatus(orderId, ReturnRequest.Status.PROCESSING);
		if (pending > 0) {
			throw new IllegalStateException("Đơn hàng đang có yêu cầu trả/đổi đang xử lý");
		}

		String imagesJson;
		try {
			imagesJson = objectMapper.writeValueAsString(imageUrls == null ? List.of() : imageUrls);
		} catch (Exception e) {
			// Fallback: lưu chuỗi rỗng nếu convert lỗi
			imagesJson = "[]";
		}

		LocalDateTime now = LocalDateTime.now();
		ReturnRequest request = ReturnRequest.builder()
				.orderId(orderId)
				.orderCode(orderCode)
				.accountId(accountId)
				.customerName(customerName)
				.customerEmail(customerEmail)
				.customerPhone(customerPhone)
				.status(ReturnRequest.Status.NEW)
				.reason(reason)
				.imageUrlsJson(imagesJson)
				.createdAt(now)
				.updatedAt(now)
				.build();

		// Gắn quan hệ 2 chiều cho items
		List<ReturnRequestItem> attach = new ArrayList<>();
		for (ReturnRequestItem it : items) {
			if (it == null) continue;
			ReturnRequestItem copy = new ReturnRequestItem();
			copy.setOrderItemId(it.getOrderItemId());
			copy.setQty(Objects.requireNonNullElse(it.getQty(), 0));
			copy.setReturnRequest(request);
			attach.add(copy);
		}
		request.setItems(attach);

		return repository.save(request);
	}

	/**
	 * Phê duyệt yêu cầu trả hàng
	 */
	@Transactional
	public void approve(Long id) {
		ReturnRequest request = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu trả hàng"));
		if (request.getStatus() == ReturnRequest.Status.REJECTED || request.getStatus() == ReturnRequest.Status.APPROVED) {
			return; // đã kết thúc
		}
		request.setStatus(ReturnRequest.Status.APPROVED);
		request.setUpdatedAt(LocalDateTime.now());
		repository.save(request);
		// TODO: Thực hiện các hành động sau phê duyệt (ghi log, hoàn tiền, v.v.) nếu cần
	}

	/**
	 * Từ chối yêu cầu trả hàng với lý do
	 */
	@Transactional
	public void reject(Long id, String reason) {
		ReturnRequest request = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu trả hàng"));
		request.setStatus(ReturnRequest.Status.REJECTED);
		if (reason != null && !reason.isBlank()) {
			String note = request.getAdminNote();
			request.setAdminNote((note == null || note.isBlank()) ? reason : (note + "\n" + reason));
		}
		request.setUpdatedAt(LocalDateTime.now());
		repository.save(request);
	}
}

