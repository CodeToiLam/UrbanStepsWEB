document.addEventListener('DOMContentLoaded', function() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const form = document.getElementById('checkoutForm');
    const voucherBtn = document.getElementById('applyVoucherBtn');
    // Voucher apply
    function applyVoucher(){
        const code = document.getElementById('voucherCode')?.value.trim();
        if(!code){ toast?toast('Vui lòng chọn mã','error'):alert('Vui lòng chọn mã'); return; }
        fetch('/checkout/api/apply-voucher-json',{
            method:'POST',headers:{'Content-Type':'application/json','X-CSRF-TOKEN':csrfToken},body:JSON.stringify({voucherCode:code})
        }).then(r=>r.json()).then(d=>{
            if(d.success){
                document.getElementById('discountAmount').textContent = formatCurrency(d.totalAmount - d.discountedTotal);
                document.getElementById('finalTotal').textContent = formatCurrency(d.discountedTotal);
                toast?toast('Áp dụng mã thành công','success'):alert('Áp dụng thành công');
            }else{toast?toast(d.message||'Mã không hợp lệ','error'):alert(d.message||'Mã không hợp lệ');}
        }).catch(()=>{toast?toast('Lỗi áp dụng mã','error'):alert('Lỗi áp dụng mã');});
    }
    voucherBtn?.addEventListener('click',applyVoucher);
    document.getElementById('voucherCode')?.addEventListener('change',applyVoucher);

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

    function formatCurrency(n){return new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND',minimumFractionDigits:0}).format(n);}

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
            .then(r=>r.json()).then(d=>{ if(d.success){ window.location.href='/checkout/success'; } else { toast?toast(d.message||'Đặt hàng thất bại','error'):alert(d.message||'Thất bại'); } })
            .catch(()=>{toast?toast('Lỗi kết nối máy chủ','error'):alert('Lỗi kết nối');});
    });
});