package gsi.project;

/**
 *
 * @author Juan Luis Molina
 */
public class Constantes {
	public static final String PROJECT = "caronte"; // "desastres"
	public static final int SERVER_PORT = 8080;
	public static final String DB = "hsqldb"; // "mysql"
	public static final int DB_PORT = 3306;
	private static final String DB_MODE = "local"; // "remote"
	
	private static final String HSQLDB_DRIVER = "org.hsqldb.jdbcDriver";
	private static final String HSQLDB_URL = "jdbc:hsqldb:file:";
	private static final String HSQLDB_USER = "sa";
	private static final String HSQLDB_PASS = "";
	
	private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	private static final String MYSQL_URL = "jdbc:mysql://localhost:" + DB_PORT + "/" + PROJECT;
	private static final String MYSQL_LOCAL_USER = "root";
	private static final String MYSQL_REMOTE_USER = "jlmolina";
	private static final String MYSQL_LOCAL_PASS = "";
	private static final String MYSQL_REMOTE_PASS = "NafDuJ4VhG6dcmtP";
	private static final String MYSQL_USER = DB_MODE.equals("local") ? MYSQL_LOCAL_USER : MYSQL_REMOTE_USER;
	private static final String MYSQL_PASS = DB_MODE.equals("local") ? MYSQL_LOCAL_PASS : MYSQL_REMOTE_PASS;
	
	public static final String DB_DRIVER = DB.equals("hsqldb") ? HSQLDB_DRIVER : MYSQL_DRIVER;
	public static final String DB_URL = DB.equals("hsqldb") ? HSQLDB_URL : MYSQL_URL;
	public static final String DB_USER = DB.equals("hsqldb") ? HSQLDB_USER : MYSQL_USER;
	public static final String DB_PASS = DB.equals("hsqldb") ? HSQLDB_PASS : MYSQL_PASS;
}