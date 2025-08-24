// product-review.js
// Hiển thị và gửi đánh giá sản phẩm

document.addEventListener('DOMContentLoaded', function() {
    // Lấy id sản phẩm từ thẻ HTML
    var reviewSection = document.getElementById('review-section');
    if (!reviewSection) return;
    var sanPhamId = reviewSection.getAttribute('data-product-id');
    if (!sanPhamId) return;

    function loadReviews() {
        fetch(`/api/san-pham/${sanPhamId}/danh-gia`)
            .then(res => res.json())
            .then(data => {
                const reviews = Array.isArray(data) ? data : [];
                // summary
                const summaryEl = document.getElementById('product-rating-summary');
                if (summaryEl) {
                    if (reviews.length === 0) {
                        summaryEl.innerHTML = '<span class="text-muted">Chưa có đánh giá</span>';
                    } else {
                        const total = reviews.reduce((s, r) => s + (Number(r.diemDanhGia) || 0), 0);
                        const avg = Math.round((total / reviews.length) * 10) / 10;
                        const full = Math.round(avg);
                        let starsHtml = '';
                        for (let i = 1; i <= 5; i++) {
                            starsHtml += `<span class="review-star ${i <= full ? 'filled' : ''}">★</span>`;
                        }
                        summaryEl.innerHTML = `<div class="d-flex align-items-center"><div class="me-2 review-summary-stars">${starsHtml}</div><div><div class="fw-bold">${avg} <small class="text-muted">/5</small></div><div class="text-muted">(${reviews.length} đánh giá)</div></div></div>`;
                    }
                }

                const list = document.getElementById('review-list');
                if (!list) return;
                if (reviews.length === 0) {
                    list.innerHTML = '<p class="text-muted">Chưa có đánh giá nào cho sản phẩm này.</p>';
                    return;
                }

                let html = '';
                reviews.forEach(r => {
                    const author = (r.taiKhoan && (r.taiKhoan.hoTenTaiKhoan || r.taiKhoan.taiKhoan)) || 'Ẩn danh';
                    const score = Number(r.diemDanhGia) || 0;
                    const created = r.createAt ? (r.createAt.replace && r.createAt.replace('T', ' ')) : '';
                    const initials = author.split(' ').map(s => s[0] || '').join('').slice(0, 2).toUpperCase();
                    let starsHtml = '';
                    for (let i = 1; i <= 5; i++) {
                        starsHtml += `<span class="review-star ${i <= score ? 'filled' : ''}">★</span>`;
                    }
                    html += `<div class="review-item">
                                <div class="avatar">${initials}</div>
                                <div class="meta">
                                    <div class="name">${author} <span class="time">• ${created}</span></div>
                                    <div class="stars">${starsHtml}</div>
                                    <div class="text mt-1">${r.noiDung || ''}</div>
                                </div>
                             </div>`;
                });
                list.innerHTML = html;
            }).catch(err => {
                console.error('Failed to load reviews', err);
            });
    }
    loadReviews();

    var reviewForm = document.getElementById('review-form');
    if (reviewForm) {
    // Read CSRF token from meta tag (if Spring Security CSRF is enabled)
    var csrfMeta = document.querySelector('meta[name="_csrf"]');
    var csrfToken = csrfMeta ? csrfMeta.getAttribute('content') : null;
        // initialize star rating UI
        var starRating = document.getElementById('star-rating');
        var ratingInput = reviewForm.querySelector('input[name="diemDanhGia"]');
        function renderStars(selected) {
            if (!starRating) return;
            var stars = starRating.querySelectorAll('.star');
            stars.forEach(function(s){
                var v = Number(s.getAttribute('data-value'));
                if (v <= selected) {
                    s.classList.add('filled');
                    s.textContent = '★';
                } else {
                    s.classList.remove('filled');
                    s.textContent = '☆';
                }
            });
        }
        if (starRating && ratingInput) {
            renderStars(Number(ratingInput.value || 5));
            starRating.addEventListener('click', function(e){
                var target = e.target;
                if (target && target.classList.contains('star')) {
                    var v = Number(target.getAttribute('data-value')) || 5;
                    ratingInput.value = v;
                    renderStars(v);
                }
            });
            // allow hover preview
            starRating.addEventListener('mouseover', function(e){
                var target = e.target;
                if (target && target.classList.contains('star')) {
                    renderStars(Number(target.getAttribute('data-value')));
                }
            });
            starRating.addEventListener('mouseout', function(){ renderStars(Number(ratingInput.value || 5)); });
        }

        reviewForm.onsubmit = function(e) {
            e.preventDefault();
            var noiDung = this.noiDung.value;
            var diemDanhGia = this.diemDanhGia.value;
            var headers = {'Content-Type': 'application/json'};
            if (csrfToken) headers['X-CSRF-TOKEN'] = csrfToken;
            fetch(`/api/san-pham/${sanPhamId}/danh-gia`, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({noiDung, diemDanhGia})
            })
            .then(res => res.json().then(data => ({ok: res.ok, data}))).then(result => {
                var ok = result.ok;
                var data = result.data;
                var msg = (data && (data.message || data.error)) ? (data.message || data.error) : JSON.stringify(data);
                var cls = ok ? 'alert alert-success' : 'alert alert-danger';
                document.getElementById('review-message').innerHTML = `<div class='${cls}'>${msg}</div>`;
                if (ok) {
                    loadReviews();
                    this.reset();
                    if (ratingInput) { ratingInput.value = 5; renderStars(5); }
                }
            }).catch(err => {
                document.getElementById('review-message').innerHTML = `<div class='alert alert-danger'>Lỗi khi gửi đánh giá</div>`;
                console.error(err);
            });
        };
    }
});
