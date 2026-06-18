package io.innoq.calvin.booking.adapter.in.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloControllerTest {

    @Test
    void hello_gibtHelloWorldZurueck() {
        assertThat(new HelloController().hello()).isEqualTo("Hello World!");
    }
}
