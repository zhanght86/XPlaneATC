/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.xatc.controllerclient.network.handlers;

import de.mytools.tools.swing.SwingTools;
import de.xatc.commons.networkpackets.pilot.LoginPacket;
import de.xatc.controllerclient.config.XHSConfig;
import java.awt.Color;
import org.apache.log4j.Logger;

/**
 *
 * @author Mirko Bubel (mirko_bubel@hotmail.com)
 */
public class LoginAnswerHandler {

    private static final Logger LOG = Logger.getLogger(LoginAnswerHandler.class.getName());
    
    public static void handleLoginAnswer(Object packet) {

        LoginPacket l = (LoginPacket) packet;
        if (!l.isSuccessful()) {

            SwingTools.alertWindow("Login in was not successful. Server said: " + l.getServerMessage(), XHSConfig.getMainFrame());
            XHSConfig.getDataClientBootstrap().shutdownClient();
            return;

        }

        XHSConfig.getMainFrame().getMainPanel().getStatusPanel().getConnectedToATCDataServer().setColor(Color.GREEN);
        XHSConfig.getMainFrame().getMainPanel().getStatusPanel().revalidate();
        XHSConfig.getMainFrame().getMainPanel().getStatusPanel().repaint();
        XHSConfig.getMainFrame().getConnectItem().setEnabled(false);
        XHSConfig.getMainFrame().getDisconnectItem().setEnabled(true);

        XHSConfig.setCurrentSessionID(l.getSessionID());
        XHSConfig.setCurrentChannelID(l.getChannelID());
        LOG.info("Login successful");

        SubmittedFlightPlanActionHandler.deleteLocalFlightPlans();
        
        LOG.info("Sending Data Structures Sync Request to Server.....");
        DataSyncHandler.sendSyncStructuresRequestPacket();

    }

}
