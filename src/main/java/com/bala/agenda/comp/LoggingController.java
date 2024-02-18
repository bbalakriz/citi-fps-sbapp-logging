package com.bala.agenda.comp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggingController {

    @Autowired
    private LoggingService ecService;

    @PostMapping("/log-rule-execution")
    private void executeAgendaRules(
            @RequestParam(required = true) String version) {
        this.ecService.executeAgendaRules(version);
    }
}
