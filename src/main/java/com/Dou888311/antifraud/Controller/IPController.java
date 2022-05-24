package com.Dou888311.antifraud.Controller;

import com.Dou888311.antifraud.DTO.IpDeleteResponse;
import com.Dou888311.antifraud.Entity.IP;
import com.Dou888311.antifraud.Service.IPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class IPController {

    private IPService ipService;

    @Autowired
    public IPController(IPService ipService) {
        this.ipService = ipService;
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public IP ipRegister(@RequestBody @Valid IP ip) {
        return ipService.ipReg(ip);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public IpDeleteResponse ipDelete(@PathVariable String ip) {
        return ipService.ipDelete(ip);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public List<IP> ipGet() {
        return ipService.ipGet();
    }

}
