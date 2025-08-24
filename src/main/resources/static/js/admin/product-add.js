// Extracted JS from product-add.html
(function(){
  function renderPreview(input, container, single){
    const wrap = document.getElementById(container); if(!wrap) return; if(single) wrap.innerHTML='';
    const files = input.files||[]; Array.from(files).forEach(f=>{ const url=URL.createObjectURL(f); const d=document.createElement('div'); d.className='thumb'; d.innerHTML=`<img src="${url}" alt="">`; wrap.appendChild(d); });
  }
  document.addEventListener('DOMContentLoaded',()=>{
    const main=document.getElementById('mainImage'); const detail=document.getElementById('detailImages');
    if(main) main.addEventListener('change',()=>renderPreview(main,'mainImagePreview',true));
    if(detail) detail.addEventListener('change',()=>{ const cont=document.getElementById('detailImagesPreview'); if(cont) cont.innerHTML=''; renderPreview(detail,'detailImagesPreview'); });

    // Gallery drag-drop + actions when editing
    const ul = document.getElementById('productGallery');
    if(ul){
      let dragEl = null;
      ul.addEventListener('dragstart', e=>{
        const li = e.target.closest('.gallery-item');
        if(!li) return; dragEl = li; li.classList.add('dragging');
        if(e.dataTransfer) e.dataTransfer.effectAllowed = 'move';
      });
      ul.addEventListener('dragend', ()=>{
        if(dragEl){ dragEl.classList.remove('dragging'); dragEl = null; }
        persistOrder();
      });
      ul.addEventListener('dragover', e=>{
        e.preventDefault();
        const after = getDragAfterElement(ul, e.clientY);
        const dragging = ul.querySelector('.gallery-item.dragging');
        if(!dragging) return;
        if(after == null){ ul.appendChild(dragging); }
        else { ul.insertBefore(dragging, after); }
      });

      function getDragAfterElement(container, y){
        const els = Array.from(container.querySelectorAll('.gallery-item:not(.dragging)'));
        return els.reduce((closest, child)=>{
          const box = child.getBoundingClientRect();
          const offset = y - box.top - box.height/2;
          if(offset < 0 && offset > closest.offset){ return { offset, element: child }; }
          else return closest;
        }, { offset: Number.NEGATIVE_INFINITY }).element;
      }

      function persistOrder(){
        const ids = Array.from(ul.querySelectorAll('.gallery-item')).map(li=>parseInt(li.dataset.linkId));
        const productId = ul.dataset.productId;
        fetch(`/admin/products/${productId}/gallery/reorder`,{
          method:'POST', headers:{'Content-Type':'application/json'},
          body: JSON.stringify({ ids })
        }).then(r=>{ if(!r.ok) throw new Error('Reorder failed'); return r.json(); }).catch(console.error);
      }

      ul.addEventListener('click', e=>{
        const li = e.target.closest('.gallery-item'); if(!li) return;
        const productId = ul.dataset.productId; const linkId = parseInt(li.dataset.linkId);
        if(e.target.classList.contains('btn-star')){
          fetch(`/admin/products/${productId}/gallery/set-primary`,{
            method:'POST', headers:{'Content-Type':'application/json'},
            body: JSON.stringify({ linkId })
          }).then(r=>r.ok?r.json():Promise.reject('Set primary failed'))
            .then(()=>{
              ul.querySelectorAll('.btn-star').forEach(b=>b.classList.remove('active'));
              e.target.classList.add('active');
            }).catch(console.error);
        }
        if(e.target.classList.contains('btn-remove')){
          if(!confirm('Xóa ảnh này khỏi gallery?')) return;
          fetch(`/admin/products/${productId}/gallery/${linkId}`,{ method:'DELETE' })
            .then(r=>{ if(!r.ok) throw new Error('Delete failed'); li.remove(); persistOrder(); })
            .catch(console.error);
        }
      });
    }

    // variants bulk form: set rowCount before submit
    const variantsForm = document.getElementById('variantsBulkForm');
    if(variantsForm){
      variantsForm.addEventListener('submit', (ev)=>{
        const tbody = document.getElementById('variantsTableBody');
        if(!tbody) return;
        const rows = Array.from(tbody.querySelectorAll('tr')).filter(r=>r.querySelector('input[name="rowKcIds"]'));
        const count = rows.length;
        const rowCountInput = document.getElementById('rowCount');
        if(rowCountInput) rowCountInput.value = count;
        // ensure that selects with name rowStatus are transformed into values suitable for controller
        // controller expects rowKcIds,rowMsIds,rowQtys,rowPrices (lists). rowStatus is optional; backend ignores unknown params.
      });
    }
    // inline save per variant
    document.addEventListener('click', (e)=>{
      const btn = e.target.closest('.save-variant'); if(!btn) return;
      const tr = btn.closest('tr'); if(!tr) return;
      const variantId = btn.dataset.id;
      const qtyInput = tr.querySelector('input[name="rowQtys"]');
      const priceInput = tr.querySelector('input[name="rowPrices"]');
      const statusSelect = tr.querySelector('select[name="rowStatus"]');
      const productId = document.querySelector('#productGallery') ? document.querySelector('#productGallery').dataset.productId : null;
      const data = new URLSearchParams();
      if(qtyInput) data.append('soLuong', qtyInput.value);
      if(priceInput) data.append('giaBanLe', priceInput.value);
      if(statusSelect) data.append('trangThai', statusSelect.value);
      if(!productId || !variantId) { alert('Không xác định sản phẩm/biến thể'); return; }
      fetch(`/admin/products/${productId}/variants/${variantId}`,{
        method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'}, body: data.toString()
      }).then(r=>{ if(!r.ok) throw new Error('Update failed'); return r.json(); })
        .then(j=>{
          // reload page to refresh totals or update totalStock if returned
          location.reload();
        }).catch(err=>{ console.error(err); alert('Lưu biến thể thất bại'); });
    });
  });
})();
