package com.Dou888311.antifraud.Service;

import com.Dou888311.antifraud.Config.WebSecurityConfig;
import com.Dou888311.antifraud.DTO.IpDeleteResponse;
import com.Dou888311.antifraud.Entity.IP;
import com.Dou888311.antifraud.repository.SuspiciousIpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class IPService {

    private WebSecurityConfig webConfig;
    private SuspiciousIpRepository ipRepository;

    @Autowired
    public IPService(WebSecurityConfig webConfig, SuspiciousIpRepository ipRepository) {
        this.webConfig = webConfig;
        this.ipRepository = ipRepository;
    }

    public IP ipReg(IP ip) {
        if (ipRepository.existsByIp(ip.getIp())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        return ipRepository.save(ip);
    }

    public IpDeleteResponse ipDelete(String address) {
        if (!ipValidCheck(address)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        IP ip = Optional
                .ofNullable(ipRepository.findIPByIp(address))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ipRepository.delete(ip);
        return new IpDeleteResponse(address);
    }

    public List<IP> ipGet() {
        return ipRepository.findAll();
    }

    public boolean ipValidCheck(String address) {
        String regex = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])." +
                "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
        return address.matches(regex);
    }
}
