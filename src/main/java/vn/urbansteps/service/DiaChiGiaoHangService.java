package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.DiaChiGiaoHang;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.DiaChiGiaoHangRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiaChiGiaoHangService {
    @Autowired
    private DiaChiGiaoHangRepository repo;

    public List<DiaChiGiaoHang> listByTaiKhoan(TaiKhoan tk){
        return repo.findByTaiKhoanOrderByIdDesc(tk);
    }

    public DiaChiGiaoHang save(DiaChiGiaoHang d){
        return repo.save(d);
    }

    public Optional<DiaChiGiaoHang> findById(Integer id){ return repo.findById(id); }

    public void delete(Integer id){ repo.deleteById(id); }
}
