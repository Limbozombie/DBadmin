package newgain.entity.tree;

import java.util.List;

public class TreeServer {
    
    private String id;
    private String text;
    private Icons icons;
    private List<TreeDB> items;
    
    public TreeServer(String id , String text , Icons icons , List<TreeDB> items) {
        
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
    
    public void setItems(List<TreeDB> items) {
        
        this.items = items;
    }
    
    public String getId() {
        
        return id;
    }
    
    public String getText() {
        
        return text;
    }
    
    public Icons getIcons() {
        
        return icons;
    }
    
    public List<TreeDB> getItems() {
        
        return items;
    }
}
