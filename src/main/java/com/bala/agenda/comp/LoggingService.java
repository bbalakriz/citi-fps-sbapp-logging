package com.bala.agenda.comp;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bala.agenda.config.AgendaConfigurationWithLogging;
import com.bala.agenda.listeners.RulesAgendaEventListener;
import com.bala.agenda.listeners.RulesRuntimeEventListener;
import com.citi.ParticipantEvent;

@Service
public class LoggingService {

        @Autowired
        private AgendaConfigurationWithLogging configuration;

        static final int JUNE = 6;

        public void executeAgendaRules(String version) {
                try {
                        StatelessKieSession kieSession = configuration.getKieContainer(version)
                                        .newStatelessKieSession("fps-sl-session");

                        BatchExecutionCommandImpl command = new BatchExecutionCommandImpl();

                        loadTestData(command);
                        command.addCommand(new FireAllRulesCommand());
                        command.addCommand(new GetObjectsCommand());
                        command.addCommand(new DisposeCommand());

                        kieSession.addEventListener(new RulesAgendaEventListener());
                        kieSession.addEventListener(new RulesRuntimeEventListener());

                        kieSession.execute(command);
                } catch (Exception e) {

                }
        }

        void loadTestData(BatchExecutionCommandImpl command) {
                // create an out of bounds fact
                ParticipantEvent p = new ParticipantEvent();
                p.setId("1");
                p.setLocation("RETAIL_STORE");
                p.setRegisteredTime(ZonedDateTime.of(2021, JUNE, 4, 0, 15, 0, 0, ZoneId.of("Asia/Singapore"))
                                .toInstant()
                                .toEpochMilli());

                command.addCommand(new InsertObjectCommand(p));

                // create a stage 1 txn
                p = new ParticipantEvent();
                p.setLocation("FUEL_STATION");
                p.setId("4");
                p.setRegisteredTime(ZonedDateTime.of(2021, JUNE, 4, 4, 15, 0, 0, ZoneId.of("Asia/Singapore"))
                                .toInstant()
                                .toEpochMilli());
                command.addCommand(new InsertObjectCommand(p));
                /**
                 * STAGE 2 - create stage 2 txns
                 */
                p = new ParticipantEvent();
                p.setLocation("JEWELLERY_STORE");
                p.setId("3");
                p.setRegisteredTime(ZonedDateTime.of(2021, JUNE, 4, 2, 15, 0, 0, ZoneId.of("Asia/Singapore"))
                                .toInstant()
                                .toEpochMilli());
                command.addCommand(new InsertObjectCommand(p));

                p = new ParticipantEvent();
                p.setLocation("SUPERMARKET");
                p.setId("3a");
                p.setRegisteredTime(ZonedDateTime.of(2021, JUNE, 4, 3, 20, 0, 0, ZoneId.of("Asia/Singapore"))
                                .toInstant()
                                .toEpochMilli());
                command.addCommand(new InsertObjectCommand(p));

                p = new ParticipantEvent();
                p.setLocation("JEWELLERY_STORE");
                p.setId("3b");
                p.setRegisteredTime(ZonedDateTime.of(2021, JUNE, 4, 3, 47, 0, 0, ZoneId.of("Asia/Singapore"))
                                .toInstant()
                                .toEpochMilli());
                command.addCommand(new InsertObjectCommand(p));

                // create a stage 3 txn
                p = new ParticipantEvent();
                p.setLocation("ONLINE");
                p.setId("2");
                p.setRegisteredTime(ZonedDateTime.of(2021, JUNE, 3, 23, 48, 0, 0, ZoneId.of("Asia/Singapore"))
                                .toInstant()
                                .toEpochMilli());
                command.addCommand(new InsertObjectCommand(p));
        }
}