/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.xatc.server.networking.protocol.controller;

import de.xatc.commons.datastructure.pilot.PilotStructure;
import de.xatc.commons.networkpackets.pilot.SubmittedFlightPlan;
import de.xatc.commons.networkpackets.pilot.SubmittedFlightPlansActionPacket;
import de.xatc.commons.networkpackets.pilot.TextMessagePacket;

import de.xatc.server.db.DBSessionManager;
import de.xatc.server.sessionmanagment.NetworkBroadcaster;
import de.xatc.server.sessionmanagment.SessionManagement;
import io.netty.channel.Channel;
import java.util.Map.Entry;
import org.hibernate.Session;

/**
 *
 * @author Mirko
 */
public class SubmittedFlightPlanActionHandlerATC {

    public static void acceptSubmittedFlightPlan(SubmittedFlightPlansActionPacket action) {
        
        PilotStructure pilotStructure = SessionManagement.getPilotDataStructures().get(action.getSubmittedFlightPlan().getPilotsSessionID());
        if (pilotStructure == null) {
            System.out.println("Could not revoke flightplan by atc. PilotStructure not found");
            return;
        }
    
        
        SubmittedFlightPlan plan = action.getSubmittedFlightPlan();
        plan.setAccepted(true);
        plan.setActive(true);
        plan.setRevoked(false);
        plan.setAssingedControllerSessionID(action.getSessionID());
        Session s = DBSessionManager.getSession();
        s.saveOrUpdate(plan);
        DBSessionManager.closeSession(s);
        pilotStructure.setSubmittedFlightPlan(plan);
        NetworkBroadcaster.broadcastATC(action);
        
        Channel n = SessionManagement.getPilotChannels().get(action.getSubmittedFlightPlan().getPilotsSessionID());
        if (n != null) {
            
            TextMessagePacket msg = new TextMessagePacket();
            msg.setToUsername(pilotStructure.getUserName());
            msg.setMessage("Your submitted FlightPlan has been accepted by Controller.");
            n.writeAndFlush(n);
            
            
        }
        
    }
    
    public static void revokeFlightPlan(SubmittedFlightPlansActionPacket action) {
        
        PilotStructure pilotStructure = SessionManagement.getPilotDataStructures().get(action.getSubmittedFlightPlan().getPilotsSessionID());
        if (pilotStructure == null) {
            System.out.println("Could not revoke flightplan by atc. PilotStructure not found");
            return;
        }
        
        SubmittedFlightPlan plan = action.getSubmittedFlightPlan();
        plan.setRevoked(true);
        plan.setAccepted(false);
        plan.setActive(false);
        Session s = DBSessionManager.getSession();
        s.saveOrUpdate(plan);
        DBSessionManager.closeSession(s);
        
        pilotStructure.setSubmittedFlightPlan(null);
        NetworkBroadcaster.broadcastATC(action);
        Channel n = SessionManagement.getPilotChannels().get(action.getSubmittedFlightPlan().getPilotsSessionID());
        if (n != null) {
            
            TextMessagePacket msg = new TextMessagePacket();
            msg.setToUsername(pilotStructure.getUserName());
            msg.setMessage("Your submitted FlightPlan has been revoked by Controller. Please send a new FlightPlan");
            n.writeAndFlush(n);
            
            
        }
        
        
    }

    public static void sendSubmittedFlightPlansToATC(SubmittedFlightPlansActionPacket action) {

        Channel channel = SessionManagement.getAtcChannels().get(action.getSessionID());
        if (channel == null) {
            
            System.out.println("Could not send all FlightPlans to Contrller. SessionID not found");
            return;
        }
        
        for (Entry<String,PilotStructure> entry : SessionManagement.getPilotDataStructures().entrySet()) {
        
            if (entry.getValue().getSubmittedFlightPlan() != null) {
                
                System.out.println("syncing flightplan to controller: " + entry.getValue().getUserName());
                SubmittedFlightPlansActionPacket p = new SubmittedFlightPlansActionPacket();
                p.setAction("sync");
                p.setSubmittedFlightPlan(entry.getValue().getSubmittedFlightPlan());
                channel.writeAndFlush(p);
            }
            
        }
        

    }

}
