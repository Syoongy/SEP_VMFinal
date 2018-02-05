package service;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("commerce")
public class ECommerceFacadeREST {

    @Context
    private UriInfo context;

    public ECommerceFacadeREST() {
    }

    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ECommerce
     *
     * @param mID
     * @param cID
     * @param content representation for the resource
     * @param aPaid
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Path("createECommerceTransactionRecord")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createECommerceTransactionRecord(@QueryParam("memberID") Long mID, @QueryParam("countryID") Long cID, 
            @QueryParam("amountPaid") double aPaid) {
        
            Long salesRecordID = null;
        try {
            String currDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String sqlStr = "INSERT INTO salesrecordentity (AMOUNTDUE, AMOUNTPAID, AMOUNTPAIDUSINGPOINTS, CREATEDDATE, CURRENCY, LOYALTYPOINTSDEDUCTED, POSNAME, RECEIPTNO, SERVEDBYSTAFF, MEMBER_ID, STORE_ID) VALUES(?, ?, 0, ?, (SELECT countryentity.CURRENCY FROM countryentity WHERE countryentity.ID = ?), 0, null, null, null, ?, 59)";
            PreparedStatement pstmt = conn.prepareStatement(sqlStr);
            pstmt.setString(1, Double.toString(aPaid));
            pstmt.setString(2, Double.toString(aPaid));
            pstmt.setString(3, currDateTime);
            pstmt.setLong(4, cID);
            pstmt.setLong(5, mID);
            
            int result = pstmt.executeUpdate();
            
            sqlStr = "SELECT max(ID) AS 'salesRecordID' FROM salesrecordentity";
            pstmt = conn.prepareStatement(sqlStr);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                salesRecordID = rs.getLong("salesRecordID");
            }
            
            conn.close();
        } catch (Exception ex) {
            System.out.println(ex);
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        
        return Response
                .status(Response.Status.CREATED)
                .entity(salesRecordID + "")
                .build();

    }
    
    /**
     * PUT method for updating or creating an instance of ECommerce
     *
     * @param sRID
     * @param cID
     * @param iEID
     * @param q
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Path("createECommerceLineItemRecord")
    @Consumes("application/json")
    @Produces("application/json")
    public Response createECommerceLineItemRecord(@QueryParam("salesRecordID") Long sRID, @QueryParam("countryID") Long cID, 
            @QueryParam("itemEntityID") Long iEID, @QueryParam("quantity") int q) {
        
            Long salesRecordID = null;
        try {
            String currDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String sqlStr = "INSERT INTO lineitementity (PACKTYPE, QUANTITY, ITEM_ID) VALUES(null, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sqlStr);
            pstmt.setString(1, Integer.toString(q));
            pstmt.setString(2, iEID.toString());
            
            int result = pstmt.executeUpdate();
            
            sqlStr = "SELECT max(ID) AS 'lineItemID' FROM lineitementity";
            pstmt = conn.prepareStatement(sqlStr);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                Long lineItemID = rs.getLong("lineItemID");
                sqlStr = "INSERT INTO salesrecordentity_lineitementity (SalesRecordEntity_ID, itemsPurchased_ID) VALUES(?, ?)";
                pstmt = conn.prepareStatement(sqlStr);
                pstmt.setLong(1, sRID);
                pstmt.setLong(2, (lineItemID));
                
                int results = pstmt.executeUpdate();
            }
            
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex);
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        return Response
                .status(Response.Status.CREATED)
                .entity(1 + "")
                .build();

    }
    
    
}
