package de.xatc.controllerclient.network.handlers;

import de.mytools.tools.swing.SwingTools;
import de.xatc.commons.datastructure.pilot.PilotStructure;
import de.xatc.commons.networkpackets.pilot.SubmittedFlightPlan;
import de.xatc.commons.networkpackets.pilot.SubmittedFlightPlansActionPacket;
import de.xatc.controllerclient.config.XHSConfig;
import de.xatc.controllerclient.datastructures.DataStructureSilo;
import de.xatc.controllerclient.datastructures.LocalPilotDataStructure;
import de.xatc.controllerclient.db.DBSessionManager;
import de.xatc.controllerclient.gui.FlightPlanStrips.FligtPlanStripsPanel;
import java.awt.Component;
import javax.swing.JPanel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Mirko
 */
public class SubmittedFlightPlanActionHandler {

    public static void handleSubmittedFlightPlanActionPacket(SubmittedFlightPlansActionPacket action) {

        if (action.getAction().equals("revoke")) {

            revokeSubmittedFlightPlan(action);

        } else if (action.getAction().equals("new")) {
            System.out.println("Handling new FlightPlan");
            handleNewSubmittedFlightPlan(action.getSubmittedFlightPlan());
        }

    }

    public static void handleNewSubmittedFlightPlan(SubmittedFlightPlan flightPlan) {

        if (flightPlan == null) {
            
            System.out.println("could not handle new flightplan. Flightplan is null");
            return;
        }
        System.out.println("Servers DatabaseID : " + flightPlan.getId() + " - setting to 0");
        flightPlan.setId(0);
        LocalPilotDataStructure s = DataStructureSilo.getLocalPilotStructure().get(flightPlan.getPilotsSessionID());
        
        if (s == null) {
            System.out.println("No session found for submitted flight plan. returning");
            SwingTools.alertWindow("A Flightplan was Subbmitted of a user which could not be found in SessionManagement!", XHSConfig.getMainFrame());
            return;
        }
        System.out.println("Saving new flightPlan and setting FlightPlan to database");
        System.out.println("From/To " + flightPlan.getIcaoFrom() + "/" + flightPlan.getIcaoTo());
        s.getPilotServerStructure().setSubmittedFlightPlan(flightPlan);
        Session session = DBSessionManager.getSession();
        session.saveOrUpdate(flightPlan);
        DBSessionManager.closeSession(session);

    }

    public static void revokeSubmittedFlightPlan(SubmittedFlightPlansActionPacket action) {

        LocalPilotDataStructure pilotStructure = DataStructureSilo.getLocalPilotStructure().get(action.getSubmittedFlightPlan().getPilotsSessionID());
        if (pilotStructure == null) {

            System.out.println("could not revoke flightPlan. PilotStrucutre not found");
            return;

        }
        SubmittedFlightPlan flightPlan = action.getSubmittedFlightPlan();
        flightPlan.setActive(false);
        flightPlan.setRevoked(true);
        pilotStructure.getPilotServerStructure().setSubmittedFlightPlan(null);

        Session session = DBSessionManager.getSession();
        session.saveOrUpdate(action.getSubmittedFlightPlan());
        DBSessionManager.closeSession(session);

    }

    public static void refreshFlightPlanFrames() {

        if (XHSConfig.getSubmittedFlightPlansPoolFrame() != null) {

            JPanel centerPanel = XHSConfig.getSubmittedFlightPlansPoolFrame().getCenterPanel();
            for (Component c : centerPanel.getComponents()) {

                if (c instanceof FligtPlanStripsPanel) {
                    FligtPlanStripsPanel panel = (FligtPlanStripsPanel) c;
//                    if (panel.getServersID() == p.getServersID()) {
//                        centerPanel.remove(c);
//                    }
                }

            }

        }
        XHSConfig.getSubmittedFlightPlansPoolFrame().revalidate();
        XHSConfig.getSubmittedFlightPlansPoolFrame().repaint();

    }

    public static void deleteLocalFlightPlans() {

        Session s = DBSessionManager.getSession();

        Transaction tx = s.beginTransaction();
        Query q = s.createQuery("delete from SubmittedFlightPlan");
        q.executeUpdate();
        tx.commit();

        DBSessionManager.closeSession(s);

    }

    public static void sendFlightPlansSyncRequest() {

        if (XHSConfig.getDataClient() == null) {
            System.out.println("could not send sync Request for SubmittedFlightPlans. Not Connected!");
            return;
        }
        SubmittedFlightPlansActionPacket p = new SubmittedFlightPlansActionPacket();
        p.setAction("syncAll");
        XHSConfig.getDataClient().writeMessage(p);

    }

}
