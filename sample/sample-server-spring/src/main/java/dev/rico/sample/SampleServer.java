package dev.rico.sample;

import dev.rico.server.RicoApplication;
import dev.rico.server.remoting.EnableRemoting;
import org.springframework.boot.SpringApplication;

@RicoApplication
@EnableRemoting
public class SampleServer {

    public static void main(String[] args) {
        SpringApplication.run(SampleServer.class, args);
    }

}
