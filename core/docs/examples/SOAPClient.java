
import org.apache.axis.client.*;
import org.apache.axis.encoding.*;

public class SOAPClient {

    public static void main(String [] args) throws Exception{
        String endpoint = "http://localhost:8080/commonapp/control/SOAPService";
	 
        Service service = new Service();
        Call call = (Call) service.createCall();

        call.setTargetEndpointAddress( new java.net.URL(endpoint) );
        call.setOperationName( "testScv" );
        call.addParameter("message", XMLType.XSD_STRING, Call.PARAM_MODE_IN);
        
        String ret = (String) call.invoke( new Object[] { "Hello!" } );
        System.out.println("Got result: " + ret);
    }
}
