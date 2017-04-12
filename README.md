# ambari-process-flow-poc
Ambari Process Flow Engine POC

This POC test out the concept of using a home-grown simple workflow engine to orchestrate wizards and work flows in Ambari. 

Two work flows are provided:
* Enable Kerberos
* Enable NameNode HA

The Ambari (groovy) client is used to communicate with and Ambari instance via Ambari's REST API.

An in-memory database is created to store the process flow meta- and instance data.

The user interface is rather simplistic but attempts to mimic a wizard. A "continue" prompt has been artificially added to give the impression of a "next" button between wizard pages. 

#### Enable Kerberos
The *Enable Kerberos* workflow tests out the ability to skip processes and tasks based on data collected in the flow.  
Some of the tasks actually communicate with an Ambari instance but only to start and stop services.  No actual Kerberos tasks are invoked.

#### Enable NameNode HA
The *Enable NameNode HA* workflow tests out the ability to fully control Ambari and actually enable NameNode HA - however manual steps are needed. 


