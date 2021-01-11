#{{{ Marathon
require_fixture 'default'
#}}} Marathon

severity("normal")

def test

    with_window("VVS WebServer - [Stopped]") {
        click("..._2")
        select("file-chooser_0", "#H/vvs/WebServer/pages")
        assert_p("Maintenance directory", "Text", "C:\\Users\\unu\\vvs\\WebServer\\pages")
        click("Start Server")
    }

    with_window("VVS WebServer - [Running]") {
        click("..._2")
        select("file-chooser_0", "#H/vvs/WebServer/maintenance")
        assert_p("Maintenance directory", "Text", "C:\\Users\\unu\\vvs\\WebServer\\maintenance")
    }

    with_window("VVS WebServer - [Maintenance]") {
        select("Switch to maintenance mode", "true")
        assert_p("Maintenance directory", "Text", "C:\\Users\\unu\\vvs\\WebServer\\maintenance")
    }

    with_window("VVS WebServer - [Running]") {
        select("Switch to maintenance mode", "false")
        click("Stop Server")
    }

    with_window("VVS WebServer - [Stopped]") {
        window_closed("VVS WebServer - [Stopped]")
    }

end
