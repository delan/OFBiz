/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.ordermgr;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.order.order.*;

import dori.jasper.engine.*;
import dori.jasper.engine.util.*;
import dori.jasper.engine.export.*;

/**
 * Order Report Events
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">Britton LaRoche</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class OrderReportEvents {
    
    public static final String module = OrderReportEvents.class.getName();
    
    public static String renderOrderReports(HttpServletRequest request, HttpServletResponse response) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        
        StringBuffer sbSql = new StringBuffer("");
        String groupDisp = "";
        String pName = "";
        String dbName = "";
        String lsQuery = "";
        String queryVal = "";
        String fromDate = "";
        String toDate = "";
        
        String groupName = request.getParameter("groupName");
        fromDate = request.getParameter("fromDate");
        toDate = request.getParameter("toDate");
        String reportName = "orderreport.jasper";
        
        if (groupName.equals("product")) {
            groupName = "order_item.product_id";
            reportName = "orderitemreport.jasper";
        }
        if (groupName.equals("orderStatus")) {
            groupName = "status_item.description";
            reportName = "orderreport.jasper";
        }
        if (groupName.equals("itemStatus")) {
            groupName = "item_status.description";
            reportName = "orderitemreport.jasper";
        }
        if (groupName.equals("adjustment")) {
            groupName = "order_adjustment_type.description";
            reportName = "orderitemreport.jasper";
        }
        if (groupName.equals("ship")) {
            groupName = "concat(concat(order_shipment_preference.carrier_party_id, ' - '), shipment_method_type.description)";
            reportName = "orderreport.jasper";
        }
        if (groupName.equals("payment")) {
            groupName = "payment_method_type.description";
            reportName = "orderreport.jasper";
        }
        if (groupName.length() < 4) {
            groupName = "status_item.description";
            reportName = "orderreport.jasper";
        }
        
        sbSql.append(" select distinct ");
        sbSql.append( groupName +" as GroupName, ");
        sbSql.append(" order_header.order_id as orderId, ");
        sbSql.append(" order_header.order_date as orderDate, ");
        sbSql.append(" status_item.description as orderStatus, ");
        sbSql.append(" payment_method_type.description as paymentMethod,");
        sbSql.append(" concat(concat(order_shipment_preference.carrier_party_id, ' - '), shipment_method_type.description) as shipMethod, ");
        sbSql.append(" order_item.product_id as productId,");
        sbSql.append(" order_item.item_description as itemDescription, ");
        sbSql.append(" item_status.description as itemStatus, ");
        sbSql.append(" order_item.quantity as quantity, ");
        sbSql.append(" order_item.unit_price as unitPrice,");
        sbSql.append(" order_item.unit_price * order_item.quantity as purchaseAmount, ");
        sbSql.append(" order_adjustment_type.description as adjustment, ");
        sbSql.append(" order_adjustment.amount as adjustmentAmount ");
        
        sbSql.append(" from order_header, ");
        sbSql.append(" order_payment_preference, ");
        sbSql.append(" payment_method_type, ");
        sbSql.append(" order_shipment_preference, ");
        sbSql.append(" shipment_method_type, ");
        sbSql.append(" status_item, ");
        sbSql.append(" status_item item_status, ");
        sbSql.append(" order_item, ");
        sbSql.append(" order_adjustment, ");
        sbSql.append(" order_adjustment_type ");
        
        sbSql.append(" where order_item.order_id = order_header.order_id and ");
        sbSql.append(" order_payment_preference.order_id = order_header.order_id and ");
        sbSql.append(" payment_method_type.payment_method_type_id = order_payment_preference.payment_method_type_id and  ");
        sbSql.append(" order_shipment_preference.order_id = order_header.order_id and ");
        sbSql.append(" shipment_method_type.shipment_method_type_id = order_shipment_preference.shipment_method_type_id and ");
        sbSql.append(" status_item.status_type_id = 'ORDER_STATUS' and ");
        sbSql.append(" status_item.status_id = order_header.status_id and ");
        sbSql.append(" item_status.status_type_id = 'ORDER_ITEM_STATUS' and ");
        sbSql.append(" item_status.status_id = order_item.status_id and ");
        sbSql.append(" order_adjustment.order_id = order_header.order_id and ");
        sbSql.append(" order_adjustment.order_item_seq_id = order_item.order_item_seq_id and ");
        sbSql.append(" order_adjustment_type.order_adjustment_type_id = order_adjustment.order_adjustment_type_id ");
        if ( fromDate.length() > 4 ) {
            sbSql.append(" and order_header.order_date >= '" + fromDate + "'");
        }
        if ( toDate.length() > 4 ) {
            sbSql.append(" and order_header.order_date <= '" + toDate + "'");
        }
        
        sbSql.append(" order by 1,2,3,4,5 ");
        
        Debug.logInfo("Sql = " + sbSql.toString() );
        
        double rowCount = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String helperName = delegator.getEntityHelperName("OrderItem");
        
        try{
            connection = ConnectionFactory.getConnection(helperName);
            preparedStatement = connection.prepareStatement(sbSql.toString());
            rs = preparedStatement.executeQuery();
            
            Debug.logInfo("rs = " +  rs.toString() );
            
            File reportFile = new File(application.getRealPath("/reports/" + reportName));
            System.err.println("filepath = " + application.getRealPath("/reports/" + reportName) );
            Map parameters = new HashMap();
            parameters.put("dateRange", fromDate + " - " + toDate );
            //parameters.put("BaseDir", reportFile.getParentFile());
            byte[] bytes;
            if ( rs != null ) {
                Debug.logInfo("Calling Jasper" );
                bytes =
                JasperManager.runReportToPdf(
                reportFile.getPath(),
                parameters,
                new JRResultSetDataSource(rs)
                );
            } else {
                bytes =
                JasperManager.runReportToPdf(
                reportFile.getPath(),
                parameters,
                new JREmptyDataSource()
                );
            }
            
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            
            ServletOutputStream ouputStream = response.getOutputStream();
            ouputStream.write(bytes, 0, bytes.length);
            ouputStream.flush();
            ouputStream.close();
            //NOTE: this method messes up the binary output, the above method works but throws an exception in some app servers
            //if (UtilJ2eeCompat.useOutputStreamNotWriter(pageContext.getServletContext())) {
            //} else {
            //    char[] chars = new char[bytes.length];
            //    for (int ind = 0; ind < bytes.length; ind++) {
            //        chars[ind] = (char) bytes[ind];
            //    }
            //    response.getWriter().write(chars, 0, chars.length);
            //    response.getWriter().flush();
            //    response.getWriter().close();
            //}
            
            
        } catch(SQLException e){
            Debug.logError(e);
        } catch(Exception e){
            Debug.logError(e);
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(baos));
        Debug.logError(new String(baos.toByteArray()));*/
        } finally {
            try{
                //if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            } catch (SQLException e){
                Debug.logError(e);
            }
        }
        
        return "success";
    }
}
