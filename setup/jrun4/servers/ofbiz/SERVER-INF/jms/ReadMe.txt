-------------------------------------------------------------
                  Documentation Addendum:

        A note regarding database table setup for the 
             JMS, RDBM-backed peristent store.
-------------------------------------------------------------

  Some versions of the JRun4 documentation refer users to this
directory in seach of SQL scripts to be used to setup the
database tables used by the rdbm-backed, JMS persistent datastore.

  These scripts are no longer provided because they are not
needed. The JMS subsystem will automatically create the necessary 
database tables if they do not exist in the database.

  To use this automatic table creation feature, go to the server
configuration file jrun-jms.xml and set the <auto-table-creation>
element to "true". 

  When <auto-table-creation> is set to true, the necessary tables
will be created in the database described by the <data-source> 
element of the active <persistent-adapter> in that same config-
uration file. Table creation will only happen if any of the 
required tables do not exist. If the tables exist, they are not
deleted and recreated.

                   ----------------------



