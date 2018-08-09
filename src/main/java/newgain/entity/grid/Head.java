package newgain.entity.grid;

public class Head {
    
    private int width;
    private  String type;
    private String align;
    private String value;
    
    public Head(int width , String type , String align , String value) {
        
        this.width = width;
        this.type = type;
        this.align = align;
        this.value = value;
    }
    
    public int getWidth() {
        
        return width;
    }
    
    public void setWidth(int width) {
        
        this.width = width;
    }
    
    public String getType() {
        
        return type;
    }
    
    public void setType(String type) {
        
        this.type = type;
    }
    
    public String getAlign() {
        
        return align;
    }
    
    public void setAlign(String align) {
        
        this.align = align;
    }
    
    public String getValue() {
        
        return value;
    }
    
    public void setValue(String value) {
        
        this.value = value;
    }
}
