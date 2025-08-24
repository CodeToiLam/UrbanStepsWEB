// JS for product variants page
(function(){
  console.log('[product-variants.js] loaded');
  document.addEventListener('DOMContentLoaded', () => {
    const kcSel = document.getElementById('kcMulti');
    const msSel = document.getElementById('msMulti');
    const tbody = document.getElementById('rowsBody');
    const genBtn = document.getElementById('genComb');
    const form = document.getElementById('bulkForm');

    if(!kcSel || !msSel || !tbody || !genBtn) return;

    // Keep track of created pairs to avoid duplicates
    function pairKey(kcId, msId) { return kcId + '::' + msId; }

    genBtn.addEventListener('click', () => {
      const kcIds = Array.from(kcSel.selectedOptions).map(o => ({id:o.value, text:o.text}));
      const msIds = Array.from(msSel.selectedOptions).map(o => ({id:o.value, text:o.text}));
      if (kcIds.length === 0 || msIds.length === 0) { alert('Vui lòng chọn kích cỡ và màu sắc.'); return; }

      // existing pairs in table
      const existing = new Set();
      Array.from(tbody.querySelectorAll('input[name="rowKcIds"]')).forEach((h, i) => {
        const kc = h.value;
        const ms = tbody.querySelectorAll('input[name="rowMsIds"]')[i]?.value || '';
        if (kc && ms) existing.add(pairKey(kc, ms));
      });

      let idx = Array.from(tbody.querySelectorAll('input[type=file]')).length;
      let added = 0;

      for (let m of msIds) {
        for (let k of kcIds) {
          if (existing.has(pairKey(k.id, m.id))) continue; // skip dup
          // safety guard: avoid creating huge tables accidentally
          if (idx >= 500) { alert('Quá nhiều biến thể (>=500). Hãy tạo ít hơn một lúc.'); break; }

          const tr = document.createElement('tr');
          tr.innerHTML = `
            <td>
              <input type="hidden" name="rowKcIds" value="${k.id}">
              <input type="hidden" name="rowMsIds" value="${m.id}">
              <span>${k.text} / ${m.text}</span>
            </td>
            <td><input type="number" name="rowQtys" min="0" value="0" class="form-control"></td>
            <td><input type="number" step="0.01" name="rowPrices" placeholder="Theo giá SP" class="form-control"></td>
            <td>
              <input type="file" name="rowImages_${idx}" accept="image/*" multiple>
              <div class="image-grid" id="grid_${idx}"></div>
            </td>
            <td><button type="button" class="btn-link remove-row">Xóa</button></td>
          `;
          tbody.appendChild(tr);

          // Hook preview for this row and remember object URLs to revoke later
          const fileInput = tr.querySelector(`input[type=file][name="rowImages_${idx}"]`);
          const grid = tr.querySelector(`#grid_${idx}`);
          const urls = [];
          fileInput.addEventListener('change', () => {
            // revoke previous urls
            urls.forEach(u => URL.revokeObjectURL(u));
            urls.length = 0;
            grid.innerHTML = '';
            Array.from(fileInput.files).forEach(f => {
              const url = URL.createObjectURL(f);
              urls.push(url);
              const wrap = document.createElement('div');
              wrap.className = 'thumb';
              wrap.innerHTML = `<img src="${url}" alt=""/>`;
              grid.appendChild(wrap);
            });
          });

          // remove button
          const removeBtn = tr.querySelector('.remove-row');
          removeBtn.addEventListener('click', () => {
            // revoke any created urls for this row
            urls.forEach(u => URL.revokeObjectURL(u));
            tr.remove();
            // reset rowCount based on current file inputs count (they are one-per-row)
            const rc = document.getElementById('rowCount'); if(rc) rc.value = tbody.querySelectorAll('input[type=file]').length;
          });

          existing.add(pairKey(k.id, m.id));
          idx++; added++;
        }
      }
      const rc = document.getElementById('rowCount'); if(rc) rc.value = tbody.querySelectorAll('input[type=file]').length;

      if (added === 0) {
        // nothing added -> maybe duplicates
        alert('Không có biến thể mới (các tổ hợp đã tồn tại trong danh sách).');
      }
    });
  });
})();
