/**
 * ====== URBANSTEPS - CHI TIẾT SẢN PHẨM ====== 
 * JavaScript cho trang chi tiết sản phẩm với gallery chuyên nghiệp
 */

document.addEventListener('DOMContentLoaded', function () {
    // Debug: Log all data received from server
    console.log('=== PRODUCT DETAIL DEBUG ===');
    if (typeof window.productId !== 'undefined') {
        console.log('Product ID:', window.productId);
    } else {
        console.log('Product ID: UNDEFINED');
    }
    
    if (typeof window.variants !== 'undefined') {
        console.log('Variants:', window.variants);
        console.log('Variants count:', window.variants.length);
    } else {
        console.log('Variants: UNDEFINED');
    }
    
    if (typeof window.tonKhoMap !== 'undefined') {
        console.log('Stock Map:', window.tonKhoMap);
    } else {
        console.log('Stock Map: UNDEFINED');
    }
    
    if (typeof window.availableSizes !== 'undefined') {
        console.log('Available Sizes:', window.availableSizes);
    } else {
        console.log('Available Sizes: UNDEFINED');
    }
    
    if (typeof window.availableColors !== 'undefined') {
        console.log('Available Colors:', window.availableColors);
    } else {
        console.log('Available Colors: UNDEFINED');
    }
    console.log('=============================');

    // Dữ liệu tồn kho từ server
    let stockData = {};
    let selectedSize = null;
    let selectedColor = null;
    let currentImageIndex = 0;
    let totalImages = 0;
    let galleryImages = [];

    // Hàm khởi tạo chính
    function initProductDetailPage() {
        try {
            initGallery();
            initSizeColorHandlers();
            initQuantityHandlers();
            initActionButtons();
            initImageZoom();
            initLightbox();
            initKeyboardNavigation();
        } catch (error) {
            console.log('Lỗi khởi tạo trang chi tiết:', error);
        }
    }

    // ====== GALLERY FUNCTIONS ======
    function initGallery() {
        const thumbnails = document.querySelectorAll('.thumbnail-item');
        const mainImage = document.getElementById('main-product-image');
        
        if (thumbnails.length === 0) return;
        
        totalImages = thumbnails.length;
        galleryImages = Array.from(thumbnails).map(thumb => ({
            src: thumb.getAttribute('data-image'),
            index: parseInt(thumb.getAttribute('data-index'))
        }));
        
        // Khởi tạo thumbnail đầu tiên
        if (thumbnails.length > 0) {
            thumbnails[0].classList.add('active');
            currentImageIndex = 0;
            updateImageCounter();
        }
        
        // Khởi tạo scroll navigation cho thumbnails
        if (thumbnails.length > 5) {
            initThumbnailScrollNavigation();
        }
    }

    // Hàm thay đổi ảnh chính với hiệu ứng loading
    window.changeMainImage = function(thumbnailElement) {
        try {
            if (!thumbnailElement) return;
            
            const imagePath = thumbnailElement.getAttribute('data-image');
            const imageIndex = parseInt(thumbnailElement.getAttribute('data-index'));
            
            if (!imagePath) return;
            
            // Show loading overlay
            showImageLoading(true);
            
            // Preload image
            const newImg = new Image();
            newImg.onload = function() {
                const mainImage = document.getElementById('main-product-image');
                if (mainImage) {
                    // Smooth transition
                    mainImage.style.opacity = '0';
                    setTimeout(() => {
                        mainImage.src = imagePath;
                        mainImage.style.opacity = '1';
                        showImageLoading(false);
                    }, 150);
                }
                
                // Update active thumbnail
                updateActiveThumbnail(thumbnailElement);
                
                // Update current index
                currentImageIndex = imageIndex;
                updateImageCounter();
            };
            
            newImg.onerror = function() {
                console.log('Lỗi tải ảnh:', imagePath);
                showImageLoading(false);
            };
            
            newImg.src = imagePath;
            
        } catch (error) {
            console.log('Lỗi khi thay đổi ảnh chính:', error);
            showImageLoading(false);
        }
    };

    // Hiển thị/ẩn loading overlay
    function showImageLoading(show) {
        const loadingOverlay = document.getElementById('imageLoadingOverlay');
        if (loadingOverlay) {
            if (show) {
                loadingOverlay.classList.add('show');
            } else {
                loadingOverlay.classList.remove('show');
            }
        }
    }

    // Cập nhật thumbnail active
    function updateActiveThumbnail(activeThumbnail) {
        const thumbnails = document.querySelectorAll('.thumbnail-item');
        thumbnails.forEach(thumb => thumb.classList.remove('active'));
        if (activeThumbnail) {
            activeThumbnail.classList.add('active');
        }
    }

    // Cập nhật counter ảnh
    function updateImageCounter() {
        const counterElement = document.getElementById('currentImageIndex');
        if (counterElement) {
            counterElement.textContent = currentImageIndex + 1;
        }
    }

    // ====== THUMBNAIL SCROLL NAVIGATION ======
    function initThumbnailScrollNavigation() {
        const scrollContainer = document.getElementById('thumbnailScrollContainer');
        const navUp = document.getElementById('thumbNavUp');
        const navDown = document.getElementById('thumbNavDown');
        
        if (!scrollContainer || !navUp || !navDown) return;
        
        // Update navigation buttons
        function updateNavButtons() {
            const { scrollTop, scrollHeight, clientHeight } = scrollContainer;
            navUp.disabled = scrollTop <= 0;
            navDown.disabled = scrollTop >= scrollHeight - clientHeight;
        }
        
        // Scroll event listener
        scrollContainer.addEventListener('scroll', updateNavButtons);
        
        // Initial update
        updateNavButtons();
    }

    // Scroll thumbnails up/down
    window.scrollThumbnails = function(direction) {
        const scrollContainer = document.getElementById('thumbnailScrollContainer');
        if (!scrollContainer) return;
        
        const scrollAmount = 120; // Height of thumbnail + gap
        const currentScroll = scrollContainer.scrollTop;
        
        if (direction === 'up') {
            scrollContainer.scrollTo({
                top: currentScroll - scrollAmount,
                behavior: 'smooth'
            });
        } else if (direction === 'down') {
            scrollContainer.scrollTo({
                top: currentScroll + scrollAmount,
                behavior: 'smooth'
            });
        }
    };

    // ====== LIGHTBOX FUNCTIONALITY ======
    function initLightbox() {
        // Create lightbox overlay
        const lightboxHTML = `
            <div class="lightbox-overlay" id="lightboxOverlay">
                <div class="lightbox-container">
                    <img class="lightbox-image" id="lightboxImage" src="" alt="">
                    <button class="lightbox-nav prev" id="lightboxPrev">
                        <i class="bi bi-chevron-left"></i>
                    </button>
                    <button class="lightbox-nav next" id="lightboxNext">
                        <i class="bi bi-chevron-right"></i>
                    </button>
                    <button class="lightbox-close" id="lightboxClose">
                        <i class="bi bi-x"></i>
                    </button>
                    <div class="lightbox-counter" id="lightboxCounter">
                        <span id="lightboxCurrentIndex">1</span> / <span id="lightboxTotalCount">1</span>
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', lightboxHTML);
        
        // Bind events
        document.getElementById('lightboxClose').addEventListener('click', closeLightbox);
        document.getElementById('lightboxPrev').addEventListener('click', () => navigateLightbox(-1));
        document.getElementById('lightboxNext').addEventListener('click', () => navigateLightbox(1));
        document.getElementById('lightboxOverlay').addEventListener('click', function(e) {
            if (e.target === this) closeLightbox();
        });
    }

    // Open lightbox
    window.openLightbox = function(startIndex = currentImageIndex) {
        if (galleryImages.length === 0) return;
        
        currentImageIndex = startIndex;
        const lightboxOverlay = document.getElementById('lightboxOverlay');
        const lightboxImage = document.getElementById('lightboxImage');
        const lightboxCurrentIndex = document.getElementById('lightboxCurrentIndex');
        const lightboxTotalCount = document.getElementById('lightboxTotalCount');
        
        lightboxImage.src = galleryImages[currentImageIndex].src;
        lightboxCurrentIndex.textContent = currentImageIndex + 1;
        lightboxTotalCount.textContent = galleryImages.length;
        
        lightboxOverlay.classList.add('show');
        document.body.style.overflow = 'hidden';
    };

    // Close lightbox
    function closeLightbox() {
        const lightboxOverlay = document.getElementById('lightboxOverlay');
        lightboxOverlay.classList.remove('show');
        document.body.style.overflow = '';
    }

    // Navigate lightbox
    function navigateLightbox(direction) {
        currentImageIndex = (currentImageIndex + direction + galleryImages.length) % galleryImages.length;
        
        const lightboxImage = document.getElementById('lightboxImage');
        const lightboxCurrentIndex = document.getElementById('lightboxCurrentIndex');
        
        lightboxImage.src = galleryImages[currentImageIndex].src;
        lightboxCurrentIndex.textContent = currentImageIndex + 1;
        
        // Update main gallery
        const targetThumbnail = document.querySelector(`[data-index="${currentImageIndex}"]`);
        if (targetThumbnail) {
            changeMainImage(targetThumbnail);
        }
    }

    // ====== IMAGE ZOOM FUNCTIONALITY ======
    function initImageZoom() {
        const mainImage = document.getElementById('main-product-image');
        if (!mainImage) return;

        let isZoomed = false;
        
        mainImage.addEventListener('click', function(e) {
            e.stopPropagation();
            
            if (!isZoomed) {
                // Zoom in
                mainImage.style.transform = 'scale(2)';
                mainImage.style.cursor = 'zoom-out';
                mainImage.style.zIndex = '1000';
                mainImage.style.position = 'relative';
                isZoomed = true;
                
                // Create zoom overlay
                const zoomOverlay = document.createElement('div');
                zoomOverlay.style.cssText = `
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0,0,0,0.8);
                    z-index: 999;
                    cursor: zoom-out;
                `;
                zoomOverlay.id = 'zoom-overlay';
                
                zoomOverlay.addEventListener('click', resetZoom);
                document.body.appendChild(zoomOverlay);
                
                // Disable body scroll
                document.body.style.overflow = 'hidden';
            } else {
                resetZoom();
            }
        });
        
        function resetZoom() {
            mainImage.style.transform = 'scale(1)';
            mainImage.style.cursor = 'zoom-in';
            mainImage.style.zIndex = 'auto';
            mainImage.style.position = 'static';
            isZoomed = false;
            
            const zoomOverlay = document.getElementById('zoom-overlay');
            if (zoomOverlay) {
                zoomOverlay.remove();
            }
            
            document.body.style.overflow = '';
        }
        
        // Add zoom hint
        mainImage.style.cursor = 'zoom-in';
        mainImage.title = 'Click để phóng to ảnh';
    }

    // ====== KEYBOARD NAVIGATION ======
    function initKeyboardNavigation() {
        document.addEventListener('keydown', function(e) {
            if (galleryImages.length <= 1) return;
            
            // If lightbox is open
            const lightboxOverlay = document.getElementById('lightboxOverlay');
            if (lightboxOverlay && lightboxOverlay.classList.contains('show')) {
                if (e.key === 'ArrowLeft') {
                    e.preventDefault();
                    navigateLightbox(-1);
                } else if (e.key === 'ArrowRight') {
                    e.preventDefault();
                    navigateLightbox(1);
                } else if (e.key === 'Escape') {
                    e.preventDefault();
                    closeLightbox();
                }
                return;
            }
            
            // Gallery navigation
            if (e.key === 'ArrowLeft') {
                e.preventDefault();
                navigateGallery(-1);
            } else if (e.key === 'ArrowRight') {
                e.preventDefault();
                navigateGallery(1);
            } else if (e.key >= '1' && e.key <= '9') {
                const index = parseInt(e.key) - 1;
                if (index < galleryImages.length) {
                    const targetThumbnail = document.querySelector(`[data-index="${index}"]`);
                    if (targetThumbnail) {
                        changeMainImage(targetThumbnail);
                    }
                }
            }
        });
    }

    // Navigate gallery with keyboard
    function navigateGallery(direction) {
        const newIndex = (currentImageIndex + direction + galleryImages.length) % galleryImages.length;
        const targetThumbnail = document.querySelector(`[data-index="${newIndex}"]`);
        if (targetThumbnail) {
            changeMainImage(targetThumbnail);
        }
    }

    // ====== SHARE FUNCTIONALITY ======
    window.shareImage = function() {
        if (navigator.share) {
            navigator.share({
                title: 'Ảnh sản phẩm',
                text: 'Xem ảnh sản phẩm này',
                url: window.location.href
            });
        } else {
            // Fallback: copy URL to clipboard
            navigator.clipboard.writeText(window.location.href).then(() => {
                alert('Đã copy link ảnh vào clipboard!');
            });
        }
    };
    // ====== SIZE, COLOR, QUANTITY HANDLERS ======
    function initSizeColorHandlers() {
        const sizeInputs = document.querySelectorAll('input[name="size"]');
        sizeInputs.forEach(input => {
            input.addEventListener('change', updateStockInfo);
        });

        const colorInputs = document.querySelectorAll('input[name="color"]');
        colorInputs.forEach(input => {
            input.addEventListener('change', updateStockInfo);
        });
        
        // Auto-trigger updateStockInfo to handle single color case
        setTimeout(() => {
            updateStockInfo();
        }, 100);
    }

    function updateStockInfo() {
        console.log('=== UPDATE STOCK INFO ===');
        const sizeChecked = document.querySelector('input[name="size"]:checked');
        const colorChecked = document.querySelector('input[name="color"]:checked');
        
        console.log('Size checked:', sizeChecked ? sizeChecked.value : 'none');
        console.log('Color checked:', colorChecked ? colorChecked.value : 'none');
        console.log('Current stockData:', stockData);
        console.log('Window tonKhoMap:', window.tonKhoMap);

        // Auto-select first color if only one color is available and none is selected
        if (!colorChecked && window.availableColors && window.availableColors.length === 1) {
            const firstColorInput = document.querySelector('input[name="color"]');
            if (firstColorInput) {
                firstColorInput.checked = true;
                console.log('Auto-selected single color:', window.availableColors[0]);
            }
        }

        // Re-check after auto-selection
        const updatedSizeChecked = document.querySelector('input[name="size"]:checked');
        const updatedColorChecked = document.querySelector('input[name="color"]:checked');

        if (updatedSizeChecked && updatedColorChecked) {
            selectedSize = updatedSizeChecked.value;
            selectedColor = updatedColorChecked.value;

            // Thử nhiều cách để lấy stock quantity
            let stockQuantity = 0;
            
            // Cách 1: Từ window.tonKhoMap (nested structure: {size: {color: quantity}})
            if (window.tonKhoMap && window.tonKhoMap[selectedSize] && window.tonKhoMap[selectedSize][selectedColor]) {
                stockQuantity = window.tonKhoMap[selectedSize][selectedColor];
                console.log('Stock from window.tonKhoMap (nested):', stockQuantity);
            }
            
            // Cách 2: Từ stockData (flat structure hoặc nested)
            if (stockQuantity === 0 && stockData) {
                // Thử cấu trúc nested
                if (stockData[selectedSize] && stockData[selectedSize][selectedColor]) {
                    stockQuantity = stockData[selectedSize][selectedColor];
                    console.log('Stock from stockData (nested):', stockQuantity);
                }
                // Thử cấu trúc flat
                else {
                    const flatKey = selectedSize + '_' + selectedColor;
                    stockQuantity = stockData[flatKey] || 0;
                    console.log('Stock from stockData (flat) with key', flatKey, ':', stockQuantity);
                }
            }
            
            // Cách 3: Từ window.variants nếu có
            if (stockQuantity === 0 && window.variants && Array.isArray(window.variants)) {
                const variant = window.variants.find(v => {
                    // Handle different data structures
                    const variantSize = v.kichCo ? (v.kichCo.tenKichCo || v.kichCo) : '';
                    const variantColor = v.mauSac ? (v.mauSac.tenMauSac || v.mauSac) : '';
                    return variantSize === selectedSize && variantColor === selectedColor;
                });
                if (variant) {
                    stockQuantity = variant.soLuong || 0;
                    console.log('Stock from variants:', stockQuantity, 'variant:', variant);
                }
            }

            console.log('Final stock quantity:', stockQuantity);
            console.log('Stock calculation summary:');
            console.log('- Selected size:', selectedSize);
            console.log('- Selected color:', selectedColor);
            console.log('- Final quantity:', stockQuantity);
            
            // Update stock display
            const stockQuantityElement = document.getElementById('stock-quantity');
            if (stockQuantityElement) {
                stockQuantityElement.textContent = stockQuantity > 0 ? stockQuantity : 'Hết hàng';
            }

            // Update buttons
            const addToCartBtn = document.getElementById('add-to-cart');
            const buyNowBtn = document.getElementById('buy-now');

            if (stockQuantity > 0) {
                if (addToCartBtn) {
                    addToCartBtn.disabled = false;
                    addToCartBtn.textContent = 'Thêm vào giỏ hàng';
                    addToCartBtn.classList.remove('btn-secondary');
                    addToCartBtn.classList.add('btn-primary');
                }
                if (buyNowBtn) {
                    buyNowBtn.disabled = false;
                    buyNowBtn.textContent = 'Mua ngay';
                    buyNowBtn.classList.remove('btn-secondary');
                    buyNowBtn.classList.add('btn-warning');
                }
            } else {
                if (addToCartBtn) {
                    addToCartBtn.disabled = true;
                    addToCartBtn.textContent = 'Hết hàng';
                    addToCartBtn.classList.remove('btn-primary');
                    addToCartBtn.classList.add('btn-secondary');
                }
                if (buyNowBtn) {
                    buyNowBtn.disabled = true;
                    buyNowBtn.textContent = 'Hết hàng';
                    buyNowBtn.classList.remove('btn-warning');
                    buyNowBtn.classList.add('btn-secondary');
                }
            }

            // Update quantity input
            const quantityInput = document.getElementById('quantity');
            if (quantityInput) {
                quantityInput.max = stockQuantity;
                if (parseInt(quantityInput.value) > stockQuantity) {
                    quantityInput.value = Math.max(1, stockQuantity);
                }
            }
        } else {
            // Reset to default state when no size/color selected
            const stockQuantityElement = document.getElementById('stock-quantity');
            if (stockQuantityElement) {
                stockQuantityElement.textContent = 'Chọn kích cỡ và màu';
            }

            const addToCartBtn = document.getElementById('add-to-cart');
            const buyNowBtn = document.getElementById('buy-now');

            if (addToCartBtn) {
                addToCartBtn.disabled = true;
                addToCartBtn.textContent = 'Chọn kích cỡ và màu';
            }
            if (buyNowBtn) {
                buyNowBtn.disabled = true;
                buyNowBtn.textContent = 'Chọn kích cỡ và màu';
            }
        }
    }

    function initQuantityHandlers() {
        const decreaseBtn = document.getElementById('decrease-quantity');
        const increaseBtn = document.getElementById('increase-quantity');
        const quantityInput = document.getElementById('quantity');
        const quantitySelector = document.querySelector('.quantity-selector');

        if (decreaseBtn) {
            decreaseBtn.addEventListener('click', function () {
                if (quantityInput) {
                    const currentValue = parseInt(quantityInput.value);
                    if (currentValue > 1) {
                        updateQuantity(currentValue - 1);
                    }
                }
            });
        }

        if (increaseBtn) {
            increaseBtn.addEventListener('click', function () {
                if (quantityInput) {
                    const currentValue = parseInt(quantityInput.value);
                    const maxValue = parseInt(quantityInput.max) || 99;
                    if (currentValue < maxValue) {
                        updateQuantity(currentValue + 1);
                    }
                }
            });
        }

        // Update quantity with animation
        function updateQuantity(newValue) {
            quantityInput.value = newValue;
            quantityInput.classList.add('changing');
            
            setTimeout(() => {
                quantityInput.classList.remove('changing');
            }, 300);
            
            // Update button states
            updateQuantityButtons();
        }

        // Update button states based on current quantity
        function updateQuantityButtons() {
            const currentValue = parseInt(quantityInput.value);
            const maxValue = parseInt(quantityInput.max) || 99;
            
            if (decreaseBtn) {
                decreaseBtn.disabled = currentValue <= 1;
            }
            
            if (increaseBtn) {
                increaseBtn.disabled = currentValue >= maxValue;
            }
            
            // Update selector state based on stock
            if (quantitySelector) {
                quantitySelector.classList.toggle('low-stock', maxValue <= 5 && maxValue > 0);
                quantitySelector.classList.toggle('out-of-stock', maxValue <= 0);
            }
        }

        // Initialize button states
        updateQuantityButtons();
        
        // Listen for direct input changes
        if (quantityInput) {
            quantityInput.addEventListener('change', function() {
                const value = parseInt(this.value);
                const maxValue = parseInt(this.max) || 99;
                
                if (value < 1) {
                    this.value = 1;
                } else if (value > maxValue) {
                    this.value = maxValue;
                }
                
                updateQuantityButtons();
            });
        }
    }

    function initActionButtons() {
        const addToCartBtn = document.getElementById('add-to-cart');
        const buyNowBtn = document.getElementById('buy-now');

        if (addToCartBtn) {
            addToCartBtn.addEventListener('click', handleAddToCart);
        }

        if (buyNowBtn) {
            buyNowBtn.addEventListener('click', handleBuyNow);
        }
    }

    function handleAddToCart() {
        console.log('=== HANDLE ADD TO CART DEBUG ===');
        console.log('Selected size:', selectedSize);
        console.log('Selected color:', selectedColor);
        
        // Check if size and color are selected
        if (!selectedSize) {
            showAlert('Vui lòng chọn kích cỡ trước khi thêm vào giỏ hàng!');
            return;
        }
        
        if (!selectedColor) {
            showAlert('Vui lòng chọn màu sắc trước khi thêm vào giỏ hàng!');
            return;
        }

        const quantityInput = document.getElementById('quantity');
        const quantity = quantityInput ? parseInt(quantityInput.value) : 1;
        
        console.log('Quantity:', quantity);
        
        // Validate quantity
        if (isNaN(quantity) || quantity <= 0) {
            showAlert('Số lượng phải lớn hơn 0!');
            return;
        }
        
        // Tìm sản phẩm chi tiết ID dựa vào size và color đã chọn
        const sanPhamChiTietId = getSanPhamChiTietId(selectedSize, selectedColor);
        
        console.log('San pham chi tiet ID:', sanPhamChiTietId);
        
        if (!sanPhamChiTietId) {
            console.log('Available variants for debugging:', window.variants);
            console.log('Available sizes:', window.availableSizes);
            console.log('Available colors:', window.availableColors);
            showAlert('Không tìm thấy sản phẩm với kích cỡ và màu sắc đã chọn! Vui lòng thử lại.');
            return;
        }

        // Hiển thị loading
        const addToCartBtn = document.getElementById('add-to-cart');
        const originalText = addToCartBtn.innerHTML;
        addToCartBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang thêm...';
        addToCartBtn.disabled = true;

        console.log('Sending request to /api/cart/add with:', {
            sanPhamChiTietId: sanPhamChiTietId,
            soLuong: quantity
        });

        // Gửi request thêm vào giỏ hàng
        fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `sanPhamChiTietId=${sanPhamChiTietId}&soLuong=${quantity}`
        })
        .then(response => {
            console.log('Response status:', response.status);
            console.log('Response ok:', response.ok);
            
            // Check if response indicates user needs to login (401 or 403)
            if (response.status === 401 || response.status === 403) {
                console.log('User not authenticated, redirecting to login...');
                // Redirect to login page
                window.location.href = '/dang-nhap';
                return null; // Return null to skip further processing
            }
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return response.json();
        })
        .then(data => {
            // If data is null, it means we redirected to login
            if (data === null) return;
            
            console.log('Response data:', data);
            if (data.success) {
                showAlert(`Đã thêm ${quantity} sản phẩm vào giỏ hàng thành công!`);
                // Cập nhật số lượng giỏ hàng trên header nếu có
                updateCartCount(data.cartCount);
            } else {
                console.error('Add to cart failed:', data.message);
                
                // Check if the error message indicates need to login or if requireLogin flag is set
                if (data.requireLogin || (data.message && (data.message.includes('đăng nhập') || data.message.includes('login')))) {
                    console.log('Login required, redirecting to login page...');
                    window.location.href = '/dang-nhap';
                    return;
                }
                
                showAlert(data.message || 'Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng!');
            }
        })
        .catch(error => {
            console.error('Error in add to cart:', error);
            showAlert('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng! Vui lòng thử lại.');
        })
        .finally(() => {
            // Khôi phục nút
            addToCartBtn.innerHTML = originalText;
            addToCartBtn.disabled = false;
        });
    }

    function handleBuyNow() {
        if (!selectedSize || !selectedColor) {
            showAlert('Vui lòng chọn kích cỡ và màu sắc trước khi mua ngay!');
            return;
        }

        const quantityInput = document.getElementById('quantity');
        const quantity = quantityInput ? parseInt(quantityInput.value) : 1;
        
        // Tìm sản phẩm chi tiết ID dựa vào size và color đã chọn
        const sanPhamChiTietId = getSanPhamChiTietId(selectedSize, selectedColor);
        
        if (!sanPhamChiTietId) {
            showAlert('Không tìm thấy sản phẩm với kích cỡ và màu sắc đã chọn!');
            return;
        }

        // Hiển thị loading
        const buyNowBtn = document.getElementById('buy-now');
        const originalText = buyNowBtn.innerHTML;
        buyNowBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang xử lý...';
        buyNowBtn.disabled = true;

        // Thêm vào giỏ hàng trước, sau đó chuyển đến trang thanh toán
        fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `sanPhamChiTietId=${sanPhamChiTietId}&soLuong=${quantity}`
        })
        .then(response => {
            // Check if response indicates user needs to login (401 or 403)
            if (response.status === 401 || response.status === 403) {
                console.log('User not authenticated, redirecting to login...');
                // Redirect to login page
                window.location.href = '/dang-nhap';
                return null;
            }
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return response.json();
        })
        .then(data => {
            // If data is null, it means we redirected to login
            if (data === null) return;
            
            if (data.success) {
                // Chuyển đến trang thanh toán
                window.location.href = '/checkout';
            } else {
                // Check if the error message indicates need to login or if requireLogin flag is set
                if (data.requireLogin || (data.message && (data.message.includes('đăng nhập') || data.message.includes('login')))) {
                    console.log('Login required, redirecting to login page...');
                    window.location.href = '/dang-nhap';
                    return;
                }
                
                showAlert(data.message || 'Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng!');
        })
        .finally(() => {
            // Khôi phục nút
            buyNowBtn.innerHTML = originalText;
            buyNowBtn.disabled = false;
        });
    }

    // Hàm tìm ID sản phẩm chi tiết dựa vào size và color
    function getSanPhamChiTietId(size, color) {
        console.log('=== getSanPhamChiTietId() được gọi ===');
        console.log('Looking for size:', size, 'color:', color);
        console.log('Available variants:', window.variants);
        
        if (!window.variants || !Array.isArray(window.variants)) {
            console.log('No variants data available');
            return null;
        }
        
        // Try exact match first
        let variant = window.variants.find(v => {
            console.log('Checking variant:', v);
            
            // Handle different data structures for kichCo and mauSac
            let variantSize = '';
            let variantColor = '';
            
            if (v.kichCo) {
                variantSize = v.kichCo.tenKichCo || v.kichCo;
            }
            
            if (v.mauSac) {
                variantColor = v.mauSac.tenMauSac || v.mauSac;
            }
            
            console.log('Variant size:', variantSize, 'Variant color:', variantColor);
            console.log('Size match:', variantSize === size);
            console.log('Color match:', variantColor === color);
            
            return variantSize === size && variantColor === color;
        });
        
        // If exact match fails, try case-insensitive match
        if (!variant) {
            console.log('Exact match failed, trying case-insensitive match...');
            variant = window.variants.find(v => {
                let variantSize = '';
                let variantColor = '';
                
                if (v.kichCo) {
                    variantSize = (v.kichCo.tenKichCo || v.kichCo).toString().toLowerCase().trim();
                }
                
                if (v.mauSac) {
                    variantColor = (v.mauSac.tenMauSac || v.mauSac).toString().toLowerCase().trim();
                }
                
                const sizeMatch = variantSize === size.toLowerCase().trim();
                const colorMatch = variantColor === color.toLowerCase().trim();
                
                console.log('Case-insensitive - Variant size:', variantSize, 'Variant color:', variantColor);
                console.log('Case-insensitive - Size match:', sizeMatch, 'Color match:', colorMatch);
                
                return sizeMatch && colorMatch;
            });
        }
        
        // If still no match, try size-only match (fallback for single color products)
        if (!variant && window.variants.length > 0) {
            console.log('No exact match found, trying size-only match...');
            variant = window.variants.find(v => {
                let variantSize = '';
                
                if (v.kichCo) {
                    variantSize = (v.kichCo.tenKichCo || v.kichCo).toString().toLowerCase().trim();
                }
                
                const sizeMatch = variantSize === size.toLowerCase().trim();
                console.log('Size-only match - Variant size:', variantSize, 'Size match:', sizeMatch);
                
                return sizeMatch;
            });
            
            if (variant) {
                console.log('Found variant using size-only match:', variant);
            }
        }
        
        console.log('Final found variant:', variant);
        return variant ? variant.id : null;
    }

    // Hàm cập nhật số lượng giỏ hàng trên header
    function updateCartCount(count) {
        const cartCountElements = document.querySelectorAll('.cart-count');
        cartCountElements.forEach(element => {
            element.textContent = count || 0;
        });
    }

    function showAlert(message) {
        // Enhanced alert with better user experience
        if (typeof toastr !== 'undefined') {
            toastr.info(message);
        } else {
            alert(message);
        }
        console.log('Alert shown:', message);
    }
    
    // Debug helper function
    window.debugCartData = function() {
        console.log('=== CART DEBUG INFO ===');
        console.log('Product ID:', window.productId);
        console.log('Available sizes:', window.availableSizes);
        console.log('Available colors:', window.availableColors);
        console.log('Variants:', window.variants);
        console.log('Stock map:', window.tonKhoMap);
        console.log('Selected size:', selectedSize);
        console.log('Selected color:', selectedColor);
        console.log('========================');
    };

    // STOCK DATA SETTER
    window.setStockData = function(data) {
        stockData = data || {};
        console.log('Stock data set to:', stockData);
        console.log('Stock data type:', typeof stockData);
        console.log('Stock data keys:', Object.keys(stockData));
        
        // Log first level structure
        for (const key in stockData) {
            console.log(`stockData[${key}]:`, stockData[key], 'type:', typeof stockData[key]);
        }
    };

    // INITIALIZE 
    initProductDetailPage();

    // Debug: Check if data is loaded correctly after initialization
    console.log('=== DEBUG CHI TIẾT SẢN PHẨM AFTER INIT ===');
    console.log('Variants data:', window.variants);
    console.log('Stock data:', stockData);
    console.log('Selected size:', selectedSize);
    console.log('Selected color:', selectedColor);
    console.log('==============================================');
});
