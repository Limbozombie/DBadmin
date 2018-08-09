package newgain.entity.tree;

public class TreeTable {
    
    private String id;
    private String text;
    private Icons icons;
    
    public TreeTable(String id , String text , Icons icons) {
        
        this.id = id;
        this.text = text;
        this.icons = icons;
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
    
    public String getId() {
        
        return id;
    }
    
    public String getText() {
        
        return text;
    }
    
    public Icons getIcons() {
        
        return icons;
    }
}
