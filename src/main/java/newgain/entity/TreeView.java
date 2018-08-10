package newgain.entity;

import java.util.List;

public class TreeView {
    
    private String id;
    private String text;
    private Icons icons;
    private List<TreeDB> items;
    
    public TreeView(String id , String text , Icons icons , List<TreeDB> items) {
        
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
    
    public static class TreeDB {
        
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
        
        public String getId() {
            
            return id;
        }
        
        public String getText() {
            
            return text;
        }
        
        public Icons getIcons() {
            
            return icons;
        }
        
        public List<TreeTable> getItems() {
            
            return items;
        }
        
        public static class TreeTable {
            
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
    }
}
