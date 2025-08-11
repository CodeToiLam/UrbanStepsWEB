// Address selection & fetch saved addresses
(function(){
  document.addEventListener('DOMContentLoaded',()=>{
    const savedWrap=document.querySelector('.saved-addresses-dynamic');
    if(savedWrap){
      fetch('/checkout/addresses').then(r=>r.json()).then(d=>{
        if(!d.success) return;
        const list=d.data||[];
        if(!list.length){savedWrap.innerHTML='<div class="text-muted small">Chưa có địa chỉ nào.</div>';return;}
        const frag=document.createDocumentFragment();
        list.forEach(a=>{
          const div=document.createElement('div');
          div.className='address-item'+(a.default?' active':'');
          div.setAttribute('data-full',a.full);
          div.innerHTML=`<div class="form-check mt-1"><input class="form-check-input" type="radio" name="addressChoice" value="${a.id}" ${a.default?'checked':''}></div>
          <div class="addr-meta"><div class="d-flex align-items-center gap-2 flex-wrap"><strong>${a.ten} • ${a.sdt}</strong>${a.default?'<span class="badge-default">Mặc định</span>':''}</div><div class="small text-muted">${a.full}</div></div>`;
          frag.appendChild(div);
        });
        savedWrap.innerHTML='';
        savedWrap.appendChild(frag);
        bindAddressItemEvents();
      }).catch(()=>{});
    }
    function bindAddressItemEvents(){
      document.querySelectorAll('.address-item').forEach(it=>{
        it.addEventListener('click',()=>{const radio=it.querySelector('input[type=radio]'); if(radio){radio.checked=true;} document.querySelectorAll('.address-item').forEach(o=>o.classList.remove('active')); it.classList.add('active');});
      });
    }
    const useBtn=document.getElementById('useSelectedAddress');
    if(useBtn){useBtn.addEventListener('click',()=>{
      const checked=document.querySelector('input[name="addressChoice"]:checked');
      if(!checked){alert('Chọn một địa chỉ.');return;}
      const wrap=checked.closest('.address-item');
      const full=wrap?wrap.getAttribute('data-full'):'';
      if(full){
        try{const parts=full.split(',').map(p=>p.trim());document.getElementById('diaChiGiaoHang').value=parts[0]||full;}catch(e){document.getElementById('diaChiGiaoHang').value=full;}
      }
  const selIdInput=document.getElementById('selectedAddressId');
  if(selIdInput) selIdInput.value=checked.value;
  // Disable manual dropdowns to skip validation
  ['province','district','ward'].forEach(id=>{const el=document.getElementById(id); if(el){el.disabled=true; el.removeAttribute('required'); el.classList.add('disabled');}});
  const manual=document.getElementById('manualAddressFields'); if(manual && !manual.classList.contains('collapsed')){manual.classList.add('collapsed');}
  toast?toast('Đã áp dụng địa chỉ đã chọn','success'):alert('Đã áp dụng địa chỉ đã chọn');
    });}
    const toggleBtn=document.getElementById('toggleManualAddress');
    const manual=document.getElementById('manualAddressFields');
    if(toggleBtn && manual){toggleBtn.addEventListener('click',()=>{const hidden=manual.classList.toggle('collapsed');toggleBtn.textContent=hidden?'Hiện form nhập tay':'Ẩn form nhập tay';});}
  });
})();
