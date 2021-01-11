#{{{ Marathon
require_fixture 'default'
#}}} Marathon

severity("normal")

def test

      with_window("VVS WebServer - [Stopped]") {
        select("Server listening on port", "ll")
        assert_p("Server listening on port", "Text", "ll")
        select("Server listening on port", "ll")
        assert_p("lbl:It is not a number", "Text", "It is not a number")
        select("Server listening on port", "-1")
        assert_p("Server listening on port", "Text", "-1")
        select("Server listening on port", "-1")
        assert_p("lbl:Out of range", "Text", "Out of range")
        select("Server listening on port", "8080")
        assert_p("Server listening on port", "Text", "8080")
        select("Server listening on port", "8080")
        click("...")
        select("file-chooser_0", "#H/vvs/WebServer/pages")
        assert_p("Web root directory", "Text", "C:\\Users\\unu\\vvs\\WebServer\\pages")
        click("Start Server")
    }

    with_window("VVS WebServer - [Running]") {
        click("Stop Server")
    }

    with_window("VVS WebServer - [Stopped]") {
        window_closed("VVS WebServer - [Stopped]")
    }

end
