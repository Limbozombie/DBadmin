package newgain.entity.grid;

import java.util.List;

public class Rows {
    
    private int id;
    private List data;
    private String style;
    
    public Rows(int id , List data , String style) {
        
        this.id = id;
        this.data = data;
        this.style = style;
    }
    
    public int getId() {
        
        return id;
    }
    
    public void setId(int id) {
        
        this.id = id;
    }
    
    public List getData() {
        
        return data;
    }
    
    public void setData(List data) {
        
        this.data = data;
    }
    
    public String getStyle() {
        
        return style;
    }
    
    public void setStyle(String style) {
        
        this.style = style;
    }
}
