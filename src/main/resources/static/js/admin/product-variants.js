// JS extracted for product variants page
(function(){
  document.addEventListener('DOMContentLoaded', () => {
    const kcSel = document.getElementById('kcMulti');
    const msSel = document.getElementById('msMulti');
    const tbody = document.getElementById('rowsBody');
    const genBtn = document.getElementById('genComb');
    const form = document.getElementById('bulkForm');

    if(!kcSel || !msSel || !tbody || !genBtn) return;

    genBtn.addEventListener('click', () => {
      const kcIds = Array.from(kcSel.selectedOptions).map(o => ({id:o.value, text:o.text}));
      const msIds = Array.from(msSel.selectedOptions).map(o => ({id:o.value, text:o.text}));
      tbody.innerHTML = '';
      if (kcIds.length === 0 || msIds.length === 0) { alert('Vui lòng chọn kích cỡ và màu sắc.'); return; }
      let idx = 0;
      msIds.forEach(m => {
        kcIds.forEach(k => {
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
          `;
          tbody.appendChild(tr);
          // Hook preview for this row
          const fileInput = tr.querySelector(`input[type=file][name="rowImages_${idx}"]`);
          const grid = tr.querySelector(`#grid_${idx}`);
          fileInput.addEventListener('change', () => {
            grid.innerHTML = '';
            Array.from(fileInput.files).forEach(f => {
              const url = URL.createObjectURL(f);
              const wrap = document.createElement('div');
              wrap.className = 'thumb';
              wrap.innerHTML = `<img src="${url}" alt=""/>`;
              grid.appendChild(wrap);
            });
          });
          idx++;
        });
      });
      const rc = document.getElementById('rowCount'); if(rc) rc.value = idx;
    });
  });
})();
