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
import java.util.UUID;
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


        String gossipMemberIP = null;
        String defaultInterface = "ens33";
       // String defaultInterface = "eth0";


        inet net = null;
        try {
            net = new inet();
        } catch (SocketException e) {
           consolelogger.error("Error cant read network interfaces: "+ e.toString());
        }

        try {
            assert net != null;
            gossipMemberIP = net.getInterfaceIP(defaultInterface); // Get my ip address
        } catch (SocketException e) {
            consolelogger.error("Error network interface not found: "+ e.toString());
        }
        consolelogger.trace("Node IP : " + gossipMemberIP);

        // Gossip service
        String gossipClusterName = "Gossip Cluster";
        String gossipProtocol = "udp";
        String gossipPort = "10000";
        String gossipMember =  gossipProtocol+"://"+ gossipMemberIP + ":" + gossipPort;

        String seedGossipMemberIP = "10.12.0.1";
        String seedGossipMemberID = "753e4e4c-3e26-4e26-accf-3142752ed0a9";
        String seedGossipMember =  gossipProtocol+"://"+ seedGossipMemberIP + ":" + gossipPort;

        String gossipMemberID = null;
        if (gossipMemberIP == seedGossipMemberIP ){
            gossipMemberID = seedGossipMemberID;
        }else {
            gossipMemberID = UUID.randomUUID().toString(); // Random UUID
        }


        consolelogger.trace("Node UUID : " + gossipMemberID);



        // Settings Gossip Cluster
        GossipSettings settings = new GossipSettings();
        settings.setWindowSize(10);        // Gossip window interval
        settings.setConvictThreshold(0.6); // threashold to set from live to death nodes.
        settings.setGossipInterval(10);    // Gossip Interval

        // Initial Members on Cluster
        List<GossipMember> startupMembers = new ArrayList<>();
        startupMembers.add(new RemoteGossipMember(gossipClusterName, URI.create(seedGossipMember),seedGossipMemberID)); // add first node - Usually root

        // Star Gossip service
        GossipService gossipService = new GossipService(gossipClusterName, URI.create(gossipMember), gossipMemberID, new HashMap(), startupMembers, settings, (a, b) -> {
        }, new MetricRegistry());
        gossipService.start();


        while(true) {
             consolelogger.trace("Live: " + gossipService.getGossipManager().getLiveMembers());
             consolelogger.trace("Dead: " + gossipService.getGossipManager().getDeadMembers());
            Thread.sleep(1000L);
        }




    }


}
