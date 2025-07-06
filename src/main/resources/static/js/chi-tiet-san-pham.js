/**
 * ====== URBANSTEPS - CHI TIẾT SẢN PHẨM ====== 
 * JavaScript cho trang chi tiết sản phẩm với gallery chuyên nghiệp
 */

document.addEventListener('DOMContentLoaded', function () {
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
    }

    function updateStockInfo() {
        const sizeChecked = document.querySelector('input[name="size"]:checked');
        const colorChecked = document.querySelector('input[name="color"]:checked');

        if (sizeChecked && colorChecked) {
            selectedSize = sizeChecked.value;
            selectedColor = colorChecked.value;

            const stockQuantity = stockData[selectedSize]?.[selectedColor] || 0;
            const stockQuantityElement = document.getElementById('stock-quantity');
            if (stockQuantityElement) {
                stockQuantityElement.textContent = stockQuantity > 0 ? stockQuantity : 'Hết hàng';
            }

            const addToCartBtn = document.getElementById('add-to-cart');
            const buyNowBtn = document.getElementById('buy-now');

            if (stockQuantity > 0) {
                if (addToCartBtn) addToCartBtn.disabled = false;
                if (buyNowBtn) buyNowBtn.disabled = false;
            } else {
                if (addToCartBtn) addToCartBtn.disabled = true;
                if (buyNowBtn) buyNowBtn.disabled = true;
            }

            const quantityInput = document.getElementById('quantity');
            if (quantityInput) {
                quantityInput.max = stockQuantity;
                if (parseInt(quantityInput.value) > stockQuantity) {
                    quantityInput.value = stockQuantity;
                }
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
        if (!selectedSize || !selectedColor) {
            showAlert('Vui lòng chọn kích cỡ và màu sắc trước khi thêm vào giỏ hàng!');
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
        const addToCartBtn = document.getElementById('add-to-cart');
        const originalText = addToCartBtn.innerHTML;
        addToCartBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang thêm...';
        addToCartBtn.disabled = true;

        // Gửi request thêm vào giỏ hàng
        fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `sanPhamChiTietId=${sanPhamChiTietId}&soLuong=${quantity}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert(`Đã thêm ${quantity} sản phẩm vào giỏ hàng thành công!`);
                // Cập nhật số lượng giỏ hàng trên header nếu có
                updateCartCount(data.cartCount);
            } else {
                showAlert(data.message || 'Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng!');
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
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Chuyển đến trang thanh toán
                window.location.href = '/checkout';
            } else {
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
        // Tìm trong stockData hoặc variants data
        const variants = window.variants || [];
        const variant = variants.find(v => {
            const kichCoName = v.kichCo ? v.kichCo.tenKichCo : '';
            const mauSacName = v.mauSac ? v.mauSac.tenMauSac : '';
            return kichCoName === size && mauSacName === color;
        });
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
        alert(message);
    }

    // ====== STOCK DATA SETTER ======
    window.setStockData = function(data) {
        stockData = data || {};
    };

    // ====== INITIALIZE ======
    initProductDetailPage();
});
