// ====== MODERN BANNER CAROUSEL WITH INFINITE LOOP ======
document.addEventListener('DOMContentLoaded', function() {
    const bannerCarousel = document.querySelector('.banner-carousel');
    
    if (!bannerCarousel) return;
    
    const slidesContainer = bannerCarousel.querySelector('.banner-slides');
    const slides = bannerCarousel.querySelectorAll('.banner-slide');
    const prevBtn = bannerCarousel.querySelector('.prev-btn');
    const nextBtn = bannerCarousel.querySelector('.next-btn');
    const dots = bannerCarousel.querySelectorAll('.banner-dot');
    
    if (slides.length <= 1) return;
    
    let currentSlide = 0;
    let isTransitioning = false;
    let autoplayInterval;
    let isDestroyed = false; // Thêm flag để tránh memory leak
    
    // Configuration
    const config = {
        autoplayDelay: 6000, // Tăng delay để giảm tần suất load
        transitionDuration: 600,
        pauseOnHover: true
    };
    
    // Clone first and last slides for infinite loop
    const firstSlideClone = slides[0].cloneNode(true);
    const lastSlideClone = slides[slides.length - 1].cloneNode(true);
    
    // Add clones to the container
    slidesContainer.appendChild(firstSlideClone);
    slidesContainer.insertBefore(lastSlideClone, slides[0]);
    
    // Update slides collection to include clones
    const allSlides = slidesContainer.querySelectorAll('.banner-slide');
    
    // Set initial position (offset by 1 because of the prepended clone)
    currentSlide = 1;
    slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;
    
    // Initialize carousel
    function initCarousel() {
        if (isDestroyed) return;
        
        updateDots();
        
        if (config.pauseOnHover) {
            bannerCarousel.addEventListener('mouseenter', pauseAutoplay);
            bannerCarousel.addEventListener('mouseleave', startAutoplay);
        }
        
        startAutoplay();
    }
    
    // Add cleanup function
    function destroyCarousel() {
        isDestroyed = true;
        clearInterval(autoplayInterval);
        bannerCarousel.removeEventListener('mouseenter', pauseAutoplay);
        bannerCarousel.removeEventListener('mouseleave', startAutoplay);
    }
    
    // Cleanup when page unloads
    window.addEventListener('beforeunload', destroyCarousel);
    
    // Update slide position
    function updateSlidePosition(animate = true) {
        if (animate) {
            slidesContainer.style.transition = `transform ${config.transitionDuration}ms cubic-bezier(0.25, 0.8, 0.25, 1)`;
        } else {
            slidesContainer.style.transition = 'none';
        }
        
        const translateX = -currentSlide * 100;
        slidesContainer.style.transform = `translateX(${translateX}%)`;
    }
    
    // Update dots
    function updateDots() {
        dots.forEach((dot, index) => {
            // Map current slide to original slide index (accounting for clones)
            const originalIndex = currentSlide === 0 ? slides.length - 1 : 
                                  currentSlide === allSlides.length - 1 ? 0 : 
                                  currentSlide - 1;
            dot.classList.toggle('active', index === originalIndex);
        });
    }
    
    // Go to specific slide
    function goToSlide(slideIndex, animate = true) {
        if (isTransitioning) return;
        
        isTransitioning = true;
        
        // Convert original slide index to actual slide index (accounting for clones)
        const targetSlide = slideIndex + 1;
        
        currentSlide = targetSlide;
        updateSlidePosition(animate);
        updateDots();
        
        setTimeout(() => {
            isTransitioning = false;
        }, config.transitionDuration);
    }
    
    // Next slide with infinite loop
    function nextSlide() {
        if (isTransitioning) return;
        
        isTransitioning = true;
        currentSlide++;
        
        updateSlidePosition(true);
        updateDots();
        
        // Check if we've reached the cloned first slide
        if (currentSlide === allSlides.length - 1) {
            setTimeout(() => {
                currentSlide = 1; // Jump to real first slide
                updateSlidePosition(false);
                updateDots();
                setTimeout(() => {
                    isTransitioning = false;
                }, 50);
            }, config.transitionDuration);
        } else {
            setTimeout(() => {
                isTransitioning = false;
            }, config.transitionDuration);
        }
    }
    
    // Previous slide with infinite loop
    function prevSlide() {
        if (isTransitioning) return;
        
        isTransitioning = true;
        currentSlide--;
        
        updateSlidePosition(true);
        updateDots();
        
        // Check if we've reached the cloned last slide
        if (currentSlide === 0) {
            setTimeout(() => {
                currentSlide = allSlides.length - 2; // Jump to real last slide
                updateSlidePosition(false);
                updateDots();
                setTimeout(() => {
                    isTransitioning = false;
                }, 50);
            }, config.transitionDuration);
        } else {
            setTimeout(() => {
                isTransitioning = false;
            }, config.transitionDuration);
        }
    }
    
    // Start autoplay
    function startAutoplay() {
        if (slides.length <= 1 || isDestroyed) return;
        
        stopAutoplay(); // Đảm bảo clear interval cũ
        
        // Thêm kiểm tra visibility và focus
        if (document.hidden || !document.hasFocus()) {
            return;
        }
        
        // Chỉ tạo interval mới nếu chưa có
        if (!autoplayInterval) {
            autoplayInterval = setInterval(() => {
                if (!isDestroyed && !isTransitioning && !document.hidden) {
                    nextSlide();
                }
            }, config.autoplayDelay);
        }
    }
    
    // Stop autoplay
    function stopAutoplay() {
        if (autoplayInterval) {
            clearInterval(autoplayInterval);
            autoplayInterval = null;
        }
    }
    
    // Pause autoplay
    function pauseAutoplay() {
        stopAutoplay();
    }
    
    // Event listeners
    if (prevBtn) {
        prevBtn.addEventListener('click', (e) => {
            e.preventDefault();
            prevSlide();
        });
    }
    
    if (nextBtn) {
        nextBtn.addEventListener('click', (e) => {
            e.preventDefault();
            nextSlide();
        });
    }
    
    // Dot navigation
    dots.forEach((dot, index) => {
        dot.addEventListener('click', (e) => {
            e.preventDefault();
            goToSlide(index);
        });
    });
    
    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
        if (!bannerCarousel.matches(':hover')) return;
        
        switch(e.key) {
            case 'ArrowLeft':
                e.preventDefault();
                prevSlide();
                break;
            case 'ArrowRight':
                e.preventDefault();
                nextSlide();
                break;
        }
    });
    
    // Touch/swipe support
    let touchStartX = 0;
    let touchEndX = 0;
    let touchStartTime = 0;
    
    bannerCarousel.addEventListener('touchstart', (e) => {
        touchStartX = e.changedTouches[0].screenX;
        touchStartTime = Date.now();
    });
    
    bannerCarousel.addEventListener('touchend', (e) => {
        touchEndX = e.changedTouches[0].screenX;
        const touchDuration = Date.now() - touchStartTime;
        
        // Only handle swipe if it's quick enough (not a long press)
        if (touchDuration < 300) {
            handleSwipe();
        }
    });
    
    function handleSwipe() {
        const swipeThreshold = 50;
        const swipeDistance = touchEndX - touchStartX;
        
        if (Math.abs(swipeDistance) > swipeThreshold) {
            if (swipeDistance > 0) {
                prevSlide();
            } else {
                nextSlide();
            }
        }
    }
    
    // Initialize
    initCarousel();
    
    // Pause autoplay when tab is not visible
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            pauseAutoplay();
        } else {
            startAutoplay();
        }
    });
    
    // Cleanup on page unload
    window.addEventListener('beforeunload', () => {
        stopAutoplay();
        destroyCarousel();
    });
    
    // Handle window resize
    window.addEventListener('resize', () => {
        if (!isDestroyed) {
            updateSlidePosition(false);
        }
    });
});
