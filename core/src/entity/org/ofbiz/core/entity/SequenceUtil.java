/*
 * $Id$
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity;

import java.sql.*;
import java.util.*;

import org.ofbiz.core.util.*;

/**
 * <p>Sequence Utility to get unique sequences from named sequence banks
 * <p>Uses a collision detection approach to safely get unique sequenced ids in banks from the database
 *
 *@author     David E. Jones
 *@created    Wed Aug 29 2001
 *@version    1.0
 */
public class SequenceUtil {

    public static final String module = SequenceUtil.class.getName();

    Map sequences = new Hashtable();
    String helperName;

    public SequenceUtil(String helperName) {
        this.helperName = helperName;
    }

    public Long getNextSeqId(String seqName) {
        SequenceBank bank = (SequenceBank) sequences.get(seqName);
        if (bank == null) {
            bank = new SequenceBank(seqName, helperName);
            sequences.put(seqName, bank);
        }
        return bank.getNextSeqId();
    }

    class SequenceBank {

        public static final long bankSize = 10;
        public static final long startSeqId = 10000;
        public static final int minWaitNanos = 500000;   // 1/2 ms
        public static final int maxWaitNanos = 1000000;  // 1 ms
        public static final int maxTries = 5;

        long curSeqId;
        long maxSeqId;
        String seqName;
        String helperName;

        public SequenceBank(String seqName, String helperName) {
            this.seqName = seqName;
            this.helperName = helperName;
            curSeqId = 0;
            maxSeqId = 0;
            fillBank();
        }

        public synchronized Long getNextSeqId() {
            if (curSeqId < maxSeqId) {
                Long retSeqId = new Long(curSeqId);
                curSeqId++;
                return retSeqId;
            } else {
                fillBank();
                if (curSeqId < maxSeqId) {
                    Long retSeqId = new Long(curSeqId);
                    curSeqId++;
                    return retSeqId;
                } else {
                    Debug.logError("[SequenceUtil.SequenceBank.getNextSeqId] Fill bank failed, returning null", module);
                    return null;
                }
            }
        }

        protected synchronized void fillBank() {
            //no need to get a new bank, SeqIds available
            if (curSeqId < maxSeqId) return;

            long val1 = 0;
            long val2 = 0;

            Connection connection = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                connection = ConnectionFactory.getConnection(helperName);
            } catch (SQLException sqle) {
                Debug.logWarning("[SequenceUtil.SequenceBank.fillBank]: Unable to esablish a connection with the database... Error was:", module);
                Debug.logWarning(sqle.getMessage(), module);
            } catch (GenericEntityException e) {
                Debug.logWarning("[SequenceUtil.SequenceBank.fillBank]: Unable to esablish a connection with the database... Error was:", module);
                Debug.logWarning(e.getMessage(), module);
            }

            String sql = null;
            try {
                //NOTE: the fancy ethernet type stuff is for the case where transactions not available
                boolean manualTX = true;
                try {
                    connection.setAutoCommit(false);
                } catch (SQLException sqle) {
                    manualTX = false;
                }

                try {
                    if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
                        manualTX = false;
                    }
                } catch (GenericTransactionException e) {
                    //nevermind, don't worry about it, but print the exc anyway
                    Debug.logWarning("[SequenceUtil.SequenceBank.fillBank] Exception was thrown trying to check " +
                                     "transaction status: " + e.toString(), module);
                }

                stmt = connection.createStatement();
                int numTries = 0;
                while (val1 + bankSize != val2) {
                    Debug.logVerbose("[SequenceUtil.SequenceBank.fillBank] Trying to get a bank of sequenced ids for " +
                                     this.seqName + "; start of loop val1=" + val1 + ", val2=" + val2 + ", bankSize=" + bankSize, module);
                    sql = "SELECT SEQ_ID FROM SEQUENCE_VALUE_ITEM WHERE SEQ_NAME='" + this.seqName + "'";
                    rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        val1 = rs.getInt("SEQ_ID");
                    } else {
                        Debug.logWarning("[SequenceUtil.SequenceBank.fillBank] first select failed: trying to add " +
                                         "row, result set was empty for sequence: " + seqName, module);
                        try {
                            if (rs != null) rs.close();
                        } catch (SQLException sqle) {
                        }
                        sql = "INSERT INTO SEQUENCE_VALUE_ITEM (SEQ_NAME, SEQ_ID) VALUES ('" + this.seqName + "', " + startSeqId + ")";
                        if (stmt.executeUpdate(sql) <= 0) return;
                        continue;
                    }
                    try {
                        if (rs != null) rs.close();
                    } catch (SQLException sqle) {
                    }

                    sql = "UPDATE SEQUENCE_VALUE_ITEM SET SEQ_ID=SEQ_ID+" + this.bankSize + " WHERE SEQ_NAME='" + this.seqName + "'";
                    if (stmt.executeUpdate(sql) <= 0) {
                        Debug.logWarning("[SequenceUtil.SequenceBank.fillBank] update failed, no rows changes for seqName: " + seqName, module);
                        return;
                    }

                    if (manualTX) {
                        connection.commit();
                    }

                    sql = "SELECT SEQ_ID FROM SEQUENCE_VALUE_ITEM WHERE SEQ_NAME='" + this.seqName + "'";
                    rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        val2 = rs.getInt("SEQ_ID");
                    } else {
                        Debug.logWarning("[SequenceUtil.SequenceBank.fillBank] second select failed: aborting, result " +
                                         "set was empty for sequence: " + seqName, module);
                        try {
                            if (rs != null) rs.close();
                        } catch (SQLException sqle) {
                        }
                        return;
                    }
                    try {
                        if (rs != null) rs.close();
                    } catch (SQLException sqle) {
                    }

                    if (val1 + bankSize != val2) {
                        if (numTries >= maxTries) {
                            Debug.logError("[SequenceUtil.SequenceBank.fillBank] maxTries (" + maxTries + ") reached, giving up.", module);
                            return;
                        }
                        //collision happened, wait a bounded random amount of time then continue
                        int waitTime = (new Double(Math.random() * (maxWaitNanos - minWaitNanos))).intValue() + minWaitNanos;
                        try {
                            this.wait(0, waitTime);
                        } catch (Exception e) {
                        }
                    }

                    numTries++;
                }

                curSeqId = val1;
                maxSeqId = val2;
                Debug.logVerbose("[SequenceUtil.SequenceBank.fillBank] Successfully got a bank of sequenced ids for " +
                                 this.seqName + "; curSeqId=" + curSeqId + ", maxSeqId=" + maxSeqId + ", bankSize=" + bankSize, module);
            } catch (SQLException sqle) {
                Debug.logWarning("[SequenceUtil.SequenceBank.fillBank] SQL Exception while executing the following:\n" +
                                 sql + "\nError was:", module);
                Debug.logWarning(sqle.getMessage(), module);
                return;
            } finally {
                try {
                    if (stmt != null) stmt.close();
                } catch (SQLException sqle) {
                }
                try {
                    if (connection != null) connection.close();
                } catch (SQLException sqle) {
                }
            }
        }
    }
}
