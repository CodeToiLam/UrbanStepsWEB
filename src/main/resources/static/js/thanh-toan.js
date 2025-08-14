document.addEventListener('DOMContentLoaded', function() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const form = document.getElementById('checkoutForm');
    const voucherBtn = document.getElementById('applyVoucherBtn');
    const I18N = (typeof window !== 'undefined' && window.i18n) ? window.i18n : {};
    const t = (k, fb) => (I18N && I18N[k]) ? I18N[k] : fb;
    // Voucher apply
    function applyVoucher(){
        const codeInput = document.querySelector('input[name="maGiamGia"]');
        const codeSelect = document.getElementById('voucherCode');
        const code = (codeInput?.value || codeSelect?.value || '').trim();
        if(!code){ toast?toast(t('voucher_selectPrompt','Vui lòng chọn mã'),'error'):alert(t('voucher_selectPrompt','Vui lòng chọn mã')); return; }
        fetch('/checkout/api/apply-voucher-json',{
            method:'POST',headers:{'Content-Type':'application/json','X-CSRF-TOKEN':csrfToken},body:JSON.stringify({voucherCode:code})
        }).then(r=>r.json()).then(d=>{
            if(d.success){
                // Update totals
                const discount = (Number(d.totalAmount)||0) - (Number(d.discountedTotal)||0);
                const finalTotal = Number(d.discountedTotal)||0;
                const discountEl = document.getElementById('discountAmount');
                const finalTotalEl = document.getElementById('finalTotal');
                if(discountEl) discountEl.textContent = formatCurrency(discount);
                if(finalTotalEl) finalTotalEl.textContent = formatCurrency(finalTotal);

                // Render per-item discount if provided
                if(d.perItemDiscount && typeof d.perItemDiscount === 'object'){
                    // Hide all first
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
                toast?toast(t('voucher_applySuccess','Áp dụng mã thành công'),'success'):alert(t('voucher_applySuccess','Áp dụng mã thành công'));
            }else{const msg = d.message||t('voucher_invalid','Mã không hợp lệ'); toast?toast(msg,'error'):alert(msg);}
        }).catch(()=>{const msg=t('voucher_error','Lỗi áp dụng mã'); toast?toast(msg,'error'):alert(msg);});
    }
    // Prevent double-binding with generic handler: only bind if our button has data-exclusive
    if(voucherBtn && voucherBtn.dataset.exclusive==='1'){
        voucherBtn.addEventListener('click',applyVoucher);
        document.getElementById('voucherCode')?.addEventListener('change',applyVoucher);
        document.querySelector('input[name="maGiamGia"]')?.addEventListener('keydown',function(e){ if(e.key==='Enter'){ e.preventDefault(); applyVoucher(); }});
    }

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
            districtSelect.innerHTML='<option value="">Chọn quận/huyện</option>'; wardSelect.innerHTML='<option value="">Chọn phường/xã</option>'; wardSelect.disabled=true; districtSelect.disabled=!this.value; if(!this.value)return; const pv=addressData.find(p=>p.Id===this.value); if(pv){pv.Districts.forEach(d=>{const o=document.createElement('option');o.value=d.Id;o.textContent=d.Name;districtSelect.appendChild(o);});}}
        );
        districtSelect.addEventListener('change',function(){
            wardSelect.innerHTML='<option value="">Chọn phường/xã</option>'; wardSelect.disabled=!this.value; if(!this.value)return; const pv=addressData.find(p=>p.Id===provinceSelect.value); if(!pv)return; const dt=pv.Districts.find(d=>d.Id===this.value); if(dt){dt.Wards.forEach(w=>{const o=document.createElement('option');o.value=w.Id;o.textContent=w.Name;wardSelect.appendChild(o);});}}
        );
    }

    function formatCurrency(n){
        try{
            return new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND',minimumFractionDigits:0}).format(n||0);
        }catch(e){
            const num = Math.round(Number(n)||0).toLocaleString('vi-VN');
            return `${num} ₫`;
        }
    }

    // Form submit
    form?.addEventListener('submit',function(e){
        e.preventDefault();
            const selectedAddressId=document.getElementById('selectedAddressId')?.value;
            if(selectedAddressId){
                // Remove required constraints from manual fields to satisfy native validation
                ['province','district','ward','diaChiGiaoHang'].forEach(id=>{const el=document.getElementById(id); if(el){el.removeAttribute('required');}});
            }
            if(!form.checkValidity()){form.classList.add('was-validated'); if(selectedAddressId){ // restore required after feedback for future edits
                    ['province','district','ward','diaChiGiaoHang'].forEach(id=>{const el=document.getElementById(id); if(el && id!=='diaChiGiaoHang'){el.setAttribute('required','required');}});
                } return;}
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
            .then(r=>r.json()).then(d=>{ if(d.success){ const code = encodeURIComponent(d.orderCode||''); window.location.href = '/checkout/success' + (code?`?orderCode=${code}`:''); } else { const msg=d.message||t('place_failed','Đặt hàng thất bại'); toast?toast(msg,'error'):alert(msg); } })
            .catch(()=>{const msg=t('network_error','Lỗi kết nối'); toast?toast(msg,'error'):alert(msg);});
    });
});