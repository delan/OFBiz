package org.ofbiz.designer.newdesigner.model;

import java.util.*;
import org.ofbiz.designer.util.*;

public class ArcLoader {
    private static Hashtable pendingArcs = new Hashtable();
    private static Hashtable arcSources = new Hashtable();
    private static Hashtable arcDestinations = new Hashtable();

    public static void addPendingArc(String sourceName, String destinationName, IArcModel arc) {
        if(pendingArcs.containsKey(sourceName + "<>" + destinationName))
            throw new RuntimeException("pendingArcs already contains key " + sourceName + "<>" + destinationName);
        if(pendingArcs.containsValue(arc))
            throw new RuntimeException("pendingArcs already contains arc");

        pendingArcs.put(sourceName + "<>" + destinationName, arc);
        arcSources.put(arc, sourceName);
        arcDestinations.put(arc, destinationName);
    }

    public static void removePendingArc(String sourceName, String destinationName) {
        if(!pendingArcs.containsKey(sourceName + "<>" + destinationName))
            throw new RuntimeException("pendingArcs does not contain key " + sourceName + "<>" + destinationName);

        IArcModel arc = getPendingArc(sourceName, destinationName);
        pendingArcs.remove(sourceName + "<>" + destinationName);
        arcSources.remove(arc);
        arcDestinations.remove(arc);
    }

    public static IArcModel getPendingArc(String sourceName, String destinationName) {
        if(!existsPendingArc(sourceName, destinationName))
            return null;
        else
            return(IArcModel)pendingArcs.get(sourceName + "<>" + destinationName);
    }

    public static boolean existsPendingArc(String sourceName, String destinationName) {
        return pendingArcs.containsKey(sourceName + "<>" + destinationName);
    }

    public static boolean existsPendingArc(IArcModel arc) {
        return pendingArcs.containsValue(arc);
    }

    public static String getPendingArcSource(IArcModel arc) {
        return(String)arcSources.get(arc);
    }

    public static String getPendingArcDestination(IArcModel arc) {
        return(String)arcDestinations.get(arc);
    }

    public static void printPendingArcs() {
        System.err.println("*** ARCLOADER CONTENTS BEGIN ***");
        Enumeration keys = pendingArcs.keys();
        while(keys.hasMoreElements())
            System.err.println(">" + keys.nextElement());
        System.err.println("*** ARCLOADER CONTENTS END ***");
    }
}
