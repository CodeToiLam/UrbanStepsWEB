// Orders page interactions extracted from template
(function(){
  function ready(fn){ if(document.readyState!=='loading'){fn()} else {document.addEventListener('DOMContentLoaded', fn)} }
  ready(function(){
    var filterLinks = document.querySelectorAll('.status-filters .nav-link');
    var list = document.getElementById('ordersList');
    if(!list){ return; }
    var cards = Array.prototype.slice.call(list.querySelectorAll('.order-card'));
    var countEl = document.getElementById('ordersCount');
    function updateCount(){
      var visible = cards.filter(function(c){ return c.style.display !== 'none'; }).length;
      if(countEl){
        var label = countEl.getAttribute('data-label') || 'ĐƠN';
        countEl.textContent = visible + ' ' + label;
      }
    }
    filterLinks.forEach(function(l){
      l.addEventListener('click', function(e){
        e.preventDefault();
        filterLinks.forEach(function(f){ f.classList.remove('active'); });
        l.classList.add('active');
        var st = l.getAttribute('data-status');
        cards.forEach(function(c){ c.style.display = (st==='all' || c.getAttribute('data-status') === st) ? 'flex' : 'none'; });
        updateCount();
      });
    });
    updateCount();
  });
})();
