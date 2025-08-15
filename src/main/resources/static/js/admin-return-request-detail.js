// Page-specific behaviors for admin return request detail
(function(){
  // Bind all approve buttons via data attribute to avoid duplicate IDs
  document.querySelectorAll('[data-approve-btn]').forEach(function(btn){
    btn.addEventListener('click', function(){
      if(!confirm('Xác nhận phê duyệt?')) return;
      const noteEl = document.getElementById('adminNote');
      const hidden = document.getElementById('approveAdminNote');
      if(hidden){ hidden.value = noteEl ? noteEl.value : ''; }
      const form = document.getElementById('approveForm');
      if(form){ form.submit(); }
    });
  });

  // Fallback open reject modal via JS and prefill textarea with current note if empty
  const rbtn = document.getElementById('rejectBtn');
  if(rbtn){
    rbtn.addEventListener('click', function(){
      const modalEl = document.getElementById('rejectModal');
      if(window.bootstrap && modalEl){
        try { new bootstrap.Modal(modalEl).show(); } catch (e) {}
      }
      const adminNote = document.getElementById('adminNote');
      if(adminNote){
        const ta = modalEl ? modalEl.querySelector('textarea[name="reason"]') : null;
        if(ta && (!ta.value || ta.value.trim() === '')){ ta.value = adminNote.value; }
        if(ta){ ta.setAttribute('required', 'required'); }
      }
    });
  }
})();

function openImageModal(src){
  const modalImg = document.getElementById('modalImage');
  if(modalImg){ modalImg.src = src; }
  const modalEl = document.getElementById('imageModal');
  if(window.bootstrap && modalEl){ new bootstrap.Modal(modalEl).show(); }
}
