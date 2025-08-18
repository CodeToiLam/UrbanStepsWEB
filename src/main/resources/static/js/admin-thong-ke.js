// Admin Dashboard JS extracted from thong-ke.html
(function(){
  let revenueChart, orderStatusChart;
  const PERIOD_LABELS = { today: 'Hôm nay', week: '7 ngày qua', month: '30 ngày qua', quarter: '3 tháng qua', year: 'Năm nay' };
  const STATUS_ORDER = ['PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED', 'CANCELLED', 'RETURNED'];
  const STATUS_TEXT = { PENDING:'Chờ xử lý', CONFIRMED:'Đã xác nhận', SHIPPING:'Đang giao', COMPLETED:'Hoàn thành', CANCELLED:'Đã hủy', RETURNED:'Trả/Hoàn' };
  const STATUS_CLASS = { PENDING:'status-pending', CONFIRMED:'status-confirmed', SHIPPING:'status-shipping', COMPLETED:'status-completed', CANCELLED:'status-cancelled', RETURNED:'status-returned' };
  const STATUS_COLORS = ['#ffc107','#007bff','#17a2b8','#28a745','#dc3545','#6c757d'];

  function getSelectedPeriod(){ const el = document.getElementById('timePeriod'); return el ? el.value : 'month'; }

  document.addEventListener('DOMContentLoaded', function(){ loadDashboardData(); initCharts(); });

  async function loadDashboardData(){
    try {
      const period = getSelectedPeriod();
      const res = await fetch(`/api/thong-ke?period=${encodeURIComponent(period)}`);
      const data = await res.json();
      updateStats(data);
      updateTopProducts(data.topSanPhamBanChay || []);
      updateRecentOrders(data.donHangGanDay || []);
      updateCharts(data, period);
    } catch(e){ console.error('Load dashboard failed', e); showEmptyState(); }
  }

  function updateStats(data){
    setText('tongDoanhThu', new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(data.tongDoanhThu||0));
    setText('tongDonHang', (data.tongDonHang||0).toLocaleString('vi-VN'));
    setText('tongSanPham', (data.tongSanPham||0).toLocaleString('vi-VN'));
    setText('tongKhachHang', (data.tongKhachHang||0).toLocaleString('vi-VN'));
  }

  function updateTopProducts(products){
    const tbody = document.getElementById('topSanPhamBanChay');
    if(!products || products.length===0){
      tbody.innerHTML = `<tr><td colspan="3" class="empty-state"><i class="fa fa-shopping-bag"></i><div>Chưa có dữ liệu sản phẩm</div></td></tr>`; return;
    }
    tbody.innerHTML = products.slice(0,5).map(p=>`
      <tr>
        <td><div class="product-info"><img src="${p.imageUrl || '/images/no-image.jpg'}" alt="Product" class="product-image"><div class="product-name">${p.tenSanPham||'Sản phẩm'}</div></div></td>
        <td><span class="quantity-badge">${(p.soLuongBan||0).toLocaleString('vi-VN')}</span></td>
        <td><span class="revenue-amount">${new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(p.doanhThu||0)}</span></td>
      </td>
    `).join('');
  }

  function updateRecentOrders(orders){
    const tbody = document.getElementById('donHangGanDay');
    if(!orders || orders.length===0){
      tbody.innerHTML = `<tr><td colspan="4" class="empty-state"><i class="fa fa-shopping-cart"></i><div>Chưa có đơn hàng nào</div></td></tr>`; return;
    }
    tbody.innerHTML = orders.slice(0,5).map(o=>{
      const norm = normalizeStatus(o.trangThai);
      return `
        <tr>
          <td><strong>${o.maHoaDon || o.code || 'N/A'}</strong></td>
          <td>${o.tenKhachHang || o.customerName || 'Khách vãng lai'}</td>
          <td class="revenue-amount">${new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(o.tongTien ?? o.totalAmount ?? 0)}</td>
          <td><span class="status-badge ${STATUS_CLASS[norm]||'status-pending'}">${STATUS_TEXT[norm]||STATUS_TEXT.PENDING}</span></td>
        </tr>`;
    }).join('');
  }

  function initCharts(){
    const revenueCtx = document.getElementById('revenueChart').getContext('2d');
    revenueChart = new Chart(revenueCtx, { type:'line', data:{ labels:[], datasets:[{ label:'Doanh thu', data:[], borderColor:'#28a745', backgroundColor:'rgba(40,167,69,0.1)', borderWidth:3, fill:true, tension:0.4 }] }, options:{ responsive:true, maintainAspectRatio:false, plugins:{ legend:{ display:false } }, scales:{ y:{ beginAtZero:true, ticks:{ callback:(v)=> new Intl.NumberFormat('vi-VN').format(v)+'đ' }}}}});

    const statusCtx = document.getElementById('orderStatusChart').getContext('2d');
    orderStatusChart = new Chart(statusCtx, {
      type:'doughnut',
      data:{
        labels: STATUS_ORDER.map(s=>STATUS_TEXT[s]),
        datasets:[{
          data:new Array(STATUS_ORDER.length).fill(0),
          backgroundColor: STATUS_COLORS,
          borderWidth:0
        }]
      },
      options:{
        responsive:true,
        maintainAspectRatio:false,
        plugins:{ legend:{ display:false } }
      }
    });
    // initial legend render
    renderStatusLegend(STATUS_ORDER.map(s=>0));
  }

  function updateCharts(data, period){
    const periodSpan = document.querySelector('.chart-card .chart-header .chart-period');
    if(periodSpan) periodSpan.textContent = PERIOD_LABELS[period] || 'Khoảng thời gian';

    const series = data.revenueSeries || data.revenue7d || {};
    const keys = Object.keys(series);
    const labels = keys.map(k=>{ const d=new Date(k); return isNaN(d)?k:d.toLocaleDateString('vi-VN',{day:'2-digit',month:'2-digit'}); });
    const values = keys.map(k=> series[k] || 0);
    revenueChart.data.labels = labels; revenueChart.data.datasets[0].data = values; revenueChart.update();

  const os = coerceOrderStatus(data.orderStatus || data.orderStatusCounts || []);
  const statusData = STATUS_ORDER.map(s => os[s] || 0);
  orderStatusChart.data.labels = STATUS_ORDER.map(s=>STATUS_TEXT[s]);
  orderStatusChart.data.datasets[0].data = statusData; orderStatusChart.update();
  renderStatusLegend(statusData);
  }

  function coerceOrderStatus(input){
    const result = {};
    if(input && !Array.isArray(input)){
      Object.keys(input).forEach(k=>{ const key = normalizeStatus(k); if(key) result[key] = (result[key]||0) + (input[k]||0); });
      return result;
    }
    if(Array.isArray(input)){
      input.forEach(item=>{ const key = normalizeStatus(item.status ?? item.trangThai); const count = Number(item.count ?? item.soLuong ?? 0); if(key) result[key] = (result[key]||0)+count; });
    }
    return result;
  }

  function normalizeStatus(status){
    if(status===null || status===undefined) return 'PENDING';
    const n = Number(status);
    if(!isNaN(n)){
      // align with HoaDon: 0 pending, 1 confirmed, 2 shipping, 3 completed, 4 cancelled, 5 paid, 6 returned
      const map = { 0:'PENDING', 1:'CONFIRMED', 2:'SHIPPING', 3:'COMPLETED', 4:'CANCELLED', 5:'PENDING', 6:'RETURNED' };
      return map[n] || 'PENDING';
    }
    const s = String(status).trim().toUpperCase();
    const alias = { PENDING:'PENDING', NEW:'PENDING', CONFIRMED:'CONFIRMED', APPROVED:'CONFIRMED', SHIPPING:'SHIPPING', SHIPPED:'SHIPPING', DELIVERED:'COMPLETED', COMPLETED:'COMPLETED', DONE:'COMPLETED', CANCELLED:'CANCELLED', CANCELED:'CANCELLED', RETURNED:'RETURNED', REFUNDED:'RETURNED' };
    return alias[s] || 'PENDING';
  }

  function setText(id, text){ const el=document.getElementById(id); if(el) el.textContent = text; }
  function showEmptyState(){ setText('tongDoanhThu','₫0'); setText('tongDonHang','0'); setText('tongSanPham','0'); setText('tongKhachHang','0'); }

  function renderStatusLegend(values){
    const container = document.getElementById('orderStatusLegend');
    if(!container) return;
    const total = values.reduce((a,b)=>a+b,0) || 0;
    container.innerHTML = STATUS_ORDER.map((key, idx)=>{
      const label = STATUS_TEXT[key];
      const val = values[idx] || 0;
      const percent = total ? Math.round(val*100/total) : 0;
      return `
        <div class="legend-item">
          <span class="legend-color" style="background:${STATUS_COLORS[idx]}"></span>
          <span class="legend-label">${label}</span>
          <span class="legend-value">${val.toLocaleString('vi-VN')} (${percent}%)</span>
        </div>
      `;
    }).join('');
  }

  // Expose refresh
  window.refreshData = function(){ loadDashboardData(); };
})();
