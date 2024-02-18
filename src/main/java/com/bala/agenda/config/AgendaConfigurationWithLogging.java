package com.bala.agenda.config;

import javax.annotation.PostConstruct;

import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgendaConfigurationWithLogging {

	static {
		System.setProperty("kie.maven.settings.custom",
				"/Users/bbalasub/config/settings.xml");
		// System.setProperty("kie.maven.offline.force", "true");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AgendaConfigurationWithLogging.class);

	static String currentVersion = "3.0.0"; // can be read from a property file
	static KieContainer kContainer;
	static KieContainerSessionsPool kcSessionPool;
	static boolean isInitializingNewVersion = false; // to prevent concurrent reinitializations for new version

	final static String GROUP_ID = "com.citi";
	final static String ARTIFACT_ID = "rule-logging";

	@PostConstruct
	public void initializeKieContainer() {
		initialize(currentVersion);
	}

	public KieContainer getKieContainer(final String newVersion) {

		if (!currentVersion.equalsIgnoreCase(newVersion) && !isInitializingNewVersion) {
			createKieContainer(newVersion); // call async new container creation
		}

		return kContainer;
	}

	/**
	 * Aysnchronously create and update the context with a new version of
	 * KieContainer
	 * 
	 * @param version
	 */
	void createKieContainer(final String version) {
		isInitializingNewVersion = true;
		Thread newThread = new Thread(() -> {
			initialize(version);
		});
		newThread.start();

	}

	/**
	 * initialize a given version of KieContainer
	 * 
	 * @param version
	 */
	void initialize(final String version) {
		LOGGER.info("New version used to initialize is: " + version);
		KieServices kieServices = KieServices.Factory.get();
		ReleaseId releaseId = kieServices.newReleaseId(GROUP_ID, ARTIFACT_ID, version);
		KieContainer kContainerForNewVersion = kieServices.newKieContainer(releaseId);

		// check if the kiecontainer is loaded without any issues
		performSanityCheck(kContainerForNewVersion);

		// finally update the context with the new version
		currentVersion = version;
		kContainer = kContainerForNewVersion;
		// =====>
		kcSessionPool = kContainer.newKieSessionsPool(10);
		// =====>
		isInitializingNewVersion = false;
	}

	private void performSanityCheck(KieContainer kContainer) {
		// Check if all the resources are loaded correctly
		Results results = kContainer.verify();
		results.getMessages().stream().forEach((message) -> {
			LOGGER.info(">> Message ( {} ): {}", message.getLevel(), message.getText());
		});

		// If there is any Error, stop and correct it
		boolean hasError = results.hasMessages(Message.Level.ERROR);
		LOGGER.info("Any Error : {}", hasError);
		if (hasError) {
			throw new UnsupportedOperationException();
		}

		// Make sure that the expected KieBases and KieSessions are loaded.
		kContainer.getKieBaseNames().stream().map((kieBase) -> {
			LOGGER.info(">> Loading KieBase: {}", kieBase);
			return kieBase;
		}).forEach((kieBase) -> {
			kContainer.getKieSessionNamesInKieBase(kieBase).stream().forEach((kieSession) -> {
				LOGGER.info("\t >> Containing KieSession: {}", kieSession);
			});
		});

	}

	public static void main(String[] args) {
		KieServices kieServices = KieServices.Factory.get();
		ReleaseId releaseId = kieServices.newReleaseId(GROUP_ID, ARTIFACT_ID, currentVersion);

		KieContainer kContainer = kieServices.newKieContainer(releaseId);
		System.out.println(kContainer);
	}
}