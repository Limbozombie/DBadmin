package newgain.entity.tree;

import java.util.List;

public class TreeDB {
    
    private String id;
    private String text;
    private Icons icons;
    private List<TreeTable> items;
    
    public TreeDB(String id , String text , Icons icons , List<TreeTable> items) {
        
        this.id = id;
        this.text = text;
        this.icons = icons;
        this.items = items;
    }
    
    public void setId(String id) {
        
        this.id = id;
    }
    
    public void setText(String text) {
        
        this.text = text;
    }
    
    public void setIcons(Icons icons) {
        
        this.icons = icons;
    }
    
    public void setItems(List<TreeTable> items) {
        
        this.items = items;
    }
}
