package pe.com.practicar.expose.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Controller
public class HomeController {

    RouterFunction<ServerResponse> index() {
        return route(
                GET("/"),
                req -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui/index.html")).build()
        );
    }
}
