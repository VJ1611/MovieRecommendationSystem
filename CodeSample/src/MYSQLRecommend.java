import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;

public class MYSQLRecommend {

  public static void main(String args[]) throws ParseException {
	 
    Scanner scanner = new Scanner(System.in);
    int choice = 0;
    System.out.println("Welcome.. Enter your choice\n"+
    		"1 to give movie recommendations using User based collabrative filtering.\n"+
            "2 to give movie recommendations using Item based filtering.\n"+
                       "3 to exit");
    do {
		String ch=scanner.next();
		if (ch.matches("[0-9]+"))
		{
    		choice=Integer.parseInt(ch);
		}
    	else
    	{
    		System.out.println("Please enter only numeric values.");
    	return;
    	}
      if (choice == 1)
        new MYSQLRecommend().query1();
      else if (choice == 2)
        new MYSQLRecommend().query2();
      else if (choice == 3) {
        System.out.println("bye..");
        System.exit(0);
      } else
          System.out.println("Wrong choice.. Try again");
      System.out.println("Welcome.. Enter your choice \n"+
    		  "1 to give movie recommendations using User based collabrative filtering.\n"+
              "2 to give movie recommendations using Item based filtering.\n"+
                         "3 to exit");
    } while (choice != 3);
    scanner.close();
  }

  public void query1() throws ParseException {	  
		HashSet<Integer> UserMovies = new HashSet<>();
		HashMap<Integer, Double> userScore = new HashMap<>();
		HashMap<Integer, Double> userScoreOthers = new HashMap<>();
		HashMap<Integer, Double> movieList = new HashMap<>();
		HashMap<Integer, Double> movieListCount = new HashMap<>();
		HashMap<Integer, String> movieName = new HashMap<>();
		HashSet<Integer> similar_Users2 = new HashSet<>();
		String zipcode = "";
		String occupation = "";
		String gender = "";
		int age = 0;
		int cnt = 0;
		int tcnt=0;
		 try {
			  Properties prop = new Properties();
					InputStream input = null;
				 String srv="";
				 String db="";
				 String usr="";
				 String pwd="";
					try {
				 
						input = new FileInputStream("config.properties");
				 
						// load a properties file
						prop.load(input);
				 
						// get the property value and print it out
						srv=prop.getProperty("server");
						db=prop.getProperty("database");
						usr=prop.getProperty("dbuser");
						pwd=prop.getProperty("dbpassword");
				 
					} catch (IOException ex) {
						ex.printStackTrace();
					}
	            String url = srv+db;
	            Connection conn = DriverManager.getConnection(url,usr,pwd);
	            Statement stmt = conn.createStatement();
	            Statement stmt1 = conn.createStatement();
	            ResultSet rs;
	            System.out.println("Enter UID (1-943):");
	            Scanner scanner= new Scanner(System.in);
	            int num=0;
				String st1=scanner.next();
				if (st1.matches("[0-9]+"))
				{
					if(!((Integer.parseInt(st1)>0) && (Integer.parseInt(st1)<944)))
					{
						System.out.println("Please enter user id's between 1-943 only.");
						return;
					}
					else
					{
						num=Integer.parseInt(st1);
					}
				}						
				else
				{
					System.out.println("Please enter only numeric values.");
					return;
				}
				int mrat=0;
				System.out.println("Enter Minimum Rating (1-5):");
				String st2=scanner.next();
				if (st2.matches("[0-9]+"))
				{
					if(!((Integer.parseInt(st2)>0) && (Integer.parseInt(st2)<6)))
					{
						System.out.println("Please enter user id's between 1-5 only.");
						return;
					}
					else
					{
						mrat=Integer.parseInt(st2);
					}
				}
				else
				{
					System.out.println("Please enter only numeric values.");
					return;
				}
				long time1 = System.nanoTime();
	            ResultSet rs1=stmt.executeQuery("select users.userid,users.age,users.occupation,users.gender,users.zip,temtab.cn from users inner join (SELECT count(*) as cn, ratinginfo.userid as uid FROM ratinginfo WHERE userid ="+num+" and rating>="+mrat+") as temtab on users.userid=temtab.uid");
	            while(rs1.next())
	            {
	            	cnt=rs1.getInt("cn");
	            	age=rs1.getInt("age");
	            	gender=rs1.getString("users.gender");
	            	occupation=rs1.getString("users.occupation");
	            	zipcode=rs1.getString("users.zip");
	            	//System.out.println(cnt+" "+age+" "+ gender+" "+occupation+" "+zipcode);
	            }
	            
	            /*rs1=stmt.executeQuery("SELECT count(*) as movcnt FROM ratinginfo WHERE userid ="+num+" and rating>3");
	            while(rs1.next() )
	            {
	            	cnt=rs1.getInt("movcnt");
	            }*/
	            rs1 = stmt.executeQuery("select users.userid, users.age, users.occupation, users.gender, users.zip, tem.cnt from users inner join (Select ratinginfo.userid as uid,count(ratinginfo.userid) as cnt from ratinginfo where ratinginfo.movieid in (SELECT movieid FROM ratinginfo WHERE userid ="+num+" and rating>="+mrat+") and ratinginfo.rating>="+mrat+" and ratinginfo.userid <>"+num+" group by ratinginfo.userid) as tem on users.userid=tem.uid;");
	           int v=0;
	            while ( rs1.next() ) 
	            {
	            	
	            	if(((double)rs1.getInt("tem.cnt"))>cnt/3)
	            	{
							userScore.put(rs1.getInt("users.userid"), (double)rs1.getInt("tem.cnt"));
							int diff=0;
							if(age<=rs1.getInt("users.age"))
							{
								diff=rs1.getInt("users.age")-age;
							}
							else
							{
								diff=age-rs1.getInt("users.age");
							}
							if (diff<=5) 
							{
								userScoreOthers.put(rs1.getInt("users.userid"), 2.0);
							} 
							else if (diff<=10) 
							{
								userScoreOthers.put(rs1.getInt("users.userid"), 1.5);
							} 
							else if (diff<=15) 
							{
								userScoreOthers.put(rs1.getInt("users.userid"), 1.0);
							} 
							else if (diff<=20) 
							{
								userScoreOthers.put(rs1.getInt("users.userid"), 0.5);
							} 
							else 
							{
								userScoreOthers.put(rs1.getInt("users.userid"), 0.0);
							}
							if (gender.contains(rs1.getString("users.gender")))
							{
								userScoreOthers.put(rs1.getInt("users.userid"),userScoreOthers.get(rs1.getInt("users.userid")) + 1);
							}
							if (occupation.contains(rs1.getString("users.occupation")))
							{
								userScoreOthers.put(rs1.getInt("users.userid"),userScoreOthers.get(rs1.getInt("users.userid")) + 1.5);
							}
							userScore.put(rs1.getInt("users.userid"),(((double)rs1.getInt("tem.cnt")*5.5)/cnt)+userScoreOthers.get(rs1.getInt("users.userid")) );
	            		v++;
	            		//System.out.println(v+". id:"+rs1.getString("users.userid")+"  age:"+rs1.getString("users.age")+"  gender:"+rs1.getString("users.gender")+"  occupation:"+rs1.getString("users.occupation")+"  count:"+rs1.getInt("tem.cnt")+" score:"+userScoreOthers.get(rs1.getInt("users.userid"))+" user score:"+ userScore.get(rs1.getInt("users.userid")));
	            	}
	            }
	            
	            Set<Integer> set = userScore.keySet();
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.addAll(set);
				Collections.sort(list);
				String qry="";
				for (Integer key : list) 
				{
					if(!qry.isEmpty())
						qry+=",";
					qry+=key;
				}
				qry="select ratinginfo.movieid,moviesinfo.title,ratinginfo.rating,ratinginfo.userid from ratinginfo inner join moviesinfo on moviesinfo.movieid= ratinginfo.movieid where userid in("+qry+") and ratinginfo.rating>3 and ratinginfo.movieid not in (select  movieid from ratinginfo where userid="+num+" and rating>="+mrat+");";
				rs1 = stmt.executeQuery(qry);
		            while ( rs1.next() ) 
		            {
		            	if (movieList.containsKey(rs1.getInt("ratinginfo.movieid"))) 
						{
							movieListCount.put(rs1.getInt("ratinginfo.movieid"),movieListCount.get(rs1.getInt("ratinginfo.movieid")) + 1.0);
							//movieList.put((Integer) row2.get("movies.id"),(movieList.get((Integer) row2.get("movies.id")) + ((Integer) row2.get("rel.rating") / 5.0))+ userScore.get(key));
							movieList.put(rs1.getInt("ratinginfo.movieid"),(movieList.get(rs1.getInt("ratinginfo.movieid")) + ((rs1.getInt("ratinginfo.rating")*2) / 5.0)+ userScore.get(rs1.getInt("ratinginfo.userid"))));
						} 
						else {
							movieName.put(rs1.getInt("ratinginfo.movieid"),rs1.getString("moviesinfo.title"));
							movieListCount.put(rs1.getInt("ratinginfo.movieid"), 1.0);
							//movieList.put((Integer) row2.get("movies.id"),(((Integer) row2.get("rel.rating") / 5.0)/movieListCount.get(movieListCount.get((Integer) row2.get("movies.id"))))+ userScore.get(key));
							movieList.put(rs1.getInt("ratinginfo.movieid"),((rs1.getInt("ratinginfo.rating")*2) / 5.0)+ userScore.get(rs1.getInt("ratinginfo.userid")));
						}
		            }
		            
		            Set<Integer> set1 = movieList.keySet();
					ArrayList<Integer> list1 = new ArrayList<Integer>();
					list1.addAll(set1);
					Collections.sort(list1);
					for (Integer key : list1) {
						movieList.put(key,(movieList.get(key) / movieListCount.get(key)));
						//System.out.println("Movie-" + key + ": " + movieList.get(key));
					}
					
					ValueComparator bvc = new ValueComparator(movieList);
					TreeMap<Integer, Double> sorted_map = new TreeMap<Integer, Double>(
							bvc);
					sorted_map.putAll(movieList);
					for (int i = 1; i < 11; i++) {
						Entry<Integer, Double> e = sorted_map.pollFirstEntry();
						Integer pname = e.getKey();
						Double score = e.getValue();
						String moviename = movieName.get(pname);
						//System.out.println((movieListCount.get(pname)*100/tcnt) );
						System.out.println(i + ". MovieId-" + pname + ": Movie Name- "+ moviename + "--" + score);
					}
					long time2 = System.nanoTime();
					long timeTaken = time2 - time1;  
					System.out.println("Time taken to give these recommendations: " + timeTaken + " ns");
	            conn.close();
	            
	            
	        } catch (Exception e) {
	            System.err.println("Got an exception! ");
	            System.err.println(e.getMessage());
	        }
}
	
  public void query2() {
	  
		HashSet<Integer> UserMovies = new HashSet<>();
		HashMap<Integer, Double> generScore = new HashMap<>();
		HashMap<Integer, Double> userScoreOthers = new HashMap<>();
		HashMap<Integer, Double> movieList = new HashMap<>();
		HashMap<Integer, Double> movieListCount = new HashMap<>();
		HashMap<Integer, String> movieName = new HashMap<>();
		HashMap<String, Double> genersCollection = new HashMap<>();
		HashMap<Integer, Double> yearsCollection = new HashMap<>();
		HashMap<Integer, Double> yearsCollection2 = new HashMap<>();
		String zipcode = "";
		String occupation = "";
		String gender = "";
		int age = 0;
		int cnt = 0;
		try {
			Properties prop = new Properties();
					InputStream input = null;
			String srv="";
				 String db="";
				 String usr="";
				 String pwd="";
					try {
				 
						input = new FileInputStream("config.properties");
				 
						// load a properties file
						prop.load(input);
				 
						// get the property value and print it out
						srv=prop.getProperty("server");
						db=prop.getProperty("database");
						usr=prop.getProperty("dbuser");
						pwd=prop.getProperty("dbpassword");
				 
					} catch (IOException ex) {
						ex.printStackTrace();
					}
            String url = srv+db;
            Connection conn = DriverManager.getConnection(url,usr,pwd);
            Statement stmt = conn.createStatement();
            Statement stmt1 = conn.createStatement();
            ResultSet rs;
            System.out.println("Enter UID:");
            Scanner scanner= new Scanner(System.in);
            
            int num=0;//scan.nextInt();
			
			String st1=scanner.next();
				if (st1.matches("[0-9]+"))
				{
					if(!((Integer.parseInt(st1)>0) && (Integer.parseInt(st1)<944)))
					{
						System.out.println("Please enter user id's between 1-943 only.");
						return;
					}
					else
					{
						num=Integer.parseInt(st1);
					}
				}						
				else
				{
					System.out.println("Please enter only numeric values.");
					return;
				}
			
			
            long time1 = System.nanoTime();
            ResultSet rs1=stmt.executeQuery("select moviesinfo.movieid,moviesinfo.action,moviesinfo.adventure,moviesinfo.animation, moviesinfo.childrens, moviesinfo.comedy,moviesinfo.crime, moviesinfo.documentary, moviesinfo.drama, moviesinfo.fantasy, moviesinfo.horror, moviesinfo.musical ,moviesinfo.mystery, moviesinfo.noir, moviesinfo.romance, moviesinfo.thriller,moviesinfo.scifi, moviesinfo.unknown1,moviesinfo.war,moviesinfo.western,moviesinfo.date1 from moviesinfo inner join ratinginfo on moviesinfo.movieid= ratinginfo.movieid where ratinginfo.userid="+num+" and ratinginfo.rating>3;");
            while(rs1.next())
            {
            	cnt++;
            	if(yearsCollection.containsKey(Integer.parseInt(rs1.getString("date1").split("-")[2])))
				{
            		yearsCollection.put(Integer.parseInt(rs1.getString("date1").split("-")[2]), yearsCollection.get(Integer.parseInt(rs1.getString("date1").split("-")[2]))+1.0);
				}
				else
				{
					yearsCollection.put(Integer.parseInt(rs1.getString("date1").split("-")[2]),1.0);
				}
            	
            	if(rs1.getString("action").contains("1"))
            	{
            		if(genersCollection.containsKey("action"))
					{
						genersCollection.put("action", genersCollection.get("action")+1.0);
					}
					else
					{
						genersCollection.put("action",1.0);
					}
            	}
            	if(rs1.getString("adventure").contains("1"))
            	{
            		if(genersCollection.containsKey("adventure"))
					{
						genersCollection.put("adventure", genersCollection.get("adventure")+1.0);
					}
					else
					{
						genersCollection.put("adventure",1.0);
					}
            	}
            	if(rs1.getString("animation").contains("1"))
            	{
            		if(genersCollection.containsKey("animation"))
					{
						genersCollection.put("animation", genersCollection.get("animation")+1.0);
					}
					else
					{
						genersCollection.put("animation",1.0);
					}
            	}
            	if(rs1.getString("childrens").contains("1"))
            	{
            		if(genersCollection.containsKey("childrens"))
					{
						genersCollection.put("childrens", genersCollection.get("childrens")+1.0);
					}
					else
					{
						genersCollection.put("childrens",1.0);
					}
            	}
            	if(rs1.getString("comedy").contains("1"))
            	{
            		if(genersCollection.containsKey("comedy"))
					{
						genersCollection.put("comedy", genersCollection.get("comedy")+1.0);
					}
					else
					{
						genersCollection.put("comedy",1.0);
					}
            	}
            	if(rs1.getString("crime").contains("1"))
            	{
            		if(genersCollection.containsKey("crime"))
					{
						genersCollection.put("crime", genersCollection.get("crime")+1.0);
					}
					else
					{
						genersCollection.put("crime",1.0);
					}
            	}
            	if(rs1.getString("drama").contains("1"))
            	{
            		if(genersCollection.containsKey("drama"))
					{
						genersCollection.put("drama", genersCollection.get("drama")+1.0);
					}
					else
					{
						genersCollection.put("drama",1.0);
					}
            	}
            	if(rs1.getString("documentary").contains("1"))
            	{
            		if(genersCollection.containsKey("documentary"))
					{
						genersCollection.put("documentary", genersCollection.get("documentary")+1.0);
					}
					else
					{
						genersCollection.put("documentary",1.0);
					}
            	}
            	if(rs1.getString("fantasy").contains("1"))
            	{
            		if(genersCollection.containsKey("fantasy"))
					{
						genersCollection.put("fantasy", genersCollection.get("fantasy")+1.0);
					}
					else
					{
						genersCollection.put("fantasy",1.0);
					}
            	}
            	if(rs1.getString("mystery").contains("1"))
            	{
            		if(genersCollection.containsKey("mystery"))
					{
						genersCollection.put("mystery", genersCollection.get("mystery")+1.0);
					}
					else
					{
						genersCollection.put("mystery",1.0);
					}
            	}
            	
            	if(rs1.getString("horror").contains("1"))
            	{
            		if(genersCollection.containsKey("horror"))
					{
						genersCollection.put("horror", genersCollection.get("horror")+1.0);
					}
					else
					{
						genersCollection.put("horror",1.0);
					}
            	}
            	if(rs1.getString("musical").contains("1"))
            	{
            		if(genersCollection.containsKey("musical"))
					{
						genersCollection.put("musical", genersCollection.get("musical")+1.0);
					}
					else
					{
						genersCollection.put("musical",1.0);
					}
            	}
            	if(rs1.getString("noir").contains("1"))
            	{
            		if(genersCollection.containsKey("noir"))
					{
						genersCollection.put("noir", genersCollection.get("noir")+1.0);
					}
					else
					{
						genersCollection.put("noir",1.0);
					}
            	}
            	if(rs1.getString("romance").contains("1"))
            	{
            		if(genersCollection.containsKey("romance"))
					{
						genersCollection.put("romance", genersCollection.get("romance")+1.0);
					}
					else
					{
						genersCollection.put("romance",1.0);
					}
            	}
            	if(rs1.getString("scifi").contains("1"))
            	{
            		if(genersCollection.containsKey("scifi"))
					{
						genersCollection.put("scifi", genersCollection.get("scifi")+1.0);
					}
					else
					{
						genersCollection.put("scifi",1.0);
					}
            	}
            	
            	if(rs1.getString("unknown1").contains("1"))
            	{
            		if(genersCollection.containsKey("unknown1"))
					{
						genersCollection.put("unknown1", genersCollection.get("unknown1")+1.0);
					}
					else
					{
						genersCollection.put("unknown1",1.0);
					}
            	}
            	if(rs1.getString("thriller").contains("1"))
            	{
            		if(genersCollection.containsKey("thriller"))
					{
						genersCollection.put("thriller", genersCollection.get("thriller")+1.0);
					}
					else
					{
						genersCollection.put("thriller",1.0);
					}
            	}
            	if(rs1.getString("war").contains("1"))
            	{
            		if(genersCollection.containsKey("war"))
					{
						genersCollection.put("war", genersCollection.get("war")+1.0);
					}
					else
					{
						genersCollection.put("war",1.0);
					}
            	}
            	if(rs1.getString("western").contains("1"))
            	{
            		if(genersCollection.containsKey("western"))
					{
						genersCollection.put("western", genersCollection.get("western")+1.0);
					}
					else
					{
						genersCollection.put("western",1.0);
					}
            	}
            }
		
			int si=0;
			int ysi=0;
			if(genersCollection.size()>3)
			{
				si=3;
			}
			else
			{
				si=genersCollection.size();
			}
			
			if(yearsCollection.size()>6)
			{
				ysi=6;
			}
			else
			{
				ysi=yearsCollection.size();
			}
			
			ValueComp2 bvc = new ValueComp2(genersCollection);
			TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
			sorted_map.putAll(genersCollection);
			genersCollection.clear();
			String qry_Gener="";
			String qry_Gener1="";
			for (int i = 1; i <= si; i++) {
				Entry<String, Double> e = sorted_map.pollFirstEntry();
				String pname = e.getKey();
				Double score = e.getValue();
				genersCollection.put(pname, score/cnt);
				//System.out.println(i + ". " + pname + "--" + score/cnt);
				if(!qry_Gener.isEmpty())
					qry_Gener+=" or ";
				qry_Gener+="moviesinfo."+pname+"=1";
				if(!qry_Gener1.isEmpty())
					qry_Gener1+=",";
				qry_Gener1+="moviesinfo."+pname;
			}
			ValueComparator y_bvc = new ValueComparator(yearsCollection);
			TreeMap<Integer, Double> y_sorted_map = new TreeMap<Integer, Double>(y_bvc);
			y_sorted_map.putAll(yearsCollection);
			yearsCollection.clear();
			String qry_Year="";
			for (int i = 1; i <= ysi; i++) {
				Entry<Integer, Double> e = y_sorted_map.pollFirstEntry();
				Integer pname = e.getKey();
				Double score = e.getValue();
				yearsCollection.put(pname, score/cnt);
				//System.out.println(i + ". " + pname + "--" + score/cnt);
				
				if(!qry_Year.isEmpty())
					qry_Year+=" or ";
				qry_Year+= "moviesinfo.date1 LIKE \"%"+pname+"\"";
			}
			
			Set<Integer> set = yearsCollection.keySet();
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.addAll(set);
			Collections.sort(list);
			for(int i=0;i<list.size();i++)
			{
				int cyear=list.get(i);
				double cScore=0.0;
				for(int j=0;j<list.size();j++)
				{
					int secYear=list.get(j);
					if(cyear!=secYear)
					{
					int diff=0;
					if(cyear<=secYear)
					{
						diff=secYear-cyear;
					}
					else
					{
						diff=cyear-secYear;
					}
					if(diff<=5)
					{
						cScore+=yearsCollection.get(secYear);
					}
					else if(diff<=15)
					{
						cScore+=yearsCollection.get(secYear)/2.0;
					}
					else if(diff<=30)
					{
						cScore+=yearsCollection.get(secYear)/4.0;
					}
					else if(diff<=40)
					{
						cScore+=yearsCollection.get(secYear)/10.0;
					}
					}
				}
				yearsCollection2.put(cyear, yearsCollection.get(cyear)+cScore);
				//System.out.println(cyear+"--"+yearsCollection2.get(cyear));
			}
			int totalCount=0;
			
			rs1=stmt.executeQuery("select ratinginfo.rating,moviesinfo.movieid, moviesinfo.title, "+qry_Gener1+",moviesinfo.date1	from moviesinfo inner join ratinginfo  on ratinginfo.movieid= moviesinfo.movieid where ratinginfo.rating>3 and("+qry_Gener+") and ("+qry_Year+");");
            while(rs1.next())
            {
            	totalCount++;
            	
            	int yearFin=Integer.parseInt((rs1.getString("date1")).split("-")[2]);
				movieName.put(rs1.getInt("movieid"), rs1.getString("title"));
				if(movieListCount.containsKey(rs1.getInt("movieid")))
				{
					movieListCount.put(rs1.getInt("movieid"),movieListCount.get(rs1.getInt("movieid"))+1.0);
				}
				else
				{
					movieListCount.put(rs1.getInt("movieid"), 1.0);
				}
				
				if (movieList.containsKey(rs1.getInt("movieid"))) 
				{
				} 
				else 
				{
					movieList.put(rs1.getInt("movieid"), yearsCollection2.get(yearFin));
					double scr=0.0;
					Set<String> set1 = genersCollection.keySet();
					ArrayList<String> list1 = new ArrayList<String>();
					list1.addAll(set1);
					Collections.sort(list1);
					for (String key : list1) {
						if(rs1.getString(key).contains("1"))
							scr+=genersCollection.get(key);
					}
					movieList.put(rs1.getInt("movieid"),movieList.get(rs1.getInt("movieid"))+scr);
				}            	
            }
            Set<Integer> set1 = movieList.keySet();
			ArrayList<Integer> list1 = new ArrayList<Integer>();
			list1.addAll(set1);
			Collections.sort(list1);
			for (Integer key : list1) {
				movieList.put(key,movieList.get(key)+(movieListCount.get(key)/totalCount));
			}
			
			ValueComparator bvcfin = new ValueComparator(movieList);
			TreeMap<Integer, Double> sorted_mapfin = new TreeMap<Integer, Double>(bvcfin);
			sorted_mapfin.putAll(movieList);
			for (int i = 1; i < 11; i++) {
				Entry<Integer, Double> e = sorted_mapfin.pollFirstEntry();
				Integer pname = e.getKey();
				Double score = e.getValue();
				String moviename = movieName.get(pname);
				System.out.println(i + ". MovieId- " + pname + ": Movie Name- "
						+ moviename + "--" + score);
			}
			long time2 = System.nanoTime();
			long timeTaken = time2 - time1;  
			System.out.println("Time taken to give these recommendations: " + timeTaken + " ns");
		
	  
			conn.close();
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
  }
}