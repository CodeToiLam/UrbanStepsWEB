// Order Management page JS: compute counts and small UX helpers
(function(){
  document.addEventListener('DOMContentLoaded', function(){
    var pending = 0, completed = 0, cancelled = 0;
    document.querySelectorAll('.status-badge').forEach(function(b){
      if (b.classList.contains('status-pending')) pending++;
      else if (b.classList.contains('status-completed')) completed++;
      else if (b.classList.contains('status-cancelled')) cancelled++;
    });
    var p = document.getElementById('pendingCount'); if (p) p.textContent = pending;
    var c = document.getElementById('completedCount'); if (c) c.textContent = completed;
    var x = document.getElementById('cancelledCount'); if (x) x.textContent = cancelled;

    // Ensure min width for select for nice layout
    document.querySelectorAll('.status-select').forEach(function(sel){
      sel.style.minWidth = '180px';
    });
  });
})();
