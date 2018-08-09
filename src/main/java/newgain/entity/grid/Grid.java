package newgain.entity.grid;

import java.util.List;

public class Grid {
    
    private List<Head> head;
    private List<Rows> rows;
    
    public Grid(List<Head> head , List<Rows> rows) {
        
        this.head = head;
        this.rows = rows;
    }
    
    public List<Head> getHead() {
        
        return head;
    }
    
    public void setHead(List<Head> head) {
        
        this.head = head;
    }
    
    public List<Rows> getRows() {
        
        return rows;
    }
    
    public void setRows(List<Rows> rows) {
        
        this.rows = rows;
    }
}
