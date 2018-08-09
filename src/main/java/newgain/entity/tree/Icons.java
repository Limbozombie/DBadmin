package newgain.entity.tree;

public class Icons {
    
    private String folder_opened;
    private String folder_closed;
    private String file;
    
    public Icons(String folder_opened , String folder_closed , String file) {
        
        this.folder_opened = folder_opened;
        this.folder_closed = folder_closed;
        this.file = file;
    }
    
    public Icons(String common) {
        
        this.folder_opened = common;
        this.folder_closed = common;
        this.file = common;
    }
    
    public String getFolder_opened() {
        
        return folder_opened;
    }
    
    public void setFolder_opened(String folder_opened) {
        
        this.folder_opened = folder_opened;
    }
    
    public String getFolder_closed() {
        
        return folder_closed;
    }
    
    public void setFolder_closed(String folder_closed) {
        
        this.folder_closed = folder_closed;
    }
    
    public String getFile() {
        
        return file;
    }
    
    public void setFile(String file) {
        
        this.file = file;
    }
}
