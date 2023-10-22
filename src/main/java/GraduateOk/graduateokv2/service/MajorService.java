package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MajorService {

    private final MajorRepository majorRepository;
}
