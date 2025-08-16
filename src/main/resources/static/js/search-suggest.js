(function(){
  const wrappers = document.querySelectorAll('[data-search]');
  if(!wrappers || wrappers.length === 0) return;

  const DEBOUNCE = 200;

  wrappers.forEach(wrapper => {
    const input = wrapper.querySelector('[data-search-input]');
    const dropdown = wrapper.querySelector('[data-search-dropdown]');
    if(!input || !dropdown) return;

    let timer = null;
    let lastQuery = '';

    function hide(){ dropdown.hidden = true; dropdown.innerHTML=''; }
    function show(){ dropdown.hidden = false; }

    function render(items, q){
      if(!items || items.length===0){ hide(); return; }
      const esc = (s)=> (s||'').toString().replace(/[&<>"']/g, m=>({"&":"&amp;","<":"&lt;",
        ">":"&gt;","\"":"&quot;","'":"&#39;"}[m]));
      const hi = (text)=>{
        if(!q) return esc(text);
        try{
          const re = new RegExp('('+q.replace(/[.*+?^${}()|[\]\\]/g,'\\$&')+')','ig');
          return esc(text).replace(re,'<mark>$1</mark>');
        }catch{ return esc(text); }
      };
      dropdown.innerHTML = items.map(it=>`<a class="sug-item" href="${esc(it.url)}">
        <img class="sug-img" src="${esc(it.image||'/images/no-image.svg')}" alt="" onerror="this.src='/images/no-image.svg'"/>
        <div class="sug-meta">
          <div class="sug-name">${hi(it.name||'')}</div>
          <div class="sug-sub">${esc(it.brand||'')}${it.price? ' · '+ new Intl.NumberFormat('vi-VN').format(it.price) + 'đ':''}</div>
        </div>
      </a>`).join('');
      show();
    }

    function fetchSuggest(q){
      if(!q){ hide(); return; }
      lastQuery = q;
      fetch(`/api/search/suggest?q=${encodeURIComponent(q)}`)
        .then(r=> r.ok ? r.json() : [])
        .then(data=>{
          // ignore late responses for this specific input
          if(input.value.trim() !== lastQuery) return;
          render(Array.isArray(data)? data: [], q);
        })
        .catch(()=> hide());
    }

    input.addEventListener('input', ()=>{
      const q = input.value.trim();
      if(timer) clearTimeout(timer);
      timer = setTimeout(()=> fetchSuggest(q), DEBOUNCE);
    });
    input.addEventListener('focus', ()=>{ if(dropdown.innerHTML) show(); });
    // delay hide to allow click on dropdown links
    input.addEventListener('blur', ()=> setTimeout(hide, 150));
    // Close on Escape
    input.addEventListener('keydown', (e)=>{ if(e.key==='Escape') hide(); });
  });
})();
