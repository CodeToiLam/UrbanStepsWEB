document.addEventListener('DOMContentLoaded', function() {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || '';
    try{ console.debug('[checkout] DOMContentLoaded, csrf present=', !!csrfToken); }catch(e){}
    const form = document.getElementById('checkoutForm');
    const voucherBtn = document.getElementById('applyVoucherBtn');
    const I18N = (typeof window !== 'undefined' && window.i18n) ? window.i18n : {};
    const t = (k, fb) => (I18N && I18N[k]) ? I18N[k] : fb;

    // Voucher apply
    function applyVoucher(){
    try{ console.debug('[checkout] applyVoucher called'); }catch(e){}
    const codeInput = document.querySelector('input[name="maGiamGia"]');
    // support either an element with id "voucherCode" (older UI) or the visible input[name="maGiamGia"]
    const codeSelect = document.getElementById('voucherCode');
    const code = (codeInput?.value || codeSelect?.value || '').trim();
    try{ console.debug('[checkout] voucher code resolved=', code); }catch(e){}
        if(!code){
            const msg = t('voucher_selectPrompt','Vui lòng chọn mã');
            return (typeof toast === 'function') ? toast(msg,'error') : alert(msg);
        }
        fetch('/checkout/api/apply-voucher-json',{
            method:'POST',
            headers:{'Content-Type':'application/json','X-CSRF-TOKEN':csrfToken},
            body:JSON.stringify({voucherCode:code})
        }).then(r=>r.json()).then(d=>{
            if(d.success){
                const discount = (Number(d.totalAmount)||0) - (Number(d.discountedTotal)||0);
                const finalTotal = Number(d.discountedTotal)||0;
                const discountEl = document.getElementById('discountAmount');
                const finalTotalEl = document.getElementById('finalTotal');
                if(discountEl) discountEl.textContent = formatCurrency(discount);
                if(finalTotalEl) finalTotalEl.textContent = formatCurrency(finalTotal);

                if(d.perItemDiscount && typeof d.perItemDiscount === 'object'){
                    document.querySelectorAll('.item-line-discount').forEach(el=>{el.style.display='none'; el.textContent='';});
                    Object.entries(d.perItemDiscount).forEach(([id,amount])=>{
                        const disc = Number(amount)||0;
                        const cell = document.querySelector(`.item-line-discount[data-item-id="${id}"]`);
                        if(cell && disc>0){
                            cell.textContent = '-' + formatCurrency(disc);
                            cell.style.display = 'block';
                        }
                    });
                }
                const msg = t('voucher_applySuccess','Áp dụng mã thành công');
                (typeof toast === 'function') ? toast(msg,'success') : alert(msg);
            } else {
                const msg = d.message || t('voucher_invalid','Mã không hợp lệ');
                (typeof toast === 'function') ? toast(msg,'error') : alert(msg);
            }
        }).catch(()=>{ const msg = t('voucher_error','Lỗi áp dụng mã'); (typeof toast === 'function') ? toast(msg,'error') : alert(msg); });
    }

    // Expose for inline handlers / console debugging
    window.applyVoucher = applyVoucher;

    // Bind apply button and input actions
    try{ if(voucherBtn && (voucherBtn.dataset.exclusive===undefined || voucherBtn.dataset.exclusive==='1')) { voucherBtn.addEventListener('click', applyVoucher); console.debug('[checkout] bound applyVoucher to #applyVoucherBtn'); } }catch(e){}
    // Bind change on either the #voucherCode element or the visible input
    const voucherCodeEl = document.getElementById('voucherCode') || document.querySelector('input[name="maGiamGia"]');
    voucherCodeEl?.addEventListener('change', applyVoucher);
    document.querySelector('input[name="maGiamGia"]')?.addEventListener('keydown', function(e){ if(e.key==='Enter'){ e.preventDefault(); applyVoucher(); }});

    // Bind modal "Dùng mã" buttons idempotently
    function bindUseVoucherButtons(){
        document.querySelectorAll('.use-voucher-btn').forEach(btn=>{
            if(btn.dataset.bound) return;
            btn.dataset.bound = '1';
            btn.addEventListener('click', function(ev){
                try{
                    ev.preventDefault();
                    const code = btn.dataset.code;
                    console.debug('[checkout] use-voucher clicked, code=', code);
                    const sel = document.querySelector('input[name="maGiamGia"]');
                    if(sel) sel.value = code;
                    applyVoucher();
                        // Close modal gracefully without calling Bootstrap internals (avoid modal.js backdrop error)
                        try{
                            const modalEl = document.getElementById('voucherModal');
                            if(modalEl){
                                // Prefer clicking the dismiss button to let Bootstrap handle the hide lifecycle
                                const dismissBtn = modalEl.querySelector('[data-bs-dismiss="modal"]');
                                if(dismissBtn){ try{ dismissBtn.click(); }catch(e){} }
                                else {
                                    // Fallback: perform a safe manual hide (best-effort)
                                    try{ modalEl.classList.remove('show'); modalEl.style.display='none'; }catch(e){}
                                    try{ document.body.classList.remove('modal-open'); }catch(e){}
                                    try{ const backdrop=document.querySelector('.modal-backdrop'); if(backdrop) backdrop.remove(); }catch(e){}
                                }
                            }
                        }catch(e){}
                }catch(e){/* ignore */}
            });
        });
    }
    try{ bindUseVoucherButtons(); }catch(e){}

    // Fallback: if data-bs-toggle on #openVoucherModalBtn doesn't work (missing bootstrap or attribute handling),
    // ensure button will open modal via Bootstrap JS API when available.
    try{
        const openVoucherModalBtn = document.getElementById('openVoucherModalBtn');
        if(openVoucherModalBtn){
            openVoucherModalBtn.addEventListener('click', function(ev){
                try{
                    const modalEl = document.getElementById('voucherModal');
                    // If user is logged in, prefer loading their voucher wallet; otherwise notify to login.
                    const isLoggedIn = document.querySelector('meta[name="isLoggedIn"]')?.getAttribute('content') === '1';
                    if(!isLoggedIn){
                        const msg = t('voucher_login_required','Vui lòng đăng nhập để sử dụng kho mã của bạn');
                        (typeof toast === 'function') ? toast(msg,'error') : alert(msg);
                        // open modal to show public vouchers as fallback
                    } else {
                        // load account vouchers into modal body (id 'voucherModal') list container
                        try{
                            const body = document.querySelector('#voucherModal .modal-body');
                            if(body){
                                const listGroup = body.querySelector('.list-group') || document.createElement('div');
                                listGroup.className = 'list-group';
                                listGroup.innerHTML = '<div class="text-center py-2 small text-muted">Đang tải kho mã...</div>';
                                // replace or append
                                if(!body.querySelector('.list-group')) body.appendChild(listGroup);
                                // prefer user's claimed vouchers
                                fetch('/tai-khoan/api/my-vouchers').then(r=>r.json()).then(vs=>{
                                    listGroup.innerHTML = '';
                                    const arr = Array.isArray(vs) ? vs : [];
                                    if(arr.length===0){ listGroup.innerHTML = '<div class="text-muted p-2">Không có mã trong kho của bạn.</div>'; }
                                    arr.forEach(v=>{
                                        const item = document.createElement('div');
                                        item.className = 'list-group-item d-flex justify-content-between align-items-center';
                                        item.innerHTML = `<div style="flex:1; min-width:0;">
                                                <div class="d-flex align-items-center gap-2">
                                                    <div class="fw-bold text-truncate">${v.maPhieuGiamGia || ''}</div>
                                                    <span class="small text-muted">${v.moTa || ''}</span>
                                                </div>
                                                <div class="small text-muted mt-1">HSD: ${v.ngayKetThuc ? v.ngayKetThuc.substring(0,10) : ''}</div>
                                            </div>
                                            <div class="d-flex gap-2 align-items-center ms-3">
                                                <button type="button" class="btn btn-sm btn-outline-secondary" data-code="${v.maPhieuGiamGia || ''}" onclick="navigator.clipboard.writeText(this.dataset.code)">Copy</button>
                                                <button type="button" class="btn btn-sm btn-primary use-voucher-btn" data-code="${v.maPhieuGiamGia || ''}">Dùng mã</button>
                                            </div>`;
                                        listGroup.appendChild(item);
                                    });
                                    // re-bind use buttons inside modal
                                    bindUseVoucherButtons();
                                }).catch(()=>{
                                    listGroup.innerHTML = '<div class="text-danger p-2">Lỗi tải kho mã. Vui lòng thử lại.</div>';
                                });
                            }
                        }catch(e){}
                    }
                    // Prefer Bootstrap API when available
                    if(modalEl && window.bootstrap && bootstrap.Modal && typeof bootstrap.Modal.getOrCreateInstance === 'function'){
                        const m = bootstrap.Modal.getOrCreateInstance(modalEl);
                        m.show();
                        return;
                    }

                    // Manual fallback: show modal and backdrop safely when Bootstrap isn't available
                    if(modalEl){
                        try{
                            // create backdrop if missing
                            if(!document.querySelector('.modal-backdrop')){
                                const backdrop = document.createElement('div');
                                backdrop.className = 'modal-backdrop fade show';
                                document.body.appendChild(backdrop);
                            }
                            modalEl.classList.add('show');
                            modalEl.style.display = 'block';
                            document.body.classList.add('modal-open');
                        }catch(e){}
                    }
                }catch(e){}
            });
        }
    }catch(e){}

    // Global fallback: handle data-bs-dismiss clicks when Bootstrap isn't present
    try{
        document.addEventListener('click', function(ev){
            try{
                const target = ev.target;
                if(!target) return;
                const dismiss = target.closest && target.closest('[data-bs-dismiss="modal"]');
                if(!dismiss) return;
                const modalEl = dismiss && dismiss.closest && dismiss.closest('.modal');
                if(modalEl && !(window.bootstrap && bootstrap.Modal)){
                    // manual hide
                    try{ modalEl.classList.remove('show'); modalEl.style.display='none'; }catch(e){}
                    try{ document.body.classList.remove('modal-open'); }catch(e){}
                    try{ const backdrop=document.querySelector('.modal-backdrop'); if(backdrop) backdrop.remove(); }catch(e){}
                }
            }catch(e){}
        });
    }catch(e){}

    // Address cascading selects
    const provinceSelect=document.getElementById('province');
    const districtSelect=document.getElementById('district');
    const wardSelect=document.getElementById('ward');
    let addressData=[];
    if(provinceSelect){
        provinceSelect.innerHTML='<option value="">Chọn tỉnh/thành phố</option>';
        districtSelect.innerHTML='<option value="">Chọn quận/huyện</option>';
        wardSelect.innerHTML='<option value="">Chọn phường/xã</option>';
        fetch('/data/vietnamAddress.json').then(r=>r.json()).then(data=>{
            addressData=data; data.forEach(p=>{const o=document.createElement('option');o.value=p.Id;o.textContent=p.Name;provinceSelect.appendChild(o);});
        }).catch(()=>{});
        provinceSelect.addEventListener('change',function(){
            districtSelect.innerHTML='<option value="">Chọn quận/huyện</option>'; wardSelect.innerHTML='<option value="">Chọn phường/xã</option>'; wardSelect.disabled=true; districtSelect.disabled=!this.value; if(!this.value) return; const pv=addressData.find(p=>p.Id===this.value); if(pv){pv.Districts.forEach(d=>{const o=document.createElement('option');o.value=d.Id;o.textContent=d.Name;districtSelect.appendChild(o);});}
        });
        districtSelect.addEventListener('change',function(){
            wardSelect.innerHTML='<option value="">Chọn phường/xã</option>'; wardSelect.disabled=!this.value; if(!this.value) return; const pv=addressData.find(p=>p.Id===provinceSelect.value); if(!pv) return; const dt=pv.Districts.find(d=>d.Id===this.value); if(dt){dt.Wards.forEach(w=>{const o=document.createElement('option');o.value=w.Id;o.textContent=w.Name;wardSelect.appendChild(o);});}
        });
    }

    function formatCurrency(n){
        try{ return new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND',minimumFractionDigits:0}).format(n||0); }catch(e){ const num = Math.round(Number(n)||0).toLocaleString('vi-VN'); return `${num} ₫`; }

    }

    // Form submit
    form?.addEventListener('submit',function(e){
        e.preventDefault();
        const selectedAddressId=document.getElementById('selectedAddressId')?.value;
        if(selectedAddressId){ ['province','district','ward','diaChiGiaoHang'].forEach(id=>{const el=document.getElementById(id); if(el){el.removeAttribute('required');}}); }
        if(!form.checkValidity()){ form.classList.add('was-validated'); if(selectedAddressId){ ['province','district','ward','diaChiGiaoHang'].forEach(id=>{const el=document.getElementById(id); if(el && id!=='diaChiGiaoHang'){el.setAttribute('required','required');}}); } return; }
        form.classList.add('was-validated');
        const payload={
            fullName: document.getElementById('hoTen').value.trim(),
            phoneNumber: document.getElementById('sdt').value.trim(),
            email: document.getElementById('email').value.trim(),
            province: selectedAddressId? '' : (provinceSelect?.value||''),
            district: selectedAddressId? '' : (districtSelect?.value||''),
            ward: selectedAddressId? '' : (wardSelect?.value||''),
            addressDetail: document.getElementById('diaChiGiaoHang').value.trim(),
            note: document.getElementById('ghiChu').value.trim(),
            paymentMethod: document.querySelector('select[name="phuongThucThanhToan"]').value,
            promoCode: document.querySelector('input[name="maGiamGia"]').value.trim(),
            selectedAddressId: selectedAddressId||null
        };
        fetch('/checkout/place-order',{method:'POST',headers:{'Content-Type':'application/json','X-CSRF-TOKEN':csrfToken},body:JSON.stringify(payload)})
            .then(r=>r.json()).then(d=>{ if(d.success){ const code = encodeURIComponent(d.orderCode||''); window.location.href = '/checkout/success' + (code?`?orderCode=${code}`:''); } else { const msg=d.message||t('place_failed','Đặt hàng thất bại'); (typeof toast === 'function') ? toast(msg,'error') : alert(msg); } })
            .catch(()=>{ const msg=t('network_error','Lỗi kết nối'); (typeof toast === 'function') ? toast(msg,'error') : alert(msg); });
    });
});