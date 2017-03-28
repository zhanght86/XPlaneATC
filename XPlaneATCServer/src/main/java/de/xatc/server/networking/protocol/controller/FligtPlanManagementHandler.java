/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.xatc.server.networking.protocol.controller;

import de.xatc.commons.networkpackets.client.SubmittedFlightPlan;
import de.xatc.server.db.DBSessionManager;
import de.xatc.server.db.entities.XATCUserSession;
import de.xatc.server.sessionmanagment.SessionManagement;
import io.netty.channel.Channel;
import org.hibernate.Session;

/**
 *
 * @author Mirko
 */
public class FligtPlanManagementHandler {
    
    public static void handleNewIncomingSubmittedFlightPlan(SubmittedFlightPlan plan, Channel c) {
        
        
        XATCUserSession user = SessionManagement.findUserSessionBySessionID(plan.getSessionID(), SessionManagement.getUserSessionList());
        if (user == null) {
            System.out.println("Could not find UserSession");
            return;
            
        }
        plan.setFlightPlanOwner(user.getRegisteredUser());
        Session session = DBSessionManager.getSession();
        session.saveOrUpdate(plan);
        DBSessionManager.closeSession(session);
        hier müssen wir dann noch weiter machen. auslesen der Flugplane fuer den ATCController mystrips or !myatrips mit ATCREquestSTripsPacket
        
    }
    
    
    
}