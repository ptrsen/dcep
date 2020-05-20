package com.dcep.main;

import com.dcep.inet.inet;


import com.codahale.metrics.MetricRegistry;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.GossipMember;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.GossipService;

import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.crdt.OrSet.Builder;
import org.apache.gossip.model.SharedGossipDataMessage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import java.net.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


public class dcep {

    private static final Logger consolelogger = LogManager.getLogger("console_log");

    public static void main(String[] args) throws IOException, InterruptedException {

       // consolelogger.trace("Trace Message!");
       // consolelogger.debug("Debug Message!");
       // consolelogger.info("Info Message!");
       // consolelogger.warn("Warn Message!");
       // consolelogger.error("Error Message Logged !!!", new NullPointerException("NullError"));
       // consolelogger.fatal("Fatal Message!");


        String gossipMemberIP = null;
       // String defaultInterface = "ens33";
        String defaultInterface = "eth0";


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
      //  String seedGossipMemberIP = "172.17.0.3";
       // String seedGossipMemberIP = "172.16.143.172";
        String seedGossipMemberID = "753e4e4c-3e26-4e26-accf-3142752ed0a9";
        String seedGossipMember =  gossipProtocol+"://"+ seedGossipMemberIP + ":" + gossipPort;

        String gossipMemberID = null;
        if (gossipMemberIP.contentEquals(seedGossipMemberIP) ){
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


      /*  while(true) {
             consolelogger.trace("Live Nodes: " + gossipService.getGossipManager().getLiveMembers());
             consolelogger.trace("Dead Nodes: " + gossipService.getGossipManager().getDeadMembers());
            Thread.sleep(1000L);
        }  */
         // thread to print

        (new Thread(() -> {
            while(true) {
                System.out.println("Live: " + gossipService.getGossipManager().getLiveMembers());
                System.out.println("Dead: " + gossipService.getGossipManager().getDeadMembers());
                System.out.println("---------- " + (gossipService.getGossipManager().findCrdt("abc") == null ? "" : gossipService.getGossipManager().findCrdt("abc").value()));
                System.out.println("********** " + gossipService.getGossipManager().findCrdt("abc"));

                try {
                    Thread.sleep(2500L);
                } catch (Exception var2) {
                }
            }
        })).start();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Throwable var5 = null;

        try {
            while((line = br.readLine()) != null) {
                System.out.println(line);
                char op = line.charAt(0);
                String val = line.substring(2);

                if (op == 'a') {
                    addData(val, gossipService);
                }
                if (op == 'r') {
                    removeData(val, gossipService);
                }

            }
        } catch (Throwable var15) {
            var5 = var15;
            throw var15;
        } finally {
            if (br != null) {
                if (var5 != null) {
                    try {
                        br.close();
                    } catch (Throwable var14) {
                        var5.addSuppressed(var14);
                    }
                } else {
                    br.close();
                }
            }

        }




    }


    private static void removeData(String val, GossipService gossipService) {
        OrSet<String> s = (OrSet)gossipService.getGossipManager().findCrdt("abc");
        SharedGossipDataMessage m = new SharedGossipDataMessage();
        m.setExpireAt(9223372036854775807L);
        m.setKey("abc");
        m.setPayload(new OrSet(s, (new OrSet.Builder()).remove(val)));
        m.setTimestamp(System.currentTimeMillis());
        gossipService.getGossipManager().merge(m);
    }

    private static void addData(String val, GossipService gossipService) {
        SharedGossipDataMessage m = new SharedGossipDataMessage();
        m.setExpireAt(9223372036854775807L);
        m.setKey("abc");
        m.setPayload(new OrSet(new String[]{val}));
        m.setTimestamp(System.currentTimeMillis());
        gossipService.getGossipManager().merge(m);
    }


}
