package com.dcep.main;

import com.dcep.inet.inet;


import com.codahale.metrics.MetricRegistry;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.GossipMember;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.GossipService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class dcep {

    private static final Logger consolelogger = LogManager.getLogger("console_log");

    public static void main(String[] args) throws UnknownHostException, InterruptedException {

       // consolelogger.trace("Trace Message!");
       // consolelogger.debug("Debug Message!");
       // consolelogger.info("Info Message!");
       // consolelogger.warn("Warn Message!");
       // consolelogger.error("Error Message Logged !!!", new NullPointerException("NullError"));
       // consolelogger.fatal("Fatal Message!");

        inet net = null;
        String myIp = null;
        try {
            net = new inet();
        } catch (SocketException e) {
           consolelogger.error("Error cant read network interfaces: "+ e.toString());
        }

        try {
            assert net != null;
            myIp = net.getInterfaceIP("ens33"); // Get my ip address
        } catch (SocketException e) {
            consolelogger.error("Error network interface not found: "+ e.toString());
        }
        consolelogger.trace("My IP address is : " + myIp);

        // Gossip Member
        String cluster = "Gossip Cluster";
        String GossipmemberIP =  "udp://"+ myIp + ":10000";
        String GossipmemberID =  "1";

        // Settings Gossip Cluster
        GossipSettings settings = new GossipSettings();
        settings.setWindowSize(10);        // Gossip window interval
        settings.setConvictThreshold(0.6); // threashold to set from live to death nodes.
        settings.setGossipInterval(10);    // Gossip Interval

        // Initial Members on Cluster
        List<GossipMember> startupMembers = new ArrayList<>();
        startupMembers.add(new RemoteGossipMember(cluster, URI.create(GossipmemberIP),GossipmemberID)); // add myself as first node

        // Star Gossip service
        GossipService gossipService = new GossipService(cluster, URI.create(GossipmemberIP), GossipmemberID, new HashMap(), startupMembers, settings, (a, b) -> {
        }, new MetricRegistry());
        gossipService.start();

        while(true) {
             consolelogger.trace("Live: " + gossipService.getGossipManager().getLiveMembers());
             consolelogger.trace("Dead: " + gossipService.getGossipManager().getDeadMembers());
            Thread.sleep(1000L);
        }


    }


}
