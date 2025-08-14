package vn.urbansteps.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.urbansteps.model.ReturnRequest;

import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
	long countByOrderIdAndStatus(Integer orderId, ReturnRequest.Status status);

	// Override default findById with an entity graph to load items eagerly
	@EntityGraph(attributePaths = {"items"})
	Optional<ReturnRequest> findById(Long id);
    
	// Admin management methods
	Page<ReturnRequest> findByStatus(ReturnRequest.Status status, Pageable pageable);
	Page<ReturnRequest> findByOrderCodeContainingIgnoreCaseAndStatus(String orderCode, ReturnRequest.Status status, Pageable pageable);
	Page<ReturnRequest> findByOrderCodeContainingIgnoreCaseOrCustomerNameContainingIgnoreCaseOrCustomerPhoneContaining(
			String orderCode, String customerName, String customerPhone, Pageable pageable);
    
	// Count methods for statistics
	long countByStatus(ReturnRequest.Status status);

	// Fetch with items to avoid LazyInitializationException and support admin detail/actions
	@Query("select rr from ReturnRequest rr left join fetch rr.items where rr.id = :id")
	Optional<ReturnRequest> findByIdWithItems(@Param("id") Long id);
}
