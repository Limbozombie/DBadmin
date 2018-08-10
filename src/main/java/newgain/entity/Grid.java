package newgain.entity;

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
    
    public static class Head {
        
        private int width;
        private String type;
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
    
    public static class Rows {
        
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
    
}
