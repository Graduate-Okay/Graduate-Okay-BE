package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CollegeService {

    private final CollegeRepository collegeRepository;
}
