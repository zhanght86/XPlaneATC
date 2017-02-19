/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.xatc.xplaneadapter.config;

import de.mytools.encoding.ObjectsMarshaller;
import de.mytools.tools.swing.SwingTools;
import de.xatc.xplaneadapter.gui.ConfigFrame;
import de.xatc.xplaneadapter.gui.ConnectFrame;
import de.xatc.xplaneadapter.gui.FlightPlanFrame;
import de.xatc.xplaneadapter.gui.MainFrame;
import de.xatc.xplaneadapter.gui.RegisterFrame;
import de.xatc.xplaneadapter.networking.DataClient;
import de.xatc.xplaneadapter.networking.DataClientBootstrap;
import de.xatc.xplaneadapter.networking.XPlaneUDPListener;
import de.xatc.xplaneadapter.tools.DebugMessageLevel;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Mirko Bubel (mirko_bubel@hotmail.com)
 */
public class AdapterConfig {

    private final static String appName = "XPlane ATC Server Adapter";
    private final static String version = "0.1-alpha";

    private final static String propertiesFileName = "ATCServerAdapterConfig.properties";
    private static ConfigBean configBean;
    private static final boolean doDebug = true;
    private static DebugMessageLevel debugLevel = DebugMessageLevel.INFO;
    private static ConnectFrame connectFrame;
    private static ConfigFrame configFrame;
    private static FlightPlanFrame flightPlanFrame;
    private static boolean connectedToATCServer = false;
    private static boolean connectedToLocalXPlane = false;
    private static boolean listeningToXPlane = false;
    private static XPlaneUDPListener xplaneUDPListener;
    private static String currentConnectionName;
    private static RegisterFrame registerFrame;
    private static String currentRadioFrequency;
    private static final String recordingDirectory = "recordings";
    
    
    private static DataClientBootstrap clientBootstrap;
    private static DataClient dataClient;
    
    private static String currentSessionID;
    private static String currentChannelID;

    private static MainFrame mainFrame;

    public static String getAppName() {
        return appName;
    }

    public static String getVersion() {
        return version;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static void setMainFrame(MainFrame mainFrame) {
        AdapterConfig.mainFrame = mainFrame;
    }

    public static String getPropertiesFileName() {
        return propertiesFileName;
    }

    public static ConfigBean getConfigBean() {
        if (configBean == null) {
            loadPropsFile();
        }
        return configBean;
    }

    public static void setConfigBean(ConfigBean configBean) {
        AdapterConfig.configBean = configBean;
    }

    public static DebugMessageLevel getDebugLevel() {
        return debugLevel;
    }

    public static void setDebugLevel(DebugMessageLevel debugLevel) {
        AdapterConfig.debugLevel = debugLevel;
    }

    /**
     * save a marshelled config bean
     */
    public static void savePropsFile() {
        AdapterConfig.debugMessage("Saving Props File", DebugMessageLevel.DEBUG);

        AdapterConfig.debugMessage("SAVING PROPERTIES", DebugMessageLevel.DEBUG);

        String configBeanContent = ObjectsMarshaller.objectToXml(configBean);
        AdapterConfig.debugMessage(configBeanContent, DebugMessageLevel.DEBUG);
        try {
            FileUtils.writeStringToFile(new File(AdapterConfig.getPropertiesFileName()), configBeanContent);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            SwingTools.alertWindow("Could not save Properties File!", mainFrame);
        }

    }
    
    /**
     * load the properties File
     */
    public static void loadPropsFile() {
        
        AdapterConfig.debugMessage("loading ConfigBean", DebugMessageLevel.DEBUG);
        
        if (!doesConfigFileExist()) {
            setConfigBean(null);
            return;
        }
        
        try {
            String configString = FileUtils.readFileToString(new File(AdapterConfig.getPropertiesFileName()));
            ConfigBean bean = (ConfigBean) ObjectsMarshaller.xmlToObject(configString);
            setConfigBean(bean);
        } catch (IOException ex) {
            setConfigBean(null);
        }
        
    }

    
    /**
     * check if ConfigFileExists
     */
    public static boolean doesConfigFileExist() {
        
        File configFile = new File(AdapterConfig.getPropertiesFileName());
        return configFile.exists();
        
    }
    
    
    /**
     *
     * @param s
     */
    public static void debugMessage(String s, DebugMessageLevel level) {

        if (!doDebug) {
            return;
        }

        if (level.getLevel() <= AdapterConfig.getDebugLevel().getLevel()) {

            System.out.println(s);

        }

    }

    public static ConnectFrame getConnectFrame() {
        return connectFrame;
    }

    public static void setConnectFrame(ConnectFrame connectFrame) {
        AdapterConfig.connectFrame = connectFrame;
    }

    public static ConfigFrame getConfigFrame() {
        return configFrame;
    }

    public static void setConfigFrame(ConfigFrame configFrame) {
        AdapterConfig.configFrame = configFrame;
    }

    public static boolean isConnectedToATCServer() {
        return connectedToATCServer;
    }

    public static void setConnectedToATCServer(boolean connectedToATCServer) {
        AdapterConfig.connectedToATCServer = connectedToATCServer;
    }

    public static boolean isConnectedToLocalXPlane() {
        return connectedToLocalXPlane;
    }

    public static void setConnectedToLocalXPlane(boolean connectedToLocalXPlane) {
        AdapterConfig.connectedToLocalXPlane = connectedToLocalXPlane;
    }

    public static boolean isListeningToXPlane() {
        return listeningToXPlane;
    }

    public static void setListeningToXPlane(boolean listeningToXPlane) {
        AdapterConfig.listeningToXPlane = listeningToXPlane;
    }

    public static XPlaneUDPListener getXplaneUDPListener() {
        return xplaneUDPListener;
    }

    public static void setXplaneUDPListener(XPlaneUDPListener xplaneUDPListener) {
        AdapterConfig.xplaneUDPListener = xplaneUDPListener;
    }

    public static FlightPlanFrame getFlightPlaneFrame() {
        return flightPlanFrame;
    }

    public static void setFlightPlaneFrame(FlightPlanFrame flightPlaneFrame) {
        AdapterConfig.flightPlanFrame = flightPlaneFrame;
    }

    public static String getCurrentConnectionName() {
        return currentConnectionName;
    }

    public static void setCurrentConnectionName(String currentConnectionName) {
        AdapterConfig.currentConnectionName = currentConnectionName;
    }

    public static DataClient getDataClient() {
        return dataClient;
    }

    public static void setDataClient(DataClient dataClient) {
        AdapterConfig.dataClient = dataClient;
    }

    public static DataClientBootstrap getClientBootstrap() {
        return clientBootstrap;
    }

    public static void setClientBootstrap(DataClientBootstrap clientBootstrap) {
        AdapterConfig.clientBootstrap = clientBootstrap;
    }

    public static RegisterFrame getRegisterFrame() {
        return registerFrame;
    }

    public static void setRegisterFrame(RegisterFrame registerFrame) {
        AdapterConfig.registerFrame = registerFrame;
    }

    public static String getCurrentSessionID() {
        return currentSessionID;
    }

    public static void setCurrentSessionID(String currentSessionID) {
        AdapterConfig.currentSessionID = currentSessionID;
    }

    public static String getCurrentChannelID() {
        return currentChannelID;
    }

    public static void setCurrentChannelID(String currentChannelID) {
        AdapterConfig.currentChannelID = currentChannelID;
    }

    public static String getCurrentRadioFrequency() {
        return currentRadioFrequency;
    }

    public static void setCurrentRadioFrequency(String currentAudioFrequency) {
        AdapterConfig.currentRadioFrequency = currentAudioFrequency;
    }

    public static boolean isDoDebug() {
        return doDebug;
    }

    public static FlightPlanFrame getFlightPlanFrame() {
        return flightPlanFrame;
    }

    public static void setFlightPlanFrame(FlightPlanFrame flightPlanFrame) {
        AdapterConfig.flightPlanFrame = flightPlanFrame;
    }

    public static String getRecordingDirectory() {
        return recordingDirectory;
    }


    
    

    
}
