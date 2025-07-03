document.addEventListener('DOMContentLoaded', function() {
    // Cải thiện tương tác cho các card sản phẩm trong trang tìm kiếm
    improveProductCardInteraction();
});

function improveProductCardInteraction() {
    // Tìm tất cả các card sản phẩm
    const productCards = document.querySelectorAll('.product-card, .card, .product-item');

    productCards.forEach(card => {
        // Đảm bảo toàn bộ card có cursor là pointer
        card.style.cursor = 'pointer';

        // Tìm link chi tiết sản phẩm trong card
        const detailsLink = card.querySelector('a[href*="/san-pham/chi-tiet/"], a[href*="/chi-tiet/"]');

        if (detailsLink) {
            const detailsUrl = detailsLink.getAttribute('href');

            // Thêm sự kiện click cho toàn bộ card
            card.addEventListener('click', function(e) {
                // Chỉ điều hướng nếu không click vào button hoặc link khác
                if (!e.target.closest('button') && !e.target.closest('a')) {
                    window.location.href = detailsUrl;
                }
            });

            // Tìm tất cả các phần tử con trong card (trừ button và link)
            const childElements = card.querySelectorAll('*:not(button):not(a)');
            childElements.forEach(element => {
                element.style.cursor = 'pointer';
            });
        }
    });

    // Đảm bảo tất cả các link xem chi tiết có cursor là pointer
    const detailLinks = document.querySelectorAll('a[href*="/san-pham/chi-tiet/"], a[href*="/chi-tiet/"]');
    detailLinks.forEach(link => {
        link.style.cursor = 'pointer';
    });
}