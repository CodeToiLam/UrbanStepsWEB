// Admin Return Request actions (single implementation)
// Use standard form POST with CSRF for best compatibility and natural redirects
(function(){
  function resolveCsrf(){
    const csrfParamMeta = document.querySelector('meta[name="_csrf_parameter"]');
    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    if(csrfParamMeta && csrfTokenMeta){
      return { name: csrfParamMeta.content, token: csrfTokenMeta.content };
    }
    const hidden = document.querySelector('input[type="hidden"][name][value][name*="_csrf"], input[type="hidden"][name="_csrf"]');
    if(hidden){ return { name: hidden.name, token: hidden.value }; }
    return null;
  }

  function post(url, data){
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = url;
    const csrf = resolveCsrf();
    if(csrf){
      const input = document.createElement('input');
      input.type = 'hidden'; input.name = csrf.name; input.value = csrf.token; form.appendChild(input);
    } else {
      console.warn('CSRF token not found. Ensure layout includes CSRF meta tags or hidden input.');
    }
    if(data){
      Object.entries(data).forEach(([k,v])=>{
        const input=document.createElement('input'); input.type='hidden'; input.name=k; input.value=v??''; form.appendChild(input);
      });
    }
    document.body.appendChild(form);
    form.submit();
  }

  // Use Vietnamese-friendly endpoints (controller supports both EN and VI mappings)
  window.markAsProcessing = function(id){ if(id){ post('/admin/yeu-cau-tra-hang/' + id + '/processing'); } };
  window.approveRequest = function(id){ if(id){ const noteEl = document.getElementById('adminNote'); const adminNote = noteEl ? noteEl.value : ''; post('/admin/yeu-cau-tra-hang/' + id + '/approve', { adminNote }); } };
  window.rejectRequest = function(id){
    if(!id) return;
    let reason = '';
    while(true){
      reason = prompt('Nhập lý do từ chối (bắt buộc):');
      if(reason === null) return; // user cancelled
      if(reason && reason.trim().length>0) break;
      alert('Vui lòng nhập lý do từ chối.');
    }
    post('/admin/yeu-cau-tra-hang/' + id + '/reject', { reason: reason.trim() });
  };
})();
