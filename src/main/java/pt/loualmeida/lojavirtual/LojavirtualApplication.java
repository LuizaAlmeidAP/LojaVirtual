package pt.loualmeida.lojavirtual;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LojavirtualApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(LojavirtualApplication.class, args);
	}

}
