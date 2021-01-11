#{{{ Marathon
require_fixture 'default'
#}}} Marathon

severity("normal")

def test
       with_window("VVS WebServer - [Stopped]") {
        click("Start Server")
    }

    with_window("VVS WebServer - [Running]") {
        assert_p("Stop Server", "Text", "Stop Server")
    }

    with_window("VVS WebServer - [Maintenance]") {
        select("Switch to maintenance mode", "true")
        assert_p("Switch to maintenance mode", "Text", "true")
    }

    with_window("VVS WebServer - [Running]") {
        select("Switch to maintenance mode", "false")
        assert_p("Switch to maintenance mode", "Text", "false")
        click("Stop Server")
    }

    with_window("VVS WebServer - [Stopped]") {
        assert_p("Start Server", "Text", "Start Server")
        window_closed("VVS WebServer - [Stopped]")
    }


end
