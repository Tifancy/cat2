package com.dianping.cat.message.io;

import java.net.InetSocketAddress;
import java.util.List;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.unidal.cat.config.ClientConfigurationManager;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named(type = TransportManager.class)
public class DefaultTransportManager implements TransportManager, Initializable, LogEnabled {
   @Inject
   private ClientConfigurationManager m_configManager;

   @Inject
   private TcpSocketSender m_tcpSocketSender;

   private Logger m_logger;

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   @Override
   public MessageSender getSender() {
      return m_tcpSocketSender;
   }

   @Override
   public void initialize() {
      if (!m_configManager.getConfig().isEnabled()) {
         m_tcpSocketSender = null;
         m_logger.warn("CAT was DISABLED or NOTE initialized correctly!");
      } else {
         List<InetSocketAddress> addresses = m_configManager.getConfig().getServersForTree();

         m_logger.info("Remote CAT servers: " + addresses);

         if (addresses.isEmpty()) {
            m_logger.error("No active servers found in configuration!");
         } else {
            m_tcpSocketSender.initialize(addresses);
         }
      }
   }
}
