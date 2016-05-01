import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.*;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class CreateMySQLDB {
	public static void main(String[] args) 
    {
		Properties prop = new Properties();
					InputStream input = null;
				 String srv1="";
				 String db1="";
				 String usr1="";
				 String pwd1="";
					try {
				 
						input = new FileInputStream("config.properties");
				 
						// load a properties file
						prop.load(input);
				 
						// get the property value and print it out
						srv1=prop.getProperty("server");
						db1=prop.getProperty("database");
						usr1=prop.getProperty("dbuser");
						pwd1=prop.getProperty("dbpassword");
				 
					} catch (IOException ex) {
						ex.printStackTrace();
					}
        DBase db = new DBase();
       // Connection conn = db.connect("jdbc:mysql://localhost:3306/movies?useUnicode=yes&characterEncoding=UTF-8","root","root");
	   Connection conn = db.connect(srv1+db1,usr1,pwd1);
       db.importData(conn);
    }
}

class DBase
{
    public DBase()
    {
    }

    public Connection connect(String db_connect_str,String db_userid, String db_password)
    {
        Connection conn;
        try 
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(db_connect_str,db_userid, db_password);        
        }
        catch(Exception e)
        {
            e.printStackTrace();
            conn = null;
        }

        return conn;    
    }
    	
    public void importData(Connection conn)
    {
        Statement stmt;
        String query1,query2,query3;
        try
        {
			Properties prop1 = new Properties();
					InputStream input1 = null;
				 String dbpath="";
				 
					try {
						input1 = new FileInputStream("config.properties");
						prop1.load(input1);
						dbpath=prop1.getProperty("dbpath");
					} catch (IOException ex) {
						ex.printStackTrace();
					}
        	System.out.println(conn);
        	stmt = conn.createStatement();
            long time1 = System.nanoTime();
            query1 = "LOAD DATA local INFILE '"+dbpath+"/dataset/udata.dat' INTO TABLE ratinginfo;";
			//System.out.println("LOAD DATA local INFILE '"+dbpath+"/dataset/udata.dat")+"' INTO TABLE ratinginfo;");
			query2 = "LOAD DATA local INFILE '"+dbpath+"/dataset/uitem.dat' into table moviesinfo character set latin1 fields  terminated by '|';";
			query3 = "LOAD DATA local INFILE '"+dbpath+"/dataset/uuser.dat' into table users  fields  terminated by ' ';";
            
            stmt.executeUpdate(query3);
			stmt.executeUpdate(query2);
			stmt.executeUpdate(query1);
			
			long time2 = System.nanoTime();
			long timeTaken = time2 - time1;  
			System.out.println("Time taken to Load data into Mysql: " + timeTaken + " ns");
                
        }
        catch(Exception e)
        {
            e.printStackTrace();
            stmt = null;
        }
    };
}
