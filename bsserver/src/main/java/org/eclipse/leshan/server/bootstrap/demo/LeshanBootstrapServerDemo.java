/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * <p>
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * <p>
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Achim Kraus (Bosch Software Innovations GmbH) - add parameter for
 *                                                     configuration filename
 *******************************************************************************/

package org.eclipse.leshan.server.bootstrap.demo;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.leshan.core.demo.cli.ShortErrorMessageHandler;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.server.bootstrap.EditableBootstrapConfigStore;
import org.eclipse.leshan.server.bootstrap.demo.cli.LeshanBsServerDemoCLI;
import org.eclipse.leshan.server.bootstrap.demo.servlet.BootstrapServlet;
import org.eclipse.leshan.server.bootstrap.demo.servlet.EventServlet;
import org.eclipse.leshan.server.bootstrap.demo.servlet.ServerServlet;
import org.eclipse.leshan.server.californium.bootstrap.LeshanBootstrapServer;
import org.eclipse.leshan.server.californium.bootstrap.LeshanBootstrapServerBuilder;
import org.eclipse.leshan.server.core.demo.json.servlet.SecurityServlet;
import org.eclipse.leshan.server.model.VersionedBootstrapModelProvider;
import org.eclipse.leshan.server.security.BootstrapSecurityStoreAdapter;
import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.server.security.FileSecurityStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import java.util.List;

public class LeshanBootstrapServerDemo {

    static {
        // Define a default logback.configurationFile
        String property = System.getProperty("logback.configurationFile");
        if (property == null) {
            System.setProperty("logback.configurationFile", "logback-config.xml");
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(LeshanBootstrapServerDemo.class);
    private static final String CF_CONFIGURATION_FILENAME = "Californium3.bsserver.properties";
    private static final String CF_CONFIGURATION_HEADER =
        "Leshan Bootstrap Server Demo - " + Configuration.DEFAULT_HEADER;

    public static void main(String[] args) {

        // Parse command line
        LeshanBsServerDemoCLI cli = new LeshanBsServerDemoCLI();
        CommandLine command = new CommandLine(cli).setParameterExceptionHandler(new ShortErrorMessageHandler());
        // Handle exit code error
        int exitCode = command.execute(args);
        if (exitCode != 0) System.exit(exitCode);
        // Handle help or version command
        if (command.isUsageHelpRequested() || command.isVersionHelpRequested()) System.exit(0);

        try {
            // Create Stores
            EditableBootstrapConfigStore bsConfigStore = new JSONFileBootstrapStore(cli.main.configFilename);
            EditableSecurityStore securityStore = new FileSecurityStore("data/bssecurity.data");

            // Create LWM2M Server
            LeshanBootstrapServer lwm2mBsServer = createBsLeshanServer(cli, bsConfigStore, securityStore);

            // Create Web Server
            Server webServer = createJettyServer(cli, lwm2mBsServer, bsConfigStore, securityStore);

            // Start servers
            lwm2mBsServer.start();
            webServer.start();
            LOG.info("Web server started at {}.", webServer.getURI());
        } catch (Exception e) {

            // Handler Execution Error
            PrintWriter printer = command.getErr();
            printer.print(command.getColorScheme().errorText("Unable to create and start server ..."));
            printer.printf("%n%n");
            printer.print(command.getColorScheme().stackTraceText(e));
            printer.flush();
            System.exit(1);
        }
    }

    public static LeshanBootstrapServer createBsLeshanServer(
        LeshanBsServerDemoCLI cli, EditableBootstrapConfigStore bsConfigStore, EditableSecurityStore securityStore
    ) {
        // Prepare LWM2M server
        LeshanBootstrapServerBuilder builder = new LeshanBootstrapServerBuilder();

        // Create CoAP Config
        File configFile = new File(CF_CONFIGURATION_FILENAME);
        Configuration coapConfig = LeshanBootstrapServerBuilder.createDefaultCoapConfiguration();
        // these configuration values are always overwritten by CLI
        // therefore set them to transient.
        coapConfig.setTransient(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY);
        coapConfig.setTransient(DtlsConfig.DTLS_CONNECTION_ID_LENGTH);
        if (configFile.isFile()) {
            coapConfig.load(configFile);
        } else {
            coapConfig.store(configFile, CF_CONFIGURATION_HEADER);
        }
        builder.setCoapConfig(coapConfig);

        // ports from CoAP Config if needed
        builder.setLocalAddress(cli.main.localAddress,
            cli.main.localPort == null ? coapConfig.get(CoapConfig.COAP_PORT) : cli.main.localPort);
        builder.setLocalSecureAddress(cli.main.secureLocalAddress,
            cli.main.secureLocalPort == null ? coapConfig.get(CoapConfig.COAP_SECURE_PORT) : cli.main.secureLocalPort);

        // Create DTLS Config
        DtlsConnectorConfig.Builder dtlsConfig = DtlsConnectorConfig.builder(coapConfig);
        dtlsConfig.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, !cli.dtls.supportDeprecatedCiphers);
        if (cli.dtls.cid != null) {
            dtlsConfig.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, cli.dtls.cid);
        }

        if (cli.identity.isx509()) {
            // use X.509 mode (+ RPK)
            builder.setPrivateKey(cli.identity.getPrivateKey());
            builder.setCertificateChain(cli.identity.getCertChain());
            builder.setTrustedCertificates(cli.identity.getTrustStore().toArray(new Certificate[0]));
        } else if (cli.identity.isRPK()) {
            // use RPK only
            builder.setPublicKey(cli.identity.getPublicKey());
            builder.setPrivateKey(cli.identity.getPrivateKey());
        }

        // Set DTLS Config
        builder.setDtlsConfig(dtlsConfig);

        // Create Models
        List<ObjectModel> models = ObjectLoader.loadDefault();
        if (cli.main.modelsFolder != null) {
            models.addAll(ObjectLoader.loadObjectsFromDir(cli.main.modelsFolder, true));
        }
        builder.setObjectModelProvider(new VersionedBootstrapModelProvider(models));

        builder.setConfigStore(bsConfigStore);
        builder.setSecurityStore(new BootstrapSecurityStoreAdapter(securityStore));

        // TODO OSCORE Temporary cli option to deactivate OSCORE
        if (!cli.main.disableOscore) {
            builder.setEnableOscore(true);
        }

        return builder.build();
    }

    private static Server createJettyServer(
        LeshanBsServerDemoCLI cli, LeshanBootstrapServer bsServer, EditableBootstrapConfigStore bsStore,
        EditableSecurityStore securityStore
    ) {
        WebAppContext root = new WebAppContext();
        root.setContextPath("/");
        root.setResourceBase(LeshanBootstrapServerDemo.class.getClassLoader().getResource("webapp").toExternalForm());
        root.setParentLoaderPriority(true);

        BootstrapServlet bsServlet = new BootstrapServlet(bsStore);
        root.addServlet(new ServletHolder(bsServlet), "/api/bootstrap/*");

        ServerServlet serverServlet = cli.identity.isRPK()
            ? new ServerServlet(bsServer, cli.identity.getPublicKey())
            : new ServerServlet(bsServer, cli.identity.getCertChain()[0]);
        root.addServlet(new ServletHolder(serverServlet), "/api/server/*");

        SecurityServlet securityServlet = cli.identity.isRPK()
            ? new SecurityServlet(securityStore, cli.identity.getPublicKey())
            : new SecurityServlet(securityStore, cli.identity.getCertChain()[0]);
        root.addServlet(new ServletHolder(securityServlet), "/api/security/*");

        ServletHolder eventServletHolder = new ServletHolder(new EventServlet(bsServer));
        root.addServlet(eventServletHolder, "/api/event/*");

        // Now prepare and start jetty
        InetSocketAddress jettyAddr = cli.main.webhost == null
            ? new InetSocketAddress(cli.main.webPort)
            : new InetSocketAddress(cli.main.webhost, cli.main.webPort);
        Server server = new Server(jettyAddr);
        server.setHandler(root);

        return server;
    }
}
