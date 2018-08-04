var dba = {}; //namespace
dba.i18n = {
    select_db: "You need to select DB first"
};
dba.uid = function () {
    return (new Date()).valueOf();
}
dba.pages = {};// store opened tab
dba.create_tab = function (id, full_id, text, extra) {
    full_id = full_id || dba.get_id_chain(dba.tree, id);
    if (!dba.pages[full_id]) {
        var details = id.split("^");
        dba.tabbar.addTab(full_id, full_id, 100);
        var win = dba.tabbar.cells(full_id);
        //using window instead of tab
        var toolbar = win.attachToolbar();
        toolbar.attachEvent("onClick", dba.tab_toolbar_click);
        toolbar.setIconsPath("./imgs/");
        toolbar.loadStruct("xml/toolbar_" + details[0] + ".xml");
        dba.tabbar.cells(full_id).setActive();
        dba.tabbar.cells(full_id).setText(text || dba.tree.getItemText(id));
        switch (details[0]) {
            case "table":
                dba.set_data_table(win, full_id);
                break;
            case "query":
                dba.set_query_layout(win);
                break;
        }
        dba.pages[full_id] = win;
        win.extra = extra;
    }
    else {
        dba.tabbar.cells(full_id).setActive();
    }
};
dba.tab_toolbar_click = function (id) {
    switch (id) {
        case "close":
            var id = dba.tabbar.getActiveTab();
            delete dba.pages[id];
            dba.tabbar.tabs(id).close(true);
            break;
        case "run_query":
            var win = dba.tabbar.cells(dba.tabbar.getActiveTab());
            win.area.parentNode.removeChild(win.area);
            win.grid.post("./logic/php/sql.php", "id=" + encodeURIComponent(
                win.extra.join("|")) + "&sql=" + encodeURIComponent(
                win.area.value));
            break;
        case "refresh_table":
            var win = dba.tabbar.cells(dba.tabbar.getActiveTab());
            win.grid.load(win.grid._refresh);
            break;
        case "show_structure":
            var win = dba.tabbar.cells(dba.tabbar.getActiveTab());
            win.grid.load(win.grid._refresh + "&struct=true");
            break;
        default:
            dhtmlx.alert({
                title: "Information!",
                type: "alert-error",
                text: "Not implemented"
            });
            break;
    }
};
dba.get_id_chain = function (tree, id) {
    var chain = [id];
    while (id = tree.getParentId(id)) {
        chain.push(id);
    }
    return chain.reverse().join("|");
};
dba.delete_connection = function (server) {
    top.setTimeout(function () {
        dhx4.ajax.get("./logic/php/connection.php?mode=delete&server="
            + encodeURIComponent(server), function (xml) {
            eval(xml.xmlDoc.responseText);
        });
    }, 1);
};
var data = dba.get_id_chain(dba.tree, dba.tree.getSelectedItemId()).split("|");
if (data.length < 2) {
    dhtmlx.alert({
        title: "Information!",
        type: "alert-error",
        text: dba.i18n.select_db
    });
}
dba.create_tab("query", dba.uid(), "SQL : " + dba.tree.getItemText(data[0])
    + " : " + data[1].split("^")[1], data);
dba.set_query_layout = function (win) {
    var grid = win.grid = win.attachGrid();
    grid.enableSmartRendering(true);
    grid.setHeader(
        "<textarea style = 'width: 100%; height: 80px; '>Type SQL query here</textarea>")
    grid.setInitWidths("*");
    grid.init();
    grid.attachEvent("onXLE", function () {
        this.hdr.rows[1].cells[0].firstChild.appendChild(win.area);
        this.hdr.rows[1].cells[0].className = "grid_hdr_editable";
        this.setSizes();
        win.area.focus();
    });
    var area = grid.hdr.rows[1].cells[0];
    area.className = "grid_hdr_editable";
    area.onselectstart = function (e) {
        return ((e || event).cancelBubble = true);
    };
    area = area.firstChild.firstChild;
    area.focus();
    area.select();
    dhtmlxEvent(area, "keypress", function (e) {
        e = e || event;
        code = e.charCode || e.keyCode;
        if (e.ctrlKey && code === 13) {
            dba.tab_toolbar_click("run_query");
        }
    });
    win.area = area;
};
dba.set_data_table = function (win, full_id) {
    var grid = win.grid = win.attachGrid();
    grid.enableSmartRendering(true);
    grid._refresh = "./logic/php/datagrid.php?id=" + encodeURIComponent(
        full_id);
    grid.load(grid._refresh);
};

function init() {
    dba.tree.setXMLAutoLoadingBehaviour("function");
    dba.tree.setXMLAutoLoading(function (id) {
        dba.tree.load("./logic/php/tree.php?id=" + id + "&full_id="
            + encodeURIComponent(dba.get_id_chain(dba.tree, unescape(id))));
    });
    dba.tree.load("./logic/php/tree.php");
    dba.tree.attachEvent("onClick", function (id) {
        if (id.split("^")[0] === "table") {
            dba.create_tab(id);
        }
        return true;
    });
    dba.tabbar.attachEvent("onTabClose", function (id) {
        delete dba.pages[id];
        return true;
    });
}