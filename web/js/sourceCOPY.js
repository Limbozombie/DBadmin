var dba = {}; //namespace

dba.start_text = "<br/><span class = 'info_message'>Select any entity in tree to view details</span>";
dba.i18n = {
    select_db: "You need to select DB first",
    select_connection: "Select any connection first",
    delete_connection: "Are you sure to delete connection:<br>"
};

dba.uid = function() {
    return (new Date()).valueOf();
};
dba.get_id_chain = function(tree,id) {
    var chain = [id];
    while (id = tree.getParentId(id))
        chain.push(id);

    return chain.reverse().join("|");
};
dba.pages = {};// store opened tab
dba.add_connection = function(server,user,pass) {
    top.setTimeout(function() {
        dhx4.ajax.get("./logic/php/connection.php?mode=add&server="+encodeURIComponent(server)+"&user="+encodeURIComponent(user)+"&pass="+encodeURIComponent(pass), function(xml){
            eval(xml.xmlDoc.responseText);
        });
        dba.popup_win.close();
    },1);
};
dba.delete_connection = function(server) {
    top.setTimeout(function() {
        dhx4.ajax.get("./logic/php/connection.php?mode=delete&server="+encodeURIComponent(server), function(xml){
            eval(xml.xmlDoc.responseText);
        });
    },1);
};
dba.create_tab = function(id,full_id,text,extra) {
    full_id = full_id||dba.get_id_chain(dba.tree,id);

    if (!dba.pages[full_id]) {
        var details = id.split("^");
        dba.tabbar.addTab(full_id,full_id,100);
        var win = dba.tabbar.cells(full_id);

        //using window instead of tab
        var toolbar = win.attachToolbar();
        toolbar.attachEvent("onClick",dba.tab_toolbar_click);
        toolbar.setIconsPath("./imgs/");
        toolbar.loadStruct("xml/toolbar_"+details[0]+".xml");

        dba.tabbar.cells(full_id).setActive();
        dba.tabbar.cells(full_id).setText(text||dba.tree.getItemText(id));
        switch(details[0]) {
            case "table":
                dba.set_data_table(win,full_id);
                break;
            case "query":
                dba.set_query_layout(win);
                break;
        }

        dba.pages[full_id] = win;
        win.extra = extra;
    }
    else dba.tabbar.cells(full_id).setActive();

};
dba.tab_toolbar_click = function(id) {
    switch(id) {
        case "close":
            var id = dba.tabbar.getActiveTab();
            delete dba.pages[id];
            dba.tabbar.tabs(id).close(true);
            break;
        case "run_query":
            var win = dba.tabbar.cells(dba.tabbar.getActiveTab());
            win.area.parentNode.removeChild(win.area);
            win.grid.post("./logic/php/sql.php","id="+encodeURIComponent(win.extra.join("|"))+"&sql="+encodeURIComponent(win.area.value));
            break;
        case "refresh_table":
            var win = dba.tabbar.cells(dba.tabbar.getActiveTab());
            win.grid.load(win.grid._refresh);
            break;

        case "show_structure":
            var win = dba.tabbar.cells(dba.tabbar.getActiveTab());
            win.grid.load(win.grid._refresh+"&struct=true");
            break;

        default:
            dhtmlx.alert({
                title:"Information!",
                type:"alert-error",
                text:"Not implemented"
            });
            break;
    }
};
dba.main_toolbar_click = function(id) {
    switch(id) {
        case "add_connection":
            var win = dba.layout.dhxWins.createWindow("creation",1,1,400,280);
            win.setText("Add connection");
            win.setModal(true);
            win.denyResize();
            win.center();
            // win.attachURL("connection.html?etc = "+new Date().getTime());
            var form = win.attachForm([
                {type: "block", width: "auto", offsetLeft: 35, offsetTop: 30, list: [
                        {type: "settings", labelWidth: 120},
                        {type: "input", name: "server", label: "Server Name", value: "", required: true},
                        {type: "input", name: "user", label: "User Name", value: ""},
                        {type: "password", name: "pass", label: "Password", value: ""},
                        {type: "button", name: "save", value: "Save", offsetLeft: 95, offsetTop: 30}
                    ]}
            ]);
            form.setFocusOnFirstActive();
            form.attachEvent("onButtonClick", function(name){
                var t = this.getFormData();
                if (t.server == "") {
                    dhtmlx.message({
                        type: "alert",
                        text: "You need to specify a server name"
                    });
                } else {
                    parent.dba.add_connection(t.server, t.user, t.pass)
                }
            });
            dba.popup_win = win;
            break;

        case "delete_connection":
            var data = dba.tree.getSelectedItemId();
            if (!data) {
                dhtmlx.alert({
                    title: "Information!",
                    type: "alert-error",
                    text: dba.i18n.select_connection
                });
                return;
            }
            data = dba.get_id_chain(dba.tree,data).split("|")[0];
            dhtmlx.confirm({
                title: "Information!",
                type: "confirm-error",
                text: dba.i18n.delete_connection + dba.tree.getItemText(data),
                callback: function(r) {
                    if (r == true) dba.delete_connection(data.split("^")[1]);
                }
            });
            break;

        case "sql_query":
            var data = dba.get_id_chain(dba.tree,dba.tree.getSelectedItemId()).split("|");
            if (data.length<2) {
                dhtmlx.alert({
                    title: "Information!",
                    type: "alert-error",
                    text: dba.i18n.select_db
                });
                return;
            }
            dba.create_tab("query",dba.uid(),"SQL : "+dba.tree.getItemText(data[0])+" : "+data[1].split("^")[1],data);
            break;
    }
};
dba.set_query_layout = function(win) {
    var grid = win.grid = win.attachGrid();
    grid.enableSmartRendering(true);
    grid.setHeader("<textarea style = 'width: 100%; height: 80px; '>Type SQL query here</textarea>")
    grid.setInitWidths("*");
    grid.init();
    grid.attachEvent("onXLE",function() {
        this.hdr.rows[1].cells[0].firstChild.appendChild(win.area);
        this.hdr.rows[1].cells[0].className = "grid_hdr_editable";
        this.setSizes();
        win.area.focus();
    });

    var area = grid.hdr.rows[1].cells[0];
    area.className = "grid_hdr_editable";
    area.onselectstart = function(e) { return ((e||event).cancelBubble = true); };

    area = area.firstChild.firstChild;
    area.focus();
    area.select();
    dhtmlxEvent(area,"keypress",function(e) {
        e = e||event;
        code = e.charCode||e.keyCode;
        if (e.ctrlKey && code == 13) dba.tab_toolbar_click("run_query");
    });

    win.area = area;
};
dba.set_data_table = function(win,full_id) {
    var grid = win.grid = win.attachGrid();
    grid.enableSmartRendering(true);
    grid._refresh = "./logic/php/datagrid.php?id="+encodeURIComponent(full_id);
    grid.load(grid._refresh);
};

function init() {

    dba.layout = new dhtmlXLayoutObject(document.body, "2U");
    dba.layout.cells("a").setText("Hierarchy");
    dba.layout.cells("a").setWidth(250);
    dba.layout.cells("b").setText("Details");

    dba.tree = dba.layout.cells("a").attachTree(0);
    dba.tree.setIconSize(18,18);
    dba.tree.setImagePath("./imgs/tree/");
    dba.tree.setXMLAutoLoadingBehaviour("function");
    dba.tree.setXMLAutoLoading(function(id) {
        dba.tree.load("./logic/php/tree.php?id="+id+"&full_id="+encodeURIComponent(dba.get_id_chain(dba.tree,unescape(id))));
    });

    dba.tree.load("./logic/php/tree.php");
    dba.tree.attachEvent("onClick",function(id) {
        if (id.split("^")[0] === "table") dba.create_tab(id);
        return true;
    });

    dba.toolbar = dba.layout.attachToolbar();
    dba.toolbar.setIconsPath("./imgs/");
    dba.toolbar.attachEvent("onClick",dba.main_toolbar_click);
    dba.toolbar.loadStruct("./xml/buttons.xml");

    dba.tabbar = dba.layout.cells("b").attachTabbar();
    dba.tabbar.addTab("start","Start","100");
    dba.tabbar.cells("start").attachHTMLString(dba.start_text); //dba.tabbar.setContentHTML("start",dba.start_text)
    dba.tabbar.cells("start").setActive(); //dba.tabbar.setTabActive("start");
    dba.tabbar.enableTabCloseButton(true);
    //dba.tabbar.setTabActive("start")

    dba.tabbar.attachEvent("onTabClose",function(id) {
        delete dba.pages[id];
        return true;
    });
}