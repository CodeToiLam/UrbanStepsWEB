document.addEventListener('DOMContentLoaded',()=>{
  // Placeholder for dynamic province/district/ward loader or geolocation
  const geoBtn = document.querySelector('[data-action="geo-locate"]');
  if(geoBtn){
    geoBtn.addEventListener('click',()=>{
      if(!navigator.geolocation){
        alert('Trình duyệt không hỗ trợ định vị');
        return;
      }
      geoBtn.disabled=true; geoBtn.textContent='Đang lấy vị trí...';
      navigator.geolocation.getCurrentPosition(pos=>{
        const {latitude, longitude}=pos.coords;
        console.log('Vị trí hiện tại:', latitude, longitude);
        geoBtn.textContent='Đã lấy vị trí';
        geoBtn.classList.add('btn-success');
        geoBtn.dataset.lat=latitude; geoBtn.dataset.lng=longitude;
        // TODO: gửi lên server hoặc reverse geocode
      },err=>{
        alert('Không lấy được vị trí: '+err.message);
        geoBtn.disabled=false; geoBtn.textContent='Lấy vị trí';
      });
    });
  }
});
