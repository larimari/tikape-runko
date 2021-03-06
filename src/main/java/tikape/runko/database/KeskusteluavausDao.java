package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.collector.KeskusteluavausCollector;
import tikape.runko.domain.Keskusteluavaus;

/**
 *
 * @author VOL
 */
public class KeskusteluavausDao implements Dao<Keskusteluavaus, Integer>{
    
    private Database database;
    
    public KeskusteluavausDao(Database database){
        this.database = database;
    }

    
    @Override
    public Keskusteluavaus findOne(Integer key) throws SQLException {
        List<Keskusteluavaus> avaus = this.database.queryAndCollect("SELECT * FROM Keskusteluavaus WHERE id = ?", new KeskusteluavausCollector(), key);
        if (avaus.isEmpty()) {
            return null;
        }

        return avaus.get(0);
    }

   

    @Override
    public void save(Keskusteluavaus avaus) throws SQLException {
        if(avaus.getId() < 0){
            this.database.update("INSERT INTO Keskusteluavaus (alue, otsikko) VALUES (?, ?, ?)", avaus.getAlueId(), avaus.getOtsikko());
        } else {
            this.database.update("INSERT INTO Keskusteluavaus (id, alue, otsikko) VALUES (?, ?, ?)", avaus.getId(), avaus.getAlueId(), avaus.getOtsikko());
        }
    }
    
    /**
     * Saves the given object to the database and returns the id that it was saved with
     * 
     * @param avaus The Keskusteluavaus-object to be saved
     * @return The id of the saved object
     * @throws SQLException 
     */
    public int saveAndGetGeneratedId(Keskusteluavaus avaus) throws SQLException{
        Connection con = database.getConnection();
        
        PreparedStatement stmnt = con.prepareStatement("INSERT INTO Keskusteluavaus(alue, otsikko) VALUES(?, ?)");
        
        stmnt.setInt(1, avaus.getAlueId());
        stmnt.setString(2, avaus.getOtsikko());
        
        stmnt.execute();
        
        ResultSet generatedKeys = stmnt.getGeneratedKeys();
        
        if(!generatedKeys.isClosed()){
            return generatedKeys.getInt(1);
        }
        
        return -1;
    }

    @Override
    public List<Keskusteluavaus> findAll() throws SQLException {
        return this.database.queryAndCollect("SELECT * FROM Keskusteluavaus", new KeskusteluavausCollector());
    }

    @Override
    public void delete(Integer key) throws SQLException {
        this.database.update("DELETE FROM Keskusteluavaus WHERE id = ?", key);
    }
    
    
    /**
     * Returns every Keskusteluavaus-object that are in the given Keskustelualue from the database
     * 
     * @param key the id of the Keskustelualue where the Keskusteluavaus-objects are wanted from
     * @return A list of Keskusteluavaus-objects that are under the specified Keskustelualue
     */
    public List<Keskusteluavaus> findAllFromAlue(int key) throws SQLException{
        Connection con = database.getConnection();
        
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Keskusteluavaus WHERE alue = '" + key +"'");
        
        //Find all Keskusteluavaus-objects where the alue is the same as the given key
        ResultSet rs = ps.executeQuery();
        
        List<Keskusteluavaus> avaukset = new ArrayList<>();
        
        while(rs.next()){
            //Get the needed information from the object
            int id = rs.getInt("id");
            int alue = rs.getInt("alue");
            String otsikko = rs.getString("otsikko");
            
            avaukset.add(new Keskusteluavaus(id, alue, otsikko));
        }
        
        return avaukset;
    }
}
