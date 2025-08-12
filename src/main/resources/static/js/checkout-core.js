// Core checkout behaviors: voucher apply, QR toggle, basic validations
(function(){
  const voucherInput = document.getElementById('voucherCode');
  const applyBtn = document.getElementById('applyVoucherBtn');
  if(applyBtn){
    applyBtn.addEventListener('click', () => {
      const code = voucherInput?.value.trim();
      if(!code){toast('Vui lòng nhập mã giảm giá');return;}
      fetch(`/api/voucher/apply?code=${encodeURIComponent(code)}`)
        .then(r=>r.json())
        .then(j=>{
          if(j.success){
            toast('Áp dụng mã thành công');
            // update totals if provided
            if(j.totalAfterDiscount!==undefined){
              const el = document.getElementById('order-total');
              if(el) el.textContent = j.totalAfterDiscount.toLocaleString('vi-VN');
            }
          } else toast(j.message||'Mã không hợp lệ');
        }).catch(()=>toast('Lỗi áp dụng mã'));
    });
  }

  // QR toggle (support both radios and the current select UI)
  const qrSection = document.getElementById('qr-section');
  function refreshQR(){
    if(!qrSection) return;
    let show = false;
    const select = document.querySelector('select[name="phuongThucThanhToan"]');
    if(select){ show = (select.value === '2'); }
    else {
      const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;
      show = (method === 'QR');
    }
    qrSection.style.display = show ? 'block':'none';
    if(show){
      // update transfer content hint
      const name = document.getElementById('hoTen')?.value?.trim()||'';
      const phone = document.getElementById('sdt')?.value?.trim()||'';
      const contentEl = document.getElementById('qr-content');
      if(contentEl){ contentEl.textContent = `Thanh toán UrbanSteps${phone?` - ${phone}`:''}`; }
    }
  }
  const selectPM = document.querySelector('select[name="phuongThucThanhToan"]');
  if(selectPM){ selectPM.addEventListener('change',refreshQR); }
  document.querySelectorAll('input[name="paymentMethod"]').forEach(r=>r.addEventListener('change',refreshQR));
  refreshQR();

  // lightweight toast system
  function ensureToastContainer(){
    let c = document.getElementById('toast-container');
    if(!c){
      c=document.createElement('div');
      c.id='toast-container';
      c.style.cssText='position:fixed;top:1rem;right:1rem;z-index:1055;display:flex;flex-direction:column;gap:.5rem;';
      document.body.appendChild(c);
    }
    return c;
  }
  window.toast = function(msg,type='info'){
    const c = ensureToastContainer();
    const item=document.createElement('div');
    const cfg={
      info:{bg:'#E3F2FD',bd:'#90CAF9',color:'#0D47A1',icon:'ℹ️'},
      success:{bg:'#E8F5E9',bd:'#A5D6A7',color:'#1B5E20',icon:'✔️'},
      error:{bg:'#FDECEA',bd:'#FFAB91',color:'#B71C1C',icon:'⚠️'}
    };
    const st=cfg[type]||cfg.info;
    item.innerHTML=`<span style="font-size:1rem;margin-right:.5rem;">${st.icon}</span><span>${msg}</span>`;
    item.style.cssText=`display:flex;align-items:center;gap:.25rem;background:${st.bg};border:1px solid ${st.bd};color:${st.color};padding:.7rem 1rem;border-radius:10px;box-shadow:0 4px 16px rgba(0,0,0,.12);opacity:0;transform:translateY(-6px);transition:.25s;font-size:.85rem;font-weight:500;min-width:240px;`;
    c.appendChild(item);
    requestAnimationFrame(()=>{item.style.opacity='1';item.style.transform='translateY(0)';});
    setTimeout(()=>{item.style.opacity='0';item.style.transform='translateY(-6px)';setTimeout(()=>item.remove(),300);},3500);
  };
})();
