// KichCoService.java
package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.KichCo;
import vn.urbansteps.repository.KichCoRepository;

import java.util.List;

@Service
public class KichCoService {
    @Autowired
    private KichCoRepository kichCoRepository;

    public List<KichCo> getAllKichCos() {
        return kichCoRepository.findAll();
    }
}
