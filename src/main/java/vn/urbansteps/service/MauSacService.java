package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.MauSac;
import vn.urbansteps.repository.MauSacRepository;

import java.util.List;

@Service
public class MauSacService {
    @Autowired
    private MauSacRepository mauSacRepository;

    public List<MauSac> getAllMauSacs() {
        return mauSacRepository.findAll();
    }
}
